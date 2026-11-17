/**
 * KBin Checker lookup service (Cloudflare Worker).
 *
 * The app calls:   GET https://<your-worker>/bin/516706
 * The Worker answers from its cache, or asks HandyAPI once and caches the result.
 * The HandyAPI key lives here as a secret and is never shipped inside the app.
 *
 * Required setup in the Cloudflare dashboard (see server/README.md):
 *   Secret      HANDY_API_KEY   your HandyAPI key
 *   KV binding  BIN_CACHE       a KV namespace used as the cache
 *
 * Abuse protection (no extra setup needed):
 *   - each IP address may make PER_IP_LIMIT lookups per minute
 *   - at most DAILY_UPSTREAM_LIMIT new BINs per day are sent to HandyAPI,
 *     so one bad day cannot use up the whole monthly quota
 * Optional: add a plain text variable DAILY_LIMIT to change the daily number,
 * and a "Rate limiter" binding named RATE_LIMITER for a stricter per-IP limit.
 */

const UPSTREAM = "https://data.handyapi.com/bin/";
const FOUND_TTL = 30 * 24 * 60 * 60;   // keep found BINs for 30 days
const NOT_FOUND_TTL = 6 * 60 * 60;     // keep "not found" for 6 hours

const PER_IP_LIMIT = 30;               // lookups per IP address per minute
const DAILY_UPSTREAM_LIMIT = 100;      // new BINs sent to HandyAPI per day (UTC)

// Per-IP counters live in memory only. They are never stored or logged.
const ipHits = new Map();

function overIpLimit(ip, now) {
  const windowStart = Math.floor(now / 60000);
  const entry = ipHits.get(ip);
  if (!entry || entry.w !== windowStart) {
    if (ipHits.size > 5000) ipHits.clear();
    ipHits.set(ip, { w: windowStart, n: 1 });
    return false;
  }
  entry.n += 1;
  return entry.n > PER_IP_LIMIT;
}

function dayKey(now) {
  return "__upstream_" + new Date(now).toISOString().slice(0, 10);
}

function reply(body, status = 200) {
  return new Response(typeof body === "string" ? body : JSON.stringify(body), {
    status,
    headers: {
      "content-type": "application/json; charset=utf-8",
      "cache-control": "no-store",
      "x-content-type-options": "nosniff",
    },
  });
}

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (request.method === "GET" && url.pathname === "/") {
      return reply({ service: "KBin Checker lookup", status: "ok" });
    }

    // Only 6 to 8 digits are accepted. Anything longer is rejected, never stored.
    const match = url.pathname.match(/^\/(?:bin\/)?(\d{6,8})$/);
    if (request.method !== "GET" || !match) {
      return reply({ Status: "INVALID" }, 400);
    }
    const bin = match[1];

    if (!env.BIN_CACHE || !env.HANDY_API_KEY) {
      return reply({ Status: "NOT CONFIGURED" }, 500);
    }

    // Per-IP limit: stops one device or script from flooding the service.
    const now = Date.now();
    const ip = request.headers.get("cf-connecting-ip") || "unknown";
    if (overIpLimit(ip, now)) return reply({ Status: "LIMIT" }, 429);
    if (env.RATE_LIMITER) {
      try {
        const result = await env.RATE_LIMITER.limit({ key: ip });
        if (result && result.success === false) return reply({ Status: "LIMIT" }, 429);
      } catch (e) {
        // The optional binding must never break lookups.
      }
    }

    const cached = await env.BIN_CACHE.get(bin);
    if (cached) return reply(cached);

    // Daily cap on provider calls. Cached BINs above are not affected.
    const dailyLimit = parseInt(env.DAILY_LIMIT, 10) > 0 ? parseInt(env.DAILY_LIMIT, 10) : DAILY_UPSTREAM_LIMIT;
    const counterKey = dayKey(now);
    let usedToday = 0;
    try {
      usedToday = parseInt(await env.BIN_CACHE.get(counterKey), 10) || 0;
    } catch (e) {
      usedToday = 0;
    }
    if (usedToday >= dailyLimit) return reply({ Status: "LIMIT" }, 429);
    try {
      await env.BIN_CACHE.put(counterKey, String(usedToday + 1), { expirationTtl: 2 * 24 * 60 * 60 });
    } catch (e) {
      // Counting is best effort.
    }

    let upstream;
    try {
      upstream = await fetch(UPSTREAM + bin, {
        headers: { "x-api-key": env.HANDY_API_KEY, accept: "application/json" },
      });
    } catch (e) {
      return reply({ Status: "UPSTREAM ERROR" }, 502);
    }
    if (upstream.status === 429) return reply({ Status: "LIMIT" }, 429);
    if (upstream.status === 401 || upstream.status === 403) {
      return reply({ Status: "UPSTREAM AUTH" }, 502);
    }
    if (!upstream.ok && upstream.status !== 404) {
      return reply({ Status: "UPSTREAM ERROR" }, 502);
    }

    let data = null;
    try {
      data = await upstream.json();
    } catch (e) {
      data = null;
    }
    const status = String((data && data.Status) || "").toUpperCase();
    if (status.includes("LIMIT")) return reply({ Status: "LIMIT" }, 429);

    const found = upstream.ok && status === "SUCCESS";
    // Keep only the fields the app uses.
    const body = JSON.stringify(
      found
        ? {
            Status: "SUCCESS",
            Scheme: data.Scheme || "",
            Type: data.Type || "",
            Issuer: data.Issuer || "",
            CardTier: data.CardTier || "",
            Country: {
              A2: (data.Country && data.Country.A2) || "",
              Name: (data.Country && data.Country.Name) || "",
            },
          }
        : { Status: "NOT FOUND" }
    );

    try {
      await env.BIN_CACHE.put(bin, body, {
        expirationTtl: found ? FOUND_TTL : NOT_FOUND_TTL,
      });
    } catch (e) {
      // Cache is best effort (for example when the daily free write quota is used up).
    }
    return reply(body);
  },
};
