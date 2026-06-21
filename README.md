# I Ching Android Local Beta

I Ching 是原生 Android 本機 Beta。它使用 Java、AndroidX Fragment、Navigation Component、Room、Material/AppCompat、XML/ViewBinding 與自訂 `HexagramView`；不使用 WebView、Jetpack Compose、後端、遠端登入或 runtime network UI assets。

目前 app 可完成 onboarding、本機帳號登入/註冊與 Guest 略過、每日一卦、三步驟占卜、結果頁、本卦/變爻/之卦、紀錄保存與搜尋篩選、學習中心、卦象詳情、收藏、深色模式、Storage Access Framework 匯出 JSON/純文字，以及刪除目前帳號紀錄。

## Current Scope

- 本機帳號是真實操作模式之一，但只存在此裝置；無 Firebase、自有 API、email 驗證、同步或 `INTERNET` 權限。
- Guest 與本機帳號的紀錄、設定、收藏彼此隔離；首次註冊本機帳號會將 Guest 資料移入該帳號。
- 占卜紀錄保存在 app-private Room database `i_ching_records.db`，目前 schema v2 以 `accountId + id` 作為主鍵。
- 設定與收藏保存在 account-scoped app-private `SharedPreferences`；onboarding completion 仍是裝置層級。
- 舊 `SharedPreferences` 紀錄 JSON 只作一次性 Room migration source，不會在 migration 後刪除。
- 問題、筆記與本機 credential verifier 可能敏感，`i_ching_accounts.xml`、`i_ching_records.xml` 與 Room DB/WAL/SHM 已排除 cloud backup。
- 64 卦資料由 `HexagramRepository` 以 explicit bottom-to-top 六爻 pattern map 查表；無效輸入才 fallback 到第 15 卦。

## UI And Assets

本輪已清除主要 UI 技術債：

- `include_top_bar.xml` 使用 `MaterialToolbar`。
- `include_bottom_nav.xml` 使用 `BottomNavigationView` 與 `res/menu/bottom_nav.xml`。
- Records / Learn 列表不再把 `RecyclerView` 放在 `ScrollView` 中。
- Filter、preset、trigram chips 改用 Material `Chip` / `ChipGroup` 或 `item_filter_chip.xml` inflation。
- 收藏、底部導覽、toolbar、狀態、ritual focus、empty state 等 icon-like UI 改為 VectorDrawable / ImageView / ImageButton。
- `Ui.java` 已移除；`HexagramView` 內聚 dp/color 與 XML attrs。
- 打包 Noto Sans TC / Noto Serif CJK TC 字體與紙張紋理、Open Access 圖像。

Production assets 皆 commit 到 repo，build 不依賴網路。來源、授權、轉檔規則與 checksum 記錄於：

- `tools/assets/asset_manifest.json`
- `docs/ASSET_PIPELINE.md`
- `docs/ASSET_LICENSES.md`

新增或更換 production icon/font/image/texture 必須同步更新 manifest 與 license docs；不得直接使用 Stitch 匯出的 `lh3.googleusercontent.com/aida-public/...` 圖資。

## Build And Test

在 repository root 執行：

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
./gradlew assembleDebugAndroidTest
```

具備 managed-device prerequisites 時：

```bash
./gradlew pixel2Api35DebugAndroidTest
```

連接實機或 emulator 時：

```bash
./gradlew connectedDebugAndroidTest
```

最近一次本機驗證（2026-06-21）：

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` 通過。
- `./gradlew pixel2Api35DebugAndroidTest` 通過 17/17；AGP 仍會印出 `testedAbi` setup 提醒。

## Project Layout

```text
app/src/main/java/fcu/app/i_ching/
  MainActivity.java
  NavigationArgs.java
  data/
  ui/
app/src/main/res/
  drawable/
  drawable-nodpi/
  font/
  layout/
  menu/
  navigation/
  values/
  values-night/
app/src/test/java/
app/src/androidTest/java/
docs/
tools/assets/
design/stitch_export/
```

`design/stitch_export/` 是參考資料，不是 production UI code 或 production asset source。

## Known Limits

- 無遠端帳號、同步、忘記密碼、email 驗證、後端、加密資料庫、隱私權政策、release signing。
- 蓍草模式是爻值機率近似，不是互動十八變流程。
- 現代解析仍是 Beta 版精簡內容，未完整納入彖傳、象傳、文言、互卦、綜卦、錯卦。
- Accessibility checks 已在 Espresso workflow 啟用，但仍需人工驗收 TalkBack、focus order、字級縮放與對比。
- 尚未建立 screenshot regression；明顯 UI 變更需更新 `docs/UI_AUDIT.md` 或補人工驗收紀錄。
