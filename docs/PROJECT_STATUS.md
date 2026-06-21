# Project Status

更新日期：2026-06-22

## Overview

本專案目前是原生 Android 本機 Beta。主要流程已可離線完成：onboarding、本機帳號登入/註冊與 Guest 略過、日期驅動每日一卦、三步驟占卜、蓍草十八變互動流程、結果頁、本卦/變爻/之卦、紀錄保存與搜尋篩選、學習中心、卦象詳情、收藏、深色模式、字體大小、預設占法、本機每日提醒、SAF 匯出與刪除目前帳號紀錄。

本輪完成首頁提問草稿與 shared chrome UX refinement。專案仍維持 Java、Fragment、Navigation Component、Room、Material/AppCompat、XML/ViewBinding；未導入 Compose、WebView、後端、遠端 auth/sync、DataStore 或 runtime network assets。

## Implemented

- `MainActivity` 維持單 Activity + `NavController` 導覽。
- `NavigationArgs` 仍是 question、method、result JSON、hexagram number、record id fallback 的單一 Fragment argument contract；首頁提問草稿使用同一 `question` argument，但 draft read helper 不會把空白草稿 fallback 成預設問題。
- `AuthFragment` 實作本機登入/註冊/Guest 略過；credential 由 `AccountStore` 以 salted PBKDF2 verifier 保存在 app-private `SharedPreferences`，不保存明文密碼。
- `HexagramRepository` 保存 64 卦 explicit pattern map、上下卦、卦辭、六爻爻辭、標籤、摘要與行動建議；invalid input fallback 仍為第 15 卦。
- `DivinationEngine` 支援 `SIMPLE`、`COINS`、`YARROW`，並由同一 line values/changing-line helper 推導本卦與之卦；Yarrow line value bucket 為傳統 6=1/16、7=5/16、8=7/16、9=3/16。
- `YarrowCastingSession` 提供 6 爻 × 3 變的純 Java state，`YarrowCastingFragment` 可逐步完成十八變或快速完成後進入結果頁。
- `DailyInsightProvider` 以本機日期/time zone 產生每日穩定卦象與 Gregorian Traditional Chinese 日期文案。
- `RecordRepository` 是 UI 紀錄入口，Room v2 schema 保存於 `app/schemas/`；`divination_records` 以 `accountId + id` 作為主鍵。
- 舊 `i_ching_records.records` JSON 可一次性匯入 Guest；migration flag 防止重複匯入。
- `SettingsStore` / `SharedPreferences` 保存 onboarding、dark mode、reduce motion、auto save、font scale、default method、daily reminder、favorites；onboarding 是裝置層級，其餘依 active account 隔離。
- `ReminderScheduler` 使用本機 `AlarmManager`、notification channel、`POST_NOTIFICATIONS` runtime permission 與 `RECEIVE_BOOT_COMPLETED` receiver，不加入網路或同步。
- Guest 與本機帳號資料隔離；首次註冊本機帳號時，Guest records/settings/favorites 會移入新帳號。
- JSON/text 匯出走 Storage Access Framework；不要求 broad storage permission。
- Auto Backup / Data Extraction rules 排除本機帳號 credential prefs、舊 records preferences 與 Room DB/WAL/SHM。

## UI Refactor State

- Top bar 改為 `MaterialToolbar`。
- Bottom nav 改為 `BottomNavigationView` + `res/menu/bottom_nav.xml`。
- `NavigationChrome` 負責 top-level toolbar/bottom navigation route binding、active tab selection、status/navigation bar inset padding；top-level toolbar 不顯示無功能 hamburger。
- Bottom nav active state 改以 icon/label tint 呈現，不再使用大面積 selected background。
- Records / Learn 已移除 `RecyclerView` inside `ScrollView`，改為 header/filter + weighted content area。
- Filter/preset/trigram chips 使用 Material `Chip` / `ChipGroup` 或 `item_filter_chip.xml` inflation。
- Favorite controls 使用 drawable-backed state，不再輸出 `♥/♡` presentation 字串。
- Method option presentation 提供 icon resource id。
- Ritual focus、empty state、toolbar、bottom nav、button arrow/prefix affordances 均改為 drawable-backed UI。
- `dialog_edit_note.xml` 取代 programmatic bottom input。
- `Ui.java` 已刪除；`HexagramView` 內聚 dp/color helper 並支援 XML attrs。
- Bundled typography 已導入 Noto Sans TC / Noto Serif TC variable fonts，並以 font-family XML 統一引用；production UI 不再直接引用 raw font files，且避免同一大型 CJK font file 在 family XML 中重複映射造成 instrumentation heap pressure。
- Light/night paper page background 已導入 committed WebP texture。
- Splash / onboarding / local entry / daily / ritual / result / records / profile 已進一步對齊 Stitch 的品牌層級、紙張卡片、pill、wrap chips、宜忌 cards、question bubble、changing-line highlight 與 settings row rhythm。
- Fullscreen splash/onboarding/auth/local-entry/question/method/ritual/result flows use a shared inset helper so content avoids status bars, cutouts, and gesture navigation.
- CTA controls now use `MaterialButton` styles instead of clickable `TextView` button shapes; bottom navigation height/icon/label sizing is resource-driven.
- Daily 「今日想問」改為占卜提問草稿，點「開始占卜」會帶入 Step 1；空白草稿仍保持 Step 1 空白，不新增每日筆記資料模型。
- Daily / Result 的行動建議保持精簡層級；Result 的盲點提醒由 presentation 產生，古典文字可收合。

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
- Project-owned Stitch-assisted artwork generated or authored for this repository。

