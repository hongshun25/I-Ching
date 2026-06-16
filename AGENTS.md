# Repository Guidelines

## Project Snapshot

This repository is currently a single-module native Android local Beta for an I Ching app. The app is implemented with Java, AndroidX Fragment, Navigation Component, Room, Material/AppCompat dependencies, XML resources/ViewBinding, shared XML components, and focused custom views. It does not use WebView, Jetpack Compose, a backend, or network-loaded production UI assets.

The current Beta implements a local high-fidelity flow based on the Stitch exports in `design/stitch_export/`: splash, onboarding, local-mode entry, daily insight, three-step divination, divination result with original hexagram / changing lines / relating hexagram, searchable and filterable records, learning center, hexagram detail with six line texts, profile/settings, data export/delete controls, and dark-mode daily styling. Authentication is not implemented; local mode is the real operating mode.

Core data is local-first and deterministic: `HexagramRepository` stores all 64 King Wen hexagrams with explicit upper/lower `Trigram`, bottom-to-top six-line patterns, judgments, six line texts, tags, summaries, and action hints. Hexagram lookup must use the explicit pattern map; do not reintroduce ordinal-bit or modulo fallback logic except the intentional invalid-input fallback to hexagram 15.

## Project Structure & Module Organization

- `app/` contains the Android app module.
- `app/src/main/java/fcu/app/i_ching/` contains app code.
- `app/src/main/java/fcu/app/i_ching/data/` contains local models, static hexagram data, casting and relating-hexagram logic, settings persistence, Room record persistence, legacy record migration/export helpers, and record filtering helpers.
- `app/src/main/java/fcu/app/i_ching/ui/` contains Fragment screens, ViewModels, presentation helpers, `NavigationChrome`, and custom views such as `HexagramView`. The old transitional `Ui` helper has been removed; do not reintroduce it.
- `app/src/main/res/layout/activity_main.xml` hosts the default `NavHostFragment`; splash, onboarding, local entry, daily, records, learn, result, profile/settings, top bar, bottom nav, empty state, record item, and settings row now use XML/ViewBinding components.
- `app/src/main/res/navigation/main_graph.xml` defines the app navigation graph. Use simple Bundle/JSON arguments for now; Safe Args is not enabled.
- `app/src/main/res/values/` and `app/src/main/res/values-night/` contain the current design tokens for colors, themes, typography, and spacing.
- `app/src/main/res/font/`, `app/src/main/res/drawable/`, `app/src/main/res/drawable-nodpi/`, `app/src/main/res/color/`, and `app/src/main/res/menu/` contain committed production fonts, vectors, bitmap assets, selectors, and menus. New production assets must be tracked in `tools/assets/asset_manifest.json`.
- `app/src/main/res/xml/backup_rules.xml` and `app/src/main/res/xml/data_extraction_rules.xml` define backup behavior. Divination records are excluded from cloud backup because questions and notes may be sensitive.
- `app/src/test/java/` contains JVM tests for pure Java logic.
- `app/src/androidTest/java/` contains instrumentation tests, including app-context and Room DAO coverage.
- `design/stitch_export/` contains design references only. Treat these HTML files and screenshots as reference material, not production Android UI code.
- `tools/assets/asset_manifest.json` records production asset source URLs, licenses, transforms, target paths, and checksums.
- `docs/` contains project documentation. Keep it updated when app architecture, behavior, asset policy, test status, or known gaps change.

## Build, Test, and Development Commands

Use the Gradle wrapper from the repository root:

- `./gradlew assembleDebug` builds a debug APK for local validation.
- `./gradlew testDebugUnitTest` runs host-side JUnit tests in `app/src/test`.
- `./gradlew connectedDebugAndroidTest` runs instrumentation tests on a connected device or emulator.
- `./gradlew lintDebug` runs Android lint checks for the debug variant.
- `./gradlew clean` removes generated build outputs when caches or stale artifacts interfere.
- `./gradlew assembleDebugAndroidTest` builds the debug instrumentation APK.
- `./gradlew pixel2Api35DebugAndroidTest` runs instrumentation tests on the configured Gradle managed device when the local Android SDK has the required emulator/system image installed.

Current known verification state after the UI debt / asset pipeline refactor:

- `./gradlew testDebugUnitTest` passes.
- `./gradlew lintDebug` passes.
- `./gradlew assembleDebug` passes.
- `./gradlew assembleDebugAndroidTest` passes.
- `./gradlew pixel2Api35DebugAndroidTest` passes 15/15 in an environment with the managed-device system image installed. AGP 9.2 may still print a `testedAbi` setup warning.
- `./gradlew connectedDebugAndroidTest` builds the app/test APK but cannot run without an attached device or emulator; the current environment reports `No connected devices!`.
- `./gradlew pixel2Api35DebugAndroidTest` is the preferred no-physical-device instrumentation command when managed-device prerequisites are installed.

