# Repository Guidelines

## Project Snapshot

This repository is currently a single-module native Android MVP for an I Ching app. The app is implemented with Java, AndroidX Fragment, Material/AppCompat dependencies, XML resources, and programmatic Android views. It does not use WebView, Jetpack Compose, a backend, or network-loaded production UI assets.

The current MVP implements a local high-fidelity flow based on the Stitch exports in `design/stitch_export/`: splash, onboarding, login/register shell, daily insight, three-step divination, divination result, empty/filled records, learning center, hexagram detail, profile/settings, and dark-mode daily styling. Authentication is only a UI state; local mode is the real operating mode.

## Project Structure & Module Organization

- `app/` contains the Android app module.
- `app/src/main/java/fcu/app/i_ching/` contains app code.
- `app/src/main/java/fcu/app/i_ching/data/` contains local models, static hexagram data, casting logic, settings persistence, and record persistence.
- `app/src/main/java/fcu/app/i_ching/ui/` contains Fragment screens and the shared programmatic UI helper `Ui`.
- `app/src/main/res/layout/activity_main.xml` hosts the `FragmentContainerView`; most MVP screens are programmatic Java views.
- `app/src/main/res/values/` and `app/src/main/res/values-night/` contain the current design tokens for colors, themes, and spacing.
- `app/src/test/java/` contains JVM tests for pure Java logic.
- `app/src/androidTest/java/` contains instrumentation tests; currently only the generated app-context smoke test exists.
- `design/stitch_export/` contains design references only. Treat these HTML files and screenshots as reference material, not production Android UI code.
- `docs/` contains project documentation. Keep it updated when app architecture, behavior, test status, or known gaps change.

## Build, Test, and Development Commands

Use the Gradle wrapper from the repository root:

- `./gradlew assembleDebug` builds a debug APK for local validation.
- `./gradlew testDebugUnitTest` runs host-side JUnit tests in `app/src/test`.
- `./gradlew connectedDebugAndroidTest` runs instrumentation tests on a connected device or emulator.
- `./gradlew lintDebug` runs Android lint checks for the debug variant.
- `./gradlew clean` removes generated build outputs when caches or stale artifacts interfere.

Current known verification state after the MVP implementation:

- `./gradlew assembleDebug` passes.
- `./gradlew testDebugUnitTest` passes.
- `./gradlew lintDebug` passes.
- `./gradlew connectedDebugAndroidTest` builds the test APK but cannot run without an attached device or emulator.

## Coding Style & Naming Conventions

The app is Java-based and targets Java 11. Follow standard Android Java formatting with 4-space indentation, braces on the same line, and clear class names in `UpperCamelCase`. Methods and fields should use `lowerCamelCase`. Resource names should use lowercase snake case, for example `activity_main.xml`, `ic_launcher_foreground.xml`, and `backup_rules.xml`. Keep package names under `fcu.app.i_ching` unless the app namespace changes deliberately.

Prefer existing local patterns before adding new ones:

- Add pure app logic under `data/`.
- Add screens under `ui/` as Fragments.
- Reuse `Ui` for shared visual primitives unless replacing the programmatic UI approach with XML/Compose is an explicit project decision.
- Keep user-facing copy in Traditional Chinese unless there is a product reason to do otherwise.
- Keep all production behavior local-first until a backend/API integration is explicitly scoped.

## Testing Guidelines

Use JUnit 4 for local unit tests and AndroidX Test/Espresso for instrumentation tests. Name test classes after the class or feature under test, ending with `Test`, and prefer descriptive test methods such as `coinLineValuesUseTraditionalBuckets`.

Current test coverage is intentionally narrow:

- `IChingLogicTest` covers casting line-value buckets, the design-critical hexagram 15 line mapping, and repository size/content smoke checks.
- `ExampleInstrumentedTest` only checks app package context.

Add local tests for pure Java logic such as casting, repository mapping, and serialization. Add instrumentation tests for Fragment routing, onboarding, dark-mode toggles, records persistence, favorites, and UI workflows.

## Documentation Guidelines

When changing behavior, update documentation in the same change. At minimum:

- Update `README.md` when build steps, app scope, or user-facing behavior changes.
- Update `docs/PROJECT_STATUS.md` when current capabilities, limitations, verification status, or future roadmap changes.
- Update this `AGENTS.md` when repository conventions or contributor expectations change.

Documentation should explicitly distinguish between implemented MVP behavior, design intent, known limitations, and future work.

## Commit & Pull Request Guidelines

The existing history uses short, imperative commit messages such as `init commit` and `Add stitch design docs`. Keep commits focused and use concise subjects that describe the change. Pull requests should include a brief summary, testing performed, linked issues when applicable, and screenshots or screen recordings for visible UI changes. Mention any Gradle, SDK, resource, or persistence changes that affect contributors or release builds.

## Security & Configuration Tips

Do not commit local machine configuration such as `local.properties` contents or signing secrets. Keep dependency versions centralized in `gradle/libs.versions.toml`, and prefer repository-wide Gradle settings over per-developer overrides. The current app stores MVP settings, favorites, and divination records in app-private `SharedPreferences`; this is suitable for local MVP behavior but not a replacement for encrypted storage, account sync, backup policy review, or privacy controls in a production app.