Stitch 匯出的 `lh3.googleusercontent.com/aida-public/...` 圖資不允許直接作為 production asset，除非能另行取得可驗證授權。Project-owned Stitch-assisted artwork may be used only when committed to `res/`, documented, and checksummed.

## Test Coverage

JVM tests 覆蓋：

- 卦象 pattern mapping、64 pattern uniqueness、changing lines、之卦。
- Divination result / record JSON persistence 與舊 JSON fallback。
- `AccountStore` register/login/duplicate email/password validation/change password/logout/delete account behavior。
- Room entity mapper、export JSON/text、legacy migration、account filtering、Guest transfer、Room v2 schema。
- Settings defaults/toggles/font scale/default method/daily reminder/favorites、account isolation、Guest transfer。
- Daily insight provider date stability/date text and Yarrow 18-step session/distribution/result derivation。
- Backup/data-extraction XML rules。
- Record search/filter 與 learning-center filters。
- Presentation mappers：result、record card、favorite icon state、daily、question presets、method options、ritual reduce-motion、hexagram list/detail。
- `AssetManifestTest`：manifest target、license/source/checksum 與 committed file 一致性。
- `UiDebtGuardTest`：production UI Java/XML/value resources 禁止回歸 icon-like text symbols、raw font-file UI refs、TextView-styled command buttons，並守住 top-level toolbar 無 dead menu、bottom nav 無 selected background contract 與 fullscreen inset helper usage。

Instrumentation tests 覆蓋：

- App context smoke test。
- Room DAO account-scoped insert/update/delete-all/Guest transfer。
- Stable workflow：onboarding → local daily、auth skip、register/login with Guest record transfer、divination auto-save → records、default method selected state、Yarrow quick-complete result auto-save、result recreate no duplicate auto-save、records search/filter state retention、note edit/delete、learning search/detail/favorite、dark mode、SAF export contracts、provider-backed writes、delete-all confirm/cancel。
- Stable workflow 也覆蓋首頁提問草稿帶入 Step 1、空白草稿不 fallback、top-level chrome 不顯示 dead hamburger。

## Verification

上一輪已驗證（2026-06-17）：

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` 通過。
- `./gradlew pixel2Api35DebugAndroidTest` 通過 15/15。

備註：AGP 仍會在 managed-device setup 印出 `testedAbi` 提醒，但任務可完成。

`connectedDebugAndroidTest` 仍需要外部實機或 emulator。

本機帳號 MVP verification（2026-06-21，上一輪）：

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` passed.
- `./gradlew pixel2Api35DebugAndroidTest` passed 17/17.

Stitch alignment reinforcement verification（2026-06-21）：

- `./gradlew testDebugUnitTest` passed.
- `./gradlew lintDebug` passed.
- `./gradlew assembleDebug` passed.
- `./gradlew assembleDebugAndroidTest` passed.
- `./gradlew pixel2Api35DebugAndroidTest` passed 19/19. AGP still prints the known `testedAbi` warning during managed-device setup.

Native UX refinement verification（2026-06-22）：

- `./gradlew testDebugUnitTest` passed.
- `./gradlew lintDebug` passed.
- `./gradlew assembleDebug` passed.
- `./gradlew assembleDebugAndroidTest` passed.
- `./gradlew pixel2Api35DebugAndroidTest` passed 22/22. AGP still prints the known `testedAbi` warning during managed-device setup.

## Known Gaps

- 無遠端帳號、同步、忘記密碼、email 驗證、後端、加密資料庫、DataStore、Safe Args。
- 無 release signing、privacy policy、analytics、crash reporting。
- 現代解析仍是 Beta 版內容，未完整納入彖傳、象傳、文言、互卦、綜卦、錯卦。
- Accessibility 尚需人工驗收 TalkBack、focus order、font scale、contrast。
- 尚未建立 screenshot regression。
- Android Studio Layout Validation 尚需人工執行並補紀錄。

## Maintenance Rules

更新主要畫面、資料儲存、占卜演算法、hexagram mapping、asset pipeline、verification state 或已知限制時，請同步更新本文件。
