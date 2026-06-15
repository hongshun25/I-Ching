# I Ching Android Local Beta

I Ching 是一個原生 Android 本機 Beta，目標是把 `design/stitch_export/` 中的 Stitch 高保真設計稿轉成可反覆內測的本機 APK。當前版本專注於「本機可用、流程完整、核心資料一致、資料可控」：使用者可以完成首次啟動、進入本機模式、查看每日一卦、進行三步驟占卜、查看本卦與之卦、保存與搜尋紀錄、瀏覽六十四卦、收藏卦象、切換深色模式，並匯出或刪除本機占卜紀錄。

此專案目前不接後端、不做真實登入、不載入網路圖片，也不使用 WebView 或 Jetpack Compose。所有主要功能都以 Java、AndroidX Fragment、Navigation Component、Room、XML 資源與程式化原生 View 完成。

## 當前狀態

目前已完成一版可建置的本機 Beta：

- Splash 啟動畫面，約 3.5 秒後依 onboarding 狀態分流。
- Onboarding 三頁輪播，完成後進入明確的「本機模式」入口。
- 本機模式入口清楚說明不建立帳號、不登入、不同步，資料只保存在此裝置。
- Daily 每日一卦，light mode 顯示第 15 卦「地山謙」，night mode 顯示第 29 卦「坎為水」風格。
- 占卜流程：提問、占法選擇、長按靜心儀式、結果頁。
- 三種占法：簡易占法、三枚銅錢、蓍草靈感模式。
- 結果頁支援本卦、變爻、之卦、爻辭提示、反思筆記與 Android 純文字分享；自動儲存開啟時進入結果頁即建立或更新同一筆本機紀錄。
- 紀錄頁支援空狀態、已保存紀錄列表、問題/筆記/卦名/標籤搜尋、占法篩選、有無變爻篩選、編輯筆記與刪除確認。
- 學習中心列出 64 卦資料，支援卦名、全名、標籤、摘要、卦辭/爻辭搜尋，以及全部 / 上經 / 下經 / 我的收藏篩選。
- 卦象詳情頁呈現上下卦、卦辭、六爻爻辭、現代解析與行動建議。
- 個人設定頁可切換深色模式、減少動態效果與自動儲存設定，並可透過 Storage Access Framework 匯出 JSON、匯出純文字或刪除全部紀錄。
- 占卜紀錄已改由 Room 保存；首次啟動會從舊 `SharedPreferences` JSON 匯入 Room，成功後寫入 migration flag，但不立即刪除舊資料。

## 技術架構

專案是單模組 Android app：

```text
app/
  src/main/java/fcu/app/i_ching/
    MainActivity.java
    data/
    ui/
  src/main/res/
    layout/
    navigation/
    values/
    values-night/
  src/test/java/
  src/androidTest/java/
design/stitch_export/
docs/
```

主要元件：

- `MainActivity`：唯一 Activity，負責 Edge-to-Edge、深色模式初始化、舊紀錄匯入與少量全域導覽 helper。畫面切換已改由 `NavController` 執行。
- `app/src/main/res/navigation/main_graph.xml`：主要 Navigation graph；目前仍用簡單 Bundle/JSON snapshot 傳遞參數，未導入 Safe Args。
- `activity_main.xml`：主容器為 default `NavHostFragment`。
- `data/HexagramRepository`：靜態 64 卦資料來源。每卦保存上下卦、bottom-to-top 六爻 pattern、卦辭、六爻爻辭、標籤、現代摘要與行動建議，並以完整 pattern 查表。
- `data/DivinationEngine`：本機隨機占卜邏輯。三枚銅錢與蓍草靈感模式以 16 桶近似機率產生 6/7/8/9 爻值；簡易占法產生六條靜爻並由爻象映射本卦。
- `data/IChingDatabase`、`DivinationRecordEntity`、`DivinationRecordDao`、`RecordRepository`：Room v1 紀錄資料庫、DAO 與 UI 唯一紀錄入口。
- `data/SettingsStore`：保存 onboarding、深色模式、減少動態、自動儲存與收藏；設定與收藏本階段仍留在 `SharedPreferences`。
- `data/LocalRecordStore`：保留 legacy JSON 解析與紀錄搜尋/篩選 helper；新 UI 不再用它直接保存紀錄。
- `ui/RecordsViewModel`、`ResultViewModel`、`ProfileSettingsViewModel`：紀錄列表、結果保存與設定資料控制的狀態/資料入口。
- `ui/Ui`：程式化 View 的共用 helper，包含文字、卡片、pill button、chip、top bar、bottom nav、hexagram renderer。

ViewBinding 已啟用，供後續 XML component 化使用；多數 Beta 畫面仍是 programmatic View，這是目前刻意保留的過渡狀態。

## 本機資料與持久化

