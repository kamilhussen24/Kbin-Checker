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
 */

const UPSTREAM = "https://data.handyapi.com/bin/";
const FOUND_TTL = 30 * 24 * 60 * 60;   // keep found BINs for 30 days
const NOT_FOUND_TTL = 6 * 60 * 60;     // keep "not found" for 6 hours

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

    const cached = await env.BIN_CACHE.get(bin);
    if (cached) return reply(cached);

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
