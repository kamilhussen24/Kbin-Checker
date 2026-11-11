# KBin Checker lookup service (Cloudflare Worker)

A small relay between the app and the BIN data provider. It keeps the provider key out of the
app and caches every result, so each BIN costs one provider request no matter how many users
look it up.

```
App  ->  https://<your-worker>/bin/516706  ->  cache (KV)  ->  HandyAPI (only on a cache miss)
```

Everything below can be done in the Cloudflare dashboard from a phone browser.

## 1. Create the cache

1. Dashboard > **Storage & Databases** > **Workers KV** > **Create instance**.
2. Name it `kbin-cache` and create it.

## 2. Create the Worker

1. Dashboard > **Workers & Pages** > **Create application** > **Start with Hello World!**.
2. Name it `kbin` and select **Deploy**.
3. Open the Worker, select **Edit code**, delete everything in the editor, paste the full content
   of `cloudflare-worker.js`, and select **Deploy**.

## 3. Connect the cache and the key

1. Worker > **Settings** > **Bindings** > **Add** > **KV namespace**.
   Variable name: `BIN_CACHE`. Namespace: `kbin-cache`. Deploy.
2. Worker > **Settings** > **Variables and Secrets** > **Add**.
   Type: **Secret**. Name: `HANDY_API_KEY`. Value: your HandyAPI key. Deploy.

## 4. Test

Open these in a browser (replace the address with your Worker's address, shown on its overview
page, for example `https://kbin.your-name.workers.dev`):

- `https://kbin.your-name.workers.dev/` shows `{"service":"KBin Checker lookup","status":"ok"}`
- `https://kbin.your-name.workers.dev/bin/516706` shows the BIN record

| Response | Meaning |
|----------|---------|
| `"Status":"SUCCESS"` | Working |
| `"Status":"NOT CONFIGURED"` | The `BIN_CACHE` binding or `HANDY_API_KEY` secret is missing |
| `"Status":"UPSTREAM AUTH"` | The HandyAPI key is wrong or disabled |
| `"Status":"LIMIT"` | The HandyAPI quota is used up |

## 5. Point the app at the Worker

In the GitHub repository, add a secret (Settings > Secrets and variables > Actions):

| Secret | Value |
|--------|-------|
| `BIN_API_URL` | `https://kbin.your-name.workers.dev/bin/` |

Remove the `BIN_API_KEY` secret if you added it earlier; the key now lives in the Worker.
Run the build workflow again. The new build talks to your Worker.

## Notes

- The `workers.dev` address belongs to your Cloudflare account, not to a domain, so it keeps
  working when you change domains. Do not change the Worker name or the account subdomain after
  the app is published, because the address is built into the app.
- Free plan limits at the time of writing: 100,000 Worker requests per day, 100,000 cache reads
  per day, 1,000 cache writes per day. Each newly seen BIN uses one write.
- The Worker stores results by BIN only. It does not log who asked.
- To change provider later, edit only this Worker. The app does not need an update as long as the
  Worker returns the same JSON fields.
