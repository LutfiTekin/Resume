
## Getting started

1. **Clone & open** — Import the project into Android Studio Iguana+ (or newer) with JDK 17 support.
2. **Add your API key** — Obtain an OpenRouter key and set `OPENAI_API_KEY` in `local.properties`, an environment variable, or `gradle.properties`. Without a key the build falls back to mock mode and hides the analyzer.
3. **Build & run** — Use Android Studio’s *Run* action or execute `./gradlew :app:installDebug` on a device/emulator running Android 8.1 (API 27) or later.

[For those about to apply](https://medium.com/@luetfitekin/for-those-about-to-apply-i-salute-you-0bb8b9c42a81)

## CV website

The localized JSON files in `docs/` are the source of truth. Generate the static GitHub Pages HTML after changing CV, language, or stack data:

```sh
npm run build:site
```

Do not edit `docs/index.html`, `docs/de/index.html`, or `docs/tr/index.html` directly. CI runs `npm run check:site` to verify that every generated page is current and that published routes and Android/PDF compatibility contracts still work. `npm run check:pdf` validates PDF inputs without requiring Pandoc or TeX. Keep the `Validate static CV site / verify` check required on `main`; legacy GitHub Pages deployments are not gated by post-push validation.

