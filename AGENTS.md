# Repository Guidelines

## Project Snapshot

This repository is currently a single-module native Android local Beta for an I Ching app. The app is implemented with Java, AndroidX Fragment, Material/AppCompat dependencies, XML resources, and programmatic Android views. It does not use WebView, Jetpack Compose, a backend, or network-loaded production UI assets.

The current Beta implements a local high-fidelity flow based on the Stitch exports in `design/stitch_export/`: splash, onboarding, login/register shell, daily insight, three-step divination, divination result with original hexagram / changing lines / relating hexagram, searchable and filterable records, learning center, hexagram detail with six line texts, profile/settings, and dark-mode daily styling. Authentication is only a UI state; local mode is the real operating mode.

Core data is local-first and deterministic: `HexagramRepository` stores all 64 King Wen hexagrams with explicit upper/lower `Trigram`, bottom-to-top six-line patterns, judgments, six line texts, tags, summaries, and action hints. Hexagram lookup must use the explicit pattern map; do not reintroduce ordinal-bit or modulo fallback logic except the intentional invalid-input fallback to hexagram 15.

## Project Structure & Module Organization

- `app/` contains the Android app module.
- `app/src/main/java/fcu/app/i_ching/` contains app code.
- `app/src/main/java/fcu/app/i_ching/data/` contains local models, static hexagram data, casting and relating-hexagram logic, settings persistence, record persistence, and record filtering helpers.
- `app/src/main/java/fcu/app/i_ching/ui/` contains Fragment screens and the shared programmatic UI helper `Ui`.
- `app/src/main/res/layout/activity_main.xml` hosts the `FragmentContainerView`; most Beta screens are programmatic Java views.
- `app/src/main/res/values/` and `app/src/main/res/values-night/` contain the current design tokens for colors, themes, and spacing.
- `app/src/main/res/xml/backup_rules.xml` and `app/src/main/res/xml/data_extraction_rules.xml` define backup behavior. Divination records are excluded from cloud backup because questions and notes may be sensitive.
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

Current known verification state after the local Beta core implementation:

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
- Keep divination consistency in the data layer: derive `Hexagram` and `relatingHexagram` from the same line values / changing-line helpers in `HexagramRepository`.
- Keep JSON persistence backward-compatible. New fields such as `relatingHexagramNumber` should be optional on read and derived from `lineValues` when old records do not contain them.
- Keep record search/filter logic in `LocalRecordStore` or another data helper; UI should not duplicate matching rules for question, note, hexagram name, full name, or tags.

## Testing Guidelines

Use JUnit 4 for local unit tests and AndroidX Test/Espresso for instrumentation tests. Name test classes after the class or feature under test, ending with `Test`, and prefer descriptive test methods such as `coinLineValuesUseTraditionalBuckets`.

Current JVM test coverage includes:

- `IChingLogicTest` covers casting line-value buckets, known hexagram pattern mappings, 64-pattern uniqueness, simple-cast consistency, changing-line flips, and relating hexagrams.
- `DivinationPersistenceTest` covers `DivinationResult` / `DivinationRecord` JSON round trips, backward-compatible old JSON fallback, relating-hexagram derivation from old `lineValues`, and record mutation helpers.
- `HexagramRepositoryFilterTest` covers learning-center search and canon/favorite filters.
- `LocalRecordStoreFilterTest` covers record search by question, note, hexagram, and tag, plus method and changing-line filters.
- `ExampleInstrumentedTest` only checks app package context.

Add local tests for pure Java logic such as casting, repository mapping, relating-hexagram behavior, serialization, filtering, and backup-sensitive persistence decisions. Add instrumentation tests for Fragment routing, onboarding, dark-mode toggles, records persistence/search/filtering, favorites, sharing intent, and UI workflows.

## Documentation Guidelines

When changing behavior, update documentation in the same change. At minimum:

- Update `README.md` when build steps, app scope, or user-facing behavior changes.
- Update `docs/PROJECT_STATUS.md` when current capabilities, limitations, verification status, or future roadmap changes.
- Update this `AGENTS.md` when repository conventions or contributor expectations change.

Documentation should explicitly distinguish between implemented Beta behavior, design intent, known limitations, and future work.

## Commit & Pull Request Guidelines

The existing history uses short, imperative commit messages such as `init commit` and `Add stitch design docs`. Keep commits focused and use concise subjects that describe the change. Pull requests should include a brief summary, testing performed, linked issues when applicable, and screenshots or screen recordings for visible UI changes. Mention any Gradle, SDK, resource, or persistence changes that affect contributors or release builds.

## Security & Configuration Tips

Do not commit local machine configuration such as `local.properties` contents or signing secrets. Keep dependency versions centralized in `gradle/libs.versions.toml`, and prefer repository-wide Gradle settings over per-developer overrides. The current app stores settings, favorites, and divination records in app-private `SharedPreferences`; this is suitable for local Beta behavior but not a replacement for encrypted storage, account sync, user-controlled export/delete flows, or privacy controls in a production app. Keep `i_ching_records.xml` excluded from cloud backup unless a future privacy review deliberately changes the backup model.
