/**
 * KBin Checker privacy policy page (Cloudflare Worker).
 *
 * Serves one static page. It has no bindings, no secrets and makes no outside requests.
 * Attach a custom domain (for example kbin.kamildex.com) under Settings > Domains & Routes,
 * then use https://<that-domain>/privacy as the privacy policy URL in Play Console.
 *
 * Keep the text in sync with docs/privacy-policy.html and the in-app privacy screen.
 */

const PRIVACY_HTML = `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>KBin Checker Privacy Policy</title>
<style>
  body { font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif; line-height: 1.6;
         color: #141214; background: #fbf9f8; margin: 0; padding: 32px 20px; }
  main { max-width: 720px; margin: 0 auto; background: #fff; border: 1px solid #ece8e8;
         border-radius: 24px; padding: 32px 28px; }
  h1 { font-size: 28px; margin: 0 0 4px; letter-spacing: -0.5px; }
  h2 { font-size: 18px; margin: 28px 0 8px; }
  p, li { color: #4a4648; }
  .meta { color: #6b6669; font-size: 14px; }
  a { color: #d4285f; }
</style>
</head>
<body>
<main>
  <h1>KBin Checker Privacy Policy</h1>
  <p class="meta">Last updated: 27 November 2026</p>

  <h2>Summary</h2>
  <p>KBin Checker has no accounts, no ads and no analytics. It does not ask for, store or transmit
  full card numbers, expiry dates, CVV codes or cardholder names.</p>

  <h2>What the app sends</h2>
  <p>When you tap Check BIN, the 6 to 8 digits you entered are sent over an encrypted (HTTPS)
  connection to our lookup service, which is hosted on Cloudflare and retrieves the public issuer
  record from our BIN data provider, HandyAPI (<a href="https://www.handyapi.com">handyapi.com</a>).
  As with any internet request, these services can see your IP address. The request contains no name,
  device identifier or other personal information, and results are cached by BIN only, with no link
  to who asked. To prevent abuse, the lookup service also keeps a one-way hash of your IP address (not
  the address itself) for up to two days to count daily requests.</p>

  <h2>What stays on your device</h2>
  <p>Your lookup history, favorites and settings are stored only in the app's private storage on
  your device. They are never uploaded. You can switch history off or clear it in Settings, and
  uninstalling the app removes everything.</p>

  <h2>Permissions</h2>
  <p>The app uses only the Internet permission, which is required to perform lookups.</p>

  <h2>Children</h2>
  <p>KBin Checker is a general reference tool and is not directed at children.</p>

  <h2>Changes</h2>
  <p>If this policy changes, the updated version will be published on this page and in the app.</p>

  <h2>Contact</h2>
  <p>Questions about this policy: <a href="mailto:contact@kamildex.com">contact@kamildex.com</a><br>
  Website: <a href="https://kamildex.com">kamildex.com</a></p>
</main>
</body>
</html>`;

export default {
  async fetch(request) {
    const url = new URL(request.url);
    if (request.method !== "GET" && request.method !== "HEAD") {
      return new Response("Method not allowed", { status: 405 });
    }
    if (url.pathname === "/") {
      return Response.redirect(url.origin + "/privacy", 302);
    }
    if (url.pathname === "/privacy" || url.pathname === "/privacy/") {
      return new Response(PRIVACY_HTML, {
        headers: {
          "content-type": "text/html; charset=utf-8",
          "cache-control": "public, max-age=3600",
          "x-content-type-options": "nosniff",
        },
      });
    }
    return new Response("Not found", { status: 404 });
  },
};