目前沒有後端或帳號同步。資料都在 app-private storage：

- `i_ching_settings` (`SharedPreferences`)
  - `onboardingComplete`
  - `darkMode`
  - `reduceMotion`
  - `autoSave`
  - `favorites`
- `i_ching_records.db` (Room)
  - `divination_records` table
  - 欄位：`id`、`question`、`hexagramNumber`、`relatingHexagramNumber`、`method`、`lineValues`、`changingLines`、`createdAt`、`note`。
  - `lineValues` 與 `changingLines` 透過 TypeConverter 以 JSON 字串保存。
  - DAO 預設依 `createdAt DESC` 查詢。
- `i_ching_records` (`SharedPreferences`)
  - 舊版 JSON array 僅作首次 Room 匯入來源；讀取舊紀錄仍支援缺少 `relatingHexagramNumber` 時由 `lineValues` 補算。

個人設定頁提供：

- 匯出 JSON：輸出完整紀錄 array。
- 匯出純文字：輸出可閱讀的問題、本卦、之卦、變爻與筆記。
- 刪除全部紀錄：清空 Room 的 `divination_records`。

因問題與筆記可能敏感，舊 `i_ching_records.xml` 與新的 Room DB / WAL / SHM 均已排除 cloud backup；device-to-device transfer 保持可用。

## 設計來源

設計參考在 `design/stitch_export/`。目前 Android 實作採用相同的資訊架構、文案方向、紙感色票、墨洗感與 pill/button/card 語言，但不是逐像素轉譯。Stitch HTML 是參考文件，不是 production code。

## 建置與測試

在 repository root 執行：

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

連接實機或 emulator 後可執行：

```bash
./gradlew connectedDebugAndroidTest
```

最近一次驗證狀態：`./gradlew testDebugUnitTest`、`./gradlew lintDebug`、`./gradlew assembleDebug` 通過。`./gradlew connectedDebugAndroidTest` 已成功建置 app/test APK，但目前環境沒有連接實機或 emulator，執行階段失敗於 `No connected devices!`。目前 JVM 測試涵蓋六十四卦 pattern mapping、之卦、簡易占法一致性、占卜 snapshot JSON、舊紀錄 fallback、Room entity mapper、legacy parser、匯出 JSON/純文字、紀錄搜尋/篩選與學習中心 filter tests。Instrumentation 目前包含 app context smoke test 與 Room DAO insert/update/delete-all 測試；需實機或 emulator 執行。

## 已知不足

目前版本仍是本機內測 Beta，有以下明確限制：

- 不提供真實登入、帳號、驗證、密碼處理、後端或雲端同步。
- 大多數畫面以 Java 程式化 View 建立；ViewBinding 已啟用，但完整 XML component system 尚未完成。
- 沒有使用實際 Noto Sans TC / Noto Serif TC bundled font，目前以系統 sans/serif 呈現。
- Material Symbols 沒有以正式 icon font 或 vector asset 系統導入，部分圖示以文字符號近似。
- 墨洗圖、紙張紋理與插圖目前多以簡化符號或色塊呈現，未建立 production-ready raster/vector asset pipeline。
- 64 卦已補卦辭與六爻爻辭，但現代解析仍是本專案撰寫的精簡 Beta 版，尚未包含彖傳、象傳、文言、互卦、綜卦、錯卦或完整學術註解。
- 占卜結果已支援本卦、變爻與之卦，但尚未支援多變爻的進階解讀規則或互卦等延伸關係。
- 蓍草模式只是機率近似，沒有十八變互動過程。
- Room 目前是 v1 本機 schema；未加入加密資料庫、帳號同步或 conflict resolution。
- 深色模式可切換，但不是所有畫面都有專屬深色版式調整；大多依 night resources 套色。
- 已補部分 content descriptions；尚未完成 accessibility 系統性檢查、focus order、字級縮放與 TalkBack 行為驗收。
- 無視覺回歸測試或截圖比對。
- Fragment/Espresso workflow tests 尚未補齊。
- 尚未建立 release signing、版本策略、隱私權政策或資料保護策略。

## 未來實作展望

1. 補 UI 系統化：把 top bar、bottom nav、record item、hexagram renderer、form controls 等逐步 XML component 化，並讓 `Ui` 收斂為過渡 helper。
2. 補 instrumentation workflow tests：onboarding → local entry → daily、占卜 → result auto-save → records、編輯筆記、刪除單筆、刪除全部與匯出 intent。
3. 深化資料模型：補彖傳、象傳、文言、互卦、綜卦、錯卦與可審校資料集。
4. 完整化占卜邏輯：補多變爻解讀策略，將蓍草模式從機率近似提升為互動十八變流程。
5. 產品化準備：補 accessibility checklist、release signing、隱私權政策、資料保護策略與 changelog 流程。
