# KBin Checker

A light, minimal Android app that looks up a card BIN (the first 6 to 8 digits of a payment card)
and shows the issuing bank, network, card type, level, country and currency.

- Package: `com.kamildex.kbinchecker`
- Min Android: 7.0 (API 24), target: Android 16 (API 36)
- UI: Jetpack Compose, Material 3, light and dark themes
- No accounts, no ads, no analytics, one permission (Internet)
- Built entirely with GitHub Actions. No Android Studio or computer needed.

## Features

- BIN lookup with live network detection while typing
- Result card with bank, country, type and level; copy, share and favorite
- Recent checks on the home screen, full history with search and favorites
- Offline cache: a BIN checked in the last 30 days opens instantly without a network call
- Settings: System / Light / Dark theme, history on or off, clear history
- About screen with a built-in privacy policy
- Input is limited to 8 digits, so a full card number can never be typed or pasted

## Project layout

```
app/src/main/java/com/kamildex/kbinchecker/
  AppConfig.kt            website, contact email, API endpoint, cache settings
  MainActivity.kt
  data/                   network detection, API client, history and settings storage
  ui/                     view model, navigation shell, theme, components, screens
.github/workflows/
  build.yml               debug / release / both
  keystore.yml            one-time release key generator
docs/
  PLAY_STORE_GUIDE.md     listing text and Play Console answers
  privacy-policy.html     page to host for the Play Store privacy policy URL
  store/                  512 px icon and 1024x500 feature graphic
```

## Build with GitHub Actions

1. Create a **private** GitHub repository and upload every file and folder from this project
   (including the hidden `.github` folder).
2. Open **Actions > Build KBin Checker > Run workflow** and choose:
   - `debug`: test APK, no secrets needed
   - `release`: signed APK and AAB (needs the secrets below)
   - `both`: everything
3. Download the result from the run's **Artifacts** section. Release builds are also attached
   to a GitHub Release tagged `v{versionName}-{run number}`.

The debug app installs as a separate app (`com.kamildex.kbinchecker.debug`), so it can sit next to
the release version. All debug builds share one key, so a new debug APK installs over the old one.

### Release signing

Add these under **Settings > Secrets and variables > Actions**:

| Secret | Value |
|--------|-------|
| `STORE_PASSWORD` | keystore password, at least 6 characters |
| `KEY_PASSWORD` | key password, at least 6 characters |
| `KEY_ALIAS` | key name, for example `kbin` |
| `KEYSTORE_BASE64` | the keystore file encoded as base64 |
| `BIN_API_KEY` | optional, your HandyAPI key |

No keystore yet? Add the first three secrets, then run **Actions > Generate Release Keystore**
(type `YES` to confirm). Download its artifact, copy the content of `keystore_base64.txt` into the
`KEYSTORE_BASE64` secret, keep the zip as your backup, and delete that workflow run.

Never generate a second key after the app is on Google Play. Updates must be signed with the same key.

### Versions

- `versionName` is set in `app/build.gradle.kts`. Change it for each public release (1.0.0, 1.0.1, ...).
- `versionCode` is the GitHub run number, so every build is automatically higher than the last one.

## BIN data provider

Lookups use HandyAPI (`https://data.handyapi.com/bin/{bin}`). The app works without a key within
the provider's free limits. For a published app, create a free HandyAPI account and add the key as
the `BIN_API_KEY` secret so the build includes it. A key shipped inside an app can be extracted,
so use a key that is meant for client apps and keep an eye on its usage.

## Before publishing

See [docs/PLAY_STORE_GUIDE.md](docs/PLAY_STORE_GUIDE.md) for the full checklist. The short version:

1. Set `CONTACT_EMAIL` in `AppConfig.kt` (optional) and check `WEBSITE_URL`.
2. Host `docs/privacy-policy.html` and use its URL in Play Console.
3. Run a `release` build and upload the `.aab` file.
