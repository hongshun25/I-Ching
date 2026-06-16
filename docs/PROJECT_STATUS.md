# Project Status

更新日期：2026-06-17

## Overview

本專案目前是原生 Android 本機 Beta。主要流程已可離線完成：onboarding、本機模式入口、每日一卦、三步驟占卜、結果頁、本卦/變爻/之卦、紀錄保存與搜尋篩選、學習中心、卦象詳情、收藏、深色模式、SAF 匯出與刪除全部紀錄。

本輪完成 UI 技術債與 asset pipeline 重構。專案仍維持 Java、Fragment、Navigation Component、Room、Material/AppCompat、XML/ViewBinding；未導入 Compose、WebView、後端、auth/sync、DataStore、Room schema v2 或 runtime network assets。

## Implemented

- `MainActivity` 維持單 Activity + `NavController` 導覽。
- `NavigationArgs` 仍是 question、method、result JSON、hexagram number、record id fallback 的單一 Fragment argument contract。
- `HexagramRepository` 保存 64 卦 explicit pattern map、上下卦、卦辭、六爻爻辭、標籤、摘要與行動建議；invalid input fallback 仍為第 15 卦。
- `DivinationEngine` 支援 `SIMPLE`、`COINS`、`YARROW`，並由同一 line values/changing-line helper 推導本卦與之卦。
- `RecordRepository` 是 UI 紀錄入口，Room v1 schema 保存於 `app/schemas/`。
- 舊 `i_ching_records.records` JSON 可一次性匯入 Room；migration flag 防止重複匯入。
- `SettingsStore` / `SharedPreferences` 保存 onboarding、dark mode、reduce motion、auto save、favorites。
- JSON/text 匯出走 Storage Access Framework；不要求 broad storage permission。
- Auto Backup / Data Extraction rules 排除舊 records preferences 與 Room DB/WAL/SHM。

## UI Refactor State

- Top bar 改為 `MaterialToolbar`。
- Bottom nav 改為 `BottomNavigationView` + `res/menu/bottom_nav.xml`。
- `NavigationChrome` 只負責 toolbar/bottom navigation selection 與 route binding。
- Records / Learn 已移除 `RecyclerView` inside `ScrollView`，改為 header/filter + weighted content area。
- Filter/preset/trigram chips 使用 Material `Chip` / `ChipGroup` 或 `item_filter_chip.xml` inflation。
- Favorite controls 使用 drawable-backed state，不再輸出 `♥/♡` presentation 字串。
- Method option presentation 提供 icon resource id。
- Ritual focus、empty state、toolbar、bottom nav、button arrow/prefix affordances 均改為 drawable-backed UI。
- `dialog_edit_note.xml` 取代 programmatic bottom input。
- `Ui.java` 已刪除；`HexagramView` 內聚 dp/color helper 並支援 XML attrs。
- Bundled typography 已導入 Noto Sans TC 與 Noto Serif CJK TC。
- Light/night paper page background 已導入 committed WebP texture。

## Asset Pipeline

Production assets 皆 commit 到 repo，Gradle build 不依賴網路。新增 asset 時必須同步更新：

- `tools/assets/asset_manifest.json`
- `docs/ASSET_PIPELINE.md`
- `docs/ASSET_LICENSES.md`

目前允許來源類型：

- SIL Open Font License 1.1 fonts。
- Apache-2.0 Material icons。
- CC0 / Public Domain images or textures。
- Project-authored vectors/placeholders。

Stitch 匯出的 `lh3.googleusercontent.com/aida-public/...` 圖資不允許作為 production asset，除非能另行取得可驗證授權。

## Test Coverage

JVM tests 覆蓋：

- 卦象 pattern mapping、64 pattern uniqueness、changing lines、之卦。
- Divination result / record JSON persistence 與舊 JSON fallback。
- Room entity mapper、export JSON/text、legacy migration、Room v1 schema。
- Settings defaults/toggles/favorites。
- Backup/data-extraction XML rules。
- Record search/filter 與 learning-center filters。
- Presentation mappers：result、record card、favorite icon state、daily、question presets、method options、ritual reduce-motion、hexagram list/detail。
- `AssetManifestTest`：manifest target、license/source/checksum 與 committed file 一致性。
- `UiDebtGuardTest`：production UI Java/XML/value resources 禁止回歸 icon-like text symbols。

Instrumentation tests 覆蓋：

- App context smoke test。
- Room DAO insert/update/delete-all。
- Stable workflow：onboarding → local daily、divination auto-save → records、method selected state、result recreate no duplicate auto-save、records search/filter state retention、note edit/delete、learning search/detail/favorite、dark mode、SAF export contracts、provider-backed writes、delete-all confirm/cancel。

## Verification

本輪已驗證（2026-06-17）：

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` 通過。
- `./gradlew pixel2Api35DebugAndroidTest` 通過 15/15。

備註：AGP 仍會在 managed-device setup 印出 `testedAbi` 提醒，但任務可完成。

`connectedDebugAndroidTest` 仍需要外部實機或 emulator。

## Known Gaps

- 無真實登入、帳號、後端、同步、加密資料庫、DataStore、Safe Args。
- 無 release signing、privacy policy、analytics、crash reporting。
- 蓍草模式不是互動十八變流程。
- 現代解析仍是 Beta 版內容，未完整納入彖傳、象傳、文言、互卦、綜卦、錯卦。
- Accessibility 尚需人工驗收 TalkBack、focus order、font scale、contrast。
- 尚未建立 screenshot regression。
- Android Studio Layout Validation 尚需人工執行並補紀錄。

## Maintenance Rules

更新主要畫面、資料儲存、占卜演算法、hexagram mapping、asset pipeline、verification state 或已知限制時，請同步更新本文件。