## Coding Style & Naming Conventions

The app is Java-based and targets Java 11. Follow standard Android Java formatting with 4-space indentation, braces on the same line, and clear class names in `UpperCamelCase`. Methods and fields should use `lowerCamelCase`. Resource names should use lowercase snake case, for example `activity_main.xml`, `main_graph.xml`, `ic_launcher_foreground.xml`, and `backup_rules.xml`. Keep package names under `fcu.app.i_ching` unless the app namespace changes deliberately.

Prefer existing local patterns before adding new ones:

- Add pure app logic under `data/`.
- Add screens and screen-level ViewModels under `ui/`.
- Route screens through `MainActivity` helpers backed by `NavController`; do not add new manual Fragment transactions.
- Use `NavigationArgs` as the single Fragment argument contract for question, method, result JSON, hexagram number, and record id fallback. Do not add new ad hoc argument constants to Fragments or `MainActivity`.
- Use XML/ViewBinding and Material components for shared UI. Do not reintroduce the removed `Ui` helper. Keep small reusable rendering logic inside focused classes such as `HexagramView` or shared XML layouts.
- Use `MaterialToolbar` / `BottomNavigationView` through `NavigationChrome` for app chrome. Do not add new hand-built TextView icon bars.
- Use Material `Chip`/`ChipGroup` or `item_filter_chip.xml` for chips. Do not create TextView chips by hand.
- Do not use icon-like text glyphs for UI controls, including `☰`, `⚙`, `◎`, `✦`, `↺`, `♡`, `♥`, `✓`, `◯`, `◌`, or `●`. Use VectorDrawable, ImageView/ImageButton, selectors, or custom views instead. Semantic reading text such as 「本卦 → 之卦」 may remain when it is content, not an icon.
- New production icon/font/image/texture assets must be committed under `res/`, listed in `tools/assets/asset_manifest.json`, and documented in `docs/ASSET_LICENSES.md` / `docs/ASSET_PIPELINE.md`. Include source URL, license, transform notes, target path, and SHA-256 checksum.
- Do not use Stitch-exported `lh3.googleusercontent.com/aida-public/...` images as production assets unless a separate verifiable redistribution license is added to the asset docs.
- Keep major XML screens and reusable item/include layouts previewable in Android Studio: add `tools:` sample text/hints, `tools:listitem`/`tools:itemCount` for RecyclerViews, `tools:showIn` where useful, and edit-mode fallbacks for custom Views that would otherwise render blank.
- Keep user-facing copy in Traditional Chinese unless there is a product reason to do otherwise.
- Keep all production behavior local-first until a backend/API integration is explicitly scoped.
- Keep divination consistency in the data layer: derive `Hexagram` and `relatingHexagram` from the same line values / changing-line helpers in `HexagramRepository`.
- Keep JSON persistence backward-compatible. New fields such as `relatingHexagramNumber` should be optional on read and derived from `lineValues` when old records do not contain them.
- Use `RecordRepository` as the UI entry point for records. `LocalRecordStore` is retained for legacy parsing/static filtering helpers, not new UI persistence.
- Keep record search/filter logic in `LocalRecordStore` or another data helper; UI should not duplicate matching rules for question, note, hexagram name, full name, or tags.
- Settings and favorites are still in `SettingsStore` / `SharedPreferences`; do not move them to Room unless that migration is explicitly scoped.

## Room & Local Data Guidelines

- Room database name: `i_ching_records.db`.
- Room schema v1 table: `divination_records`.
- `IChingDatabase` must keep `exportSchema = true`, and committed schemas live under `app/schemas/`.
- Production Room builders must not use `allowMainThreadQueries()`. UI-facing DAO work should go through `RecordRepository` async callback methods backed by `AppExecutors.diskIo()`.
- DAO list queries should default to `createdAt DESC`.
- Preserve record fields: `id`, `question`, `hexagramNumber`, `relatingHexagramNumber`, `method`, `lineValues`, `changingLines`, `createdAt`, `note`.
- Store `lineValues` and `changingLines` through `RecordTypeConverters` as JSON strings.
- `RecordRepository.migrateFromLegacyPrefsIfNeeded()` imports old `i_ching_records.records` JSON once and writes a migration flag; do not delete legacy SharedPreferences during migration.
- `RecordRepository.recordsNow()` and synchronous repository helpers are for tests, export helpers, or non-UI background work only. Fragment/ViewModel code should use LiveData or async callbacks.
- Export must use Storage Access Framework from UI; do not request broad storage permissions for JSON/text export.
- Keep `i_ching_records.xml` and `i_ching_records.db`/WAL/SHM excluded from cloud backup unless a future privacy review deliberately changes the backup model.

