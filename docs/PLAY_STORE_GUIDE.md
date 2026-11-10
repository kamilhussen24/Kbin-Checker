# Google Play publishing guide for KBin Checker

Everything here can be done from a phone browser in Play Console.

## 1. What is already handled in the app

| Requirement | Status |
|-------------|--------|
| Target API level 36 (required for new apps since 31 Aug 2026) | Done |
| Android App Bundle (.aab) | Built by the `release` workflow |
| Edge-to-edge layout, predictive back | Done |
| 16 KB page size | Not affected, the app has no native code |
| Permissions | Internet only |
| Adaptive and themed launcher icon | Done |
| No full card number, expiry or CVV input | Input is capped at 8 digits |
| No bank or network logos | Text badges only, to avoid trademark issues |
| In-app privacy policy and disclaimer | About > Privacy policy |

## 2. Things you must do yourself

1. **Privacy policy URL.** Host `docs/privacy-policy.html` (for example at
   `https://kamildex.com/kbin-checker/privacy`) and add your contact email inside it.
2. **Contact details.** Play Console requires a public support email for the listing.
3. **Screenshots.** At least 2 phone screenshots. Install the debug APK and capture Home,
   a result, History and About. Use the light theme for consistency.
4. **Testing rule for new personal accounts.** Personal developer accounts created after
   13 Nov 2023 must run a closed test with at least 12 testers for 14 days before production.
   If your account is older or is an organization account, this does not apply.

## 3. Store listing text

**App name** (30 characters max)

```
KBin Checker - BIN Lookup
```

**Short description** (80 characters max)

```
Look up any card BIN: issuing bank, network, card type and country in seconds.
```

**Full description**

```
KBin Checker is a fast, minimal BIN lookup tool for developers, payment teams, merchants and anyone who works with card payments.

Enter the first 6 to 8 digits of a payment card (the Bank Identification Number, also called IIN) and instantly see:

• Issuing bank
• Card network
• Card type: credit, debit or prepaid
• Card level
• Issuing country and currency

WHY KBIN CHECKER
• Clean, light interface with light and dark themes
• Network detection while you type
• History with search and favorites, stored only on your device
• Recent lookups open instantly, even offline
• Copy or share any result
• No account, no ads, no tracking

PRIVACY FIRST
KBin Checker only needs the first 6 to 8 digits. It never asks for a full card number, expiry date, CVV or cardholder name, and the input field does not accept more than 8 digits. Your history never leaves your device.

GOOD TO KNOW
BIN data comes from a third party database and may be incomplete or out of date. KBin Checker is an informational tool and is not affiliated with any card network or bank. A BIN identifies the issuer of a card range, not a person or an account.
```

**Category:** Tools (Finance is also acceptable, but it triggers more review questions)
**Tags:** Tools, Utilities
**Graphics:** `docs/store/play-icon-512.png`, `docs/store/feature-graphic-1024x500.png`

## 4. App content answers (suggested)

These are suggestions based on how the app works. Read each Play Console question and answer
truthfully for the version you publish.

| Section | Suggested answer |
|---------|------------------|
| Privacy policy | Your hosted URL |
| App access | All functionality is available without special access |
| Ads | No, the app does not contain ads |
| Content rating | Utility / reference app, no user generated content, no violence, no gambling. Expect "Everyone" |
| Target audience | 18 and over (simplest: no children's policy requirements) |
| News app | No |
| Government app | No |
| Financial features | "My app doesn't provide any financial features" |
| Health | None |

### Data safety

How the app actually behaves:

- The 6 to 8 digit BIN is sent over HTTPS to the data provider (HandyAPI) to get the result.
  It is not linked to a user, account or device ID and the app does not store it on any server.
- History, favorites and settings stay on the device.
- No analytics, ads or crash reporting SDKs.

A BIN is a public issuer prefix, not a card number, and the request is processed only to return
the result. On that basis the usual declaration is:

- Does your app collect or share any of the required user data types? **No**

If you later add analytics, ads, crash reporting or accounts, this answer must change. If you are
unsure how Google classifies a specific item, choose the more cautious answer.

## 5. Release steps

1. Run **Build KBin Checker** with `release` and download the `.aab` artifact.
2. Play Console > Create app > fill in the listing and App content sections above.
3. Testing > Internal testing > create a release, upload the `.aab`, add yourself as a tester
   and install from the test link.
4. When it looks right, promote to Closed testing (if required for your account) and then Production.
5. Accept **Play App Signing** when asked. Your GitHub keystore then acts as the upload key.

For every update: change `versionName` in `app/build.gradle.kts`, run `release` again and upload
the new `.aab`. The version code increases automatically.

## 6. Policy notes for this kind of app

- Keep the wording about "checking" neutral. Do not describe the app as validating, generating
  or testing card numbers. Card generators and "live card checkers" are removed from Google Play.
- Do not add full card number fields, card generators or bulk checking features.
- Do not use card network or bank logos in the app, icon or screenshots.