## Testing Guidelines

Use JUnit 4 for local unit tests and AndroidX Test/Espresso for instrumentation tests. Name test classes after the class or feature under test, ending with `Test`, and prefer descriptive test methods such as `coinLineValuesUseTraditionalBuckets`.

Current JVM test coverage includes:

- `IChingLogicTest` covers casting line-value buckets, known hexagram pattern mappings, 64-pattern uniqueness, simple-cast consistency, changing-line flips, and relating hexagrams.
- `DivinationPersistenceTest` covers `DivinationResult` / `DivinationRecord` JSON round trips, backward-compatible old JSON fallback, relating-hexagram derivation from old `lineValues`, and record mutation helpers.
- `RecordRepositoryTest` covers Room entity mapping, export JSON/text formatting including empty/null edge cases, legacy JSON parser behavior, and one-time legacy SharedPreferences-to-Room migration flag behavior.
- `SettingsStoreTest` covers settings defaults, toggles, and favorites.
- `ProfileSettingsFragmentTest` covers the UTF-8 export writer used after SAF returns a URI.
- `ResultFragmentTest` covers the Android share chooser intent wrapping `ACTION_SEND`, `text/plain`, and the share text.
- `NavigationArgsTest` covers navigation argument Bundle round trips and fallback behavior.
- `RecordRepositoryTest` also covers exported Room v1 schema and repository async export callbacks.
- `BackupRulesTest` covers Auto Backup and Data Extraction XML rules for sensitive local record storage.
- `HexagramRepositoryFilterTest` covers learning-center search and canon/favorite filters.
- `LocalRecordStoreFilterTest` covers record search by question, note, hexagram, and tag, plus method and changing-line filters.
- `ResultPresentationTest`, `RecordCardPresentationTest`, and `FavoriteHexagramPresentationTest` cover pure Java UI presentation text/state for result sharing/changing lines, record cards, and favorite icon labels.
- `AssetManifestTest` verifies production asset manifest entries, target paths, licenses/source metadata, and SHA-256 checksums.
- `UiDebtGuardTest` blocks icon-like text symbols from returning to production UI Java/XML/value resources.

Current instrumentation coverage includes:

- `ExampleInstrumentedTest` checks app package context.
- `RecordDaoInstrumentedTest` checks Room DAO insert/update/delete-all behavior with an in-memory database.
- `StableBetaWorkflowInstrumentedTest` covers onboarding to local daily, divination result auto-save to records, result recreate without duplicate auto-save, records search/filter state retention, record note edit/delete, favorites, dark-mode preference, JSON/text SAF export contracts, UI launch intents, provider-backed SAF writes, and profile delete-all confirm/cancel. Espresso root-view accessibility checks are enabled in this workflow test class.
- `TestDocumentProvider` is androidTest-only and exists to verify actual SAF writes through `ContentResolver.openOutputStream()`.

Add local tests for pure Java logic such as casting, repository mapping, relating-hexagram behavior, serialization, filtering, export formatting, and backup-sensitive persistence decisions. Add instrumentation tests for Fragment routing, onboarding, local-mode entry, dark-mode toggles, records persistence/search/filtering, favorites, sharing intent, SAF export, delete-all, and UI workflows.

## Documentation Guidelines

When changing behavior, update documentation in the same change. At minimum:

- Update `README.md` when build steps, app scope, or user-facing behavior changes.
- Update `docs/PROJECT_STATUS.md` when current capabilities, limitations, verification status, or future roadmap changes.
- Update `docs/ASSET_PIPELINE.md`, `docs/ASSET_LICENSES.md`, and `tools/assets/asset_manifest.json` when adding or changing production assets.
- Update this `AGENTS.md` when repository conventions or contributor expectations change.

Documentation should explicitly distinguish between implemented Beta behavior, design intent, known limitations, and future work.

## Commit & Pull Request Guidelines

The existing history uses short, imperative commit messages such as `init commit` and `Add stitch design docs`. Keep commits focused and use concise subjects that describe the change. Pull requests should include a brief summary, testing performed, linked issues when applicable, and screenshots or screen recordings for visible UI changes. Mention any Gradle, SDK, resource, navigation, Room schema, backup-rule, or persistence changes that affect contributors or release builds.

## Security & Configuration Tips

Do not commit local machine configuration such as `local.properties` contents or signing secrets. Keep dependency versions centralized in `gradle/libs.versions.toml`, and prefer repository-wide Gradle settings over per-developer overrides. The current app stores settings and favorites in app-private `SharedPreferences`, and divination records in app-private Room. This is suitable for local Beta behavior but not a replacement for encrypted storage, account sync, privacy policy work, or production data governance. Keep record storage excluded from cloud backup unless a future privacy review deliberately changes the backup model.
