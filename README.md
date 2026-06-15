# I Ching Android MVP

I Ching 是一個原生 Android 本機 MVP，目標是把 `design/stitch_export/` 中的 Stitch 高保真設計稿轉成可執行的 Android 體驗。當前版本專注於「本機可用、流程完整、視覺接近設計稿」：使用者可以完成首次啟動、進入本機模式、查看每日一卦、進行三步驟占卜、保存紀錄、瀏覽六十四卦、收藏卦象，以及切換深色模式。

此專案目前不接後端、不做真實登入、不載入網路圖片，也不使用 WebView 或 Jetpack Compose。所有主要功能都以 Java、AndroidX Fragment、XML 資源與程式化原生 View 完成。

## 當前狀態

目前已完成一版可建置的本機 MVP：

- Splash 啟動畫面，約 3.5 秒後依 onboarding 狀態分流。
- Onboarding 三頁輪播與「開始使用 / 本機模式」入口。
- Login/Register 高保真 UI shell；登入與註冊按鈕目前都導向本機模式。
- Daily 每日一卦，light mode 顯示第 15 卦「地山謙」，night mode 顯示第 29 卦「坎為水」風格。
- 占卜流程：提問、占法選擇、長按靜心儀式、結果頁。
- 三種占法：簡易占法、三枚銅錢、蓍草靈感模式。
- 結果頁支援反思筆記；自動儲存開啟時進入結果頁即建立一筆本機紀錄，之後儲存筆記會更新同一筆紀錄。
- 紀錄頁支援空狀態、已保存紀錄列表、編輯筆記與刪除確認。
- 學習中心列出 64 卦基本資料，支援卦名、全名、標籤、摘要搜尋，以及全部 / 上經 / 下經 / 我的收藏篩選。
- 卦象詳情頁完整呈現設計稿涉及的第 15 卦，並提供第 29 卦的較完整內容。
- 個人設定頁可切換深色模式、減少動態效果與自動儲存設定。
- 本機儲存使用 app-private `SharedPreferences`。部分可點擊控制已補 content descriptions，作為 accessibility 的第一階段整理。

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
    values/
    values-night/
  src/test/java/
  src/androidTest/java/
design/stitch_export/
docs/
```

主要元件：

- `MainActivity`：唯一 Activity，負責 Edge-to-Edge、初始 Fragment、簡單 Fragment 導覽、暫存占卜提問與占法。
- `activity_main.xml`：只保留 `FragmentContainerView` 作為畫面容器。
- `data/HexagramRepository`：靜態 64 卦資料來源。64 卦都有基本名稱、標籤與 placeholder 解釋；第 15 卦與第 29 卦內容較完整。
- `data/DivinationEngine`：本機隨機占卜邏輯。三枚銅錢與蓍草靈感模式以 16 桶近似機率產生 6/7/8/9 爻值。
- `data/SettingsStore`：保存 onboarding、深色模式、減少動態、自動儲存與收藏。
- `data/LocalRecordStore`：用 JSON 字串保存占卜紀錄。
- `ui/Ui`：程式化 View 的共用 helper，包含文字、卡片、pill button、chip、top bar、bottom nav、hexagram renderer。
- `ui/*Fragment`：各畫面的原生 Fragment 實作。

## 本機資料與持久化

目前沒有資料庫或後端。所有持久化都在 app-private `SharedPreferences`：

- `i_ching_settings`
  - `onboardingComplete`
  - `darkMode`
  - `reduceMotion`
  - `autoSave`
  - `favorites`
- `i_ching_records`
  - `records`：JSON array 字串，元素包含 id、question、hexagramNumber、method、lineValues、changingLines、createdAt、note；讀取舊紀錄時缺少的新欄位會以預設值 fallback。

這個設計足夠支撐 MVP。紀錄目前已支援單筆刪除確認與筆記更新，但仍未處理資料遷移、加密、備份、跨裝置同步、批次管理或匯出。

## 設計來源

設計參考在 `design/stitch_export/`：

- `splash_screen`
- `onboarding_flow`
- `login_register`
- `daily_insight`
- `daily_insight_dark_mode`
- `step_1_question`
- `step_2_method`
- `step_3_ritual`
- `divination_result`
- `empty_state`
- `learn_center`
- `hexagram_detail`
- `profile_settings`
- `i_ching/DESIGN.md`

目前 Android 實作採用相同的資訊架構、文案方向、紙感色票、墨洗感與 pill/button/card 語言，但不是逐像素轉譯。Stitch HTML 是參考文件，不是 production code。

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

最近一次驗證狀態：

- `./gradlew assembleDebug`：通過。
- `./gradlew testDebugUnitTest`：通過，包含占卜 snapshot JSON、舊紀錄 fallback、紀錄 upsert/update/delete 與學習中心 filter tests。
- `./gradlew lintDebug`：通過。
- `./gradlew connectedDebugAndroidTest`：因本機沒有連接裝置而失敗，錯誤為 `No connected devices!`。

## 已知不足

目前版本仍是 MVP，有以下明確限制：

- 登入/註冊只是 UI 狀態，沒有帳號、驗證、密碼處理或雲端同步。
- 大多數畫面以 Java 程式化 View 建立，尚未建立完整 XML component system、Compose component system 或正式 design system 模組。
- 沒有使用實際 Noto Sans TC / Noto Serif TC bundled font，目前以系統 sans/serif 呈現。
- Material Symbols 沒有以正式 icon font 或 vector asset 系統導入，部分圖示以文字符號近似。
- 墨洗圖、紙張紋理與插圖目前多以簡化符號或色塊呈現，未建立 production-ready raster/vector asset pipeline。
- 64 卦只有基本列表資料；完整長文、爻辭、象傳、變卦與綜卦/錯卦關係尚未補齊。
- `HexagramRepository` 的 generic pattern mapping 是 MVP fallback，不是完整嚴謹的易經卦序/八卦對照資料模型。
- 占卜結果目前只顯示本卦，尚未根據變爻產生之卦或完整變爻解讀。
- 蓍草模式只是機率近似，沒有十八變互動過程。
- 紀錄支援新增、筆記編輯與刪除；尚未支援紀錄搜尋、篩選、匯出或備份。
- 深色模式可切換，但不是所有畫面都有專屬深色版式調整；大多依 night resources 套色。
- 已補部分 content descriptions；尚未完成 accessibility 系統性檢查、focus order、字級縮放與 TalkBack 行為驗收。
- 無視覺回歸測試或截圖比對。
- Instrumentation/Espresso workflow tests 尚未建立。
- 尚未建立 release signing、版本策略、隱私權政策或資料保護策略。

## 未來實作展望

建議依以下順序推進：

1. 補齊資料模型
   - 建立完整 64 卦資料結構。
   - 補上卦辭、彖傳、象傳、爻辭、現代解析、應用場景。
   - 建立嚴謹的上下卦、line pattern、卦序、變卦 mapping。

2. 完整化占卜邏輯
   - 支援變爻與之卦。
   - 結果頁呈現本卦、變爻、之卦與行動建議。
   - 將蓍草模式從機率近似提升為可互動的十八變流程。

3. 強化本機資料能力
   - 將 SharedPreferences 紀錄改為 Room 或 DataStore。
   - 加入資料 migration、刪除、編輯、搜尋、篩選、匯出。
   - 評估是否需要 EncryptedSharedPreferences 或 SQLCipher 類型方案。

4. 完成 UI 系統化
   - 決定長期 UI 路線：XML component library、Compose migration，或持續 programmatic View。
   - 導入正式字型與 icon/vector assets。
   - 補齊墨洗/紙感本地資產。
   - 建立 tablet 與 landscape layout 策略。

5. 補齊測試
   - 擴充 JVM tests：repository mapping、變卦、settings behavior。
   - 建立 Espresso tests：onboarding、本機模式、五分頁切換、占卜流程、收藏、深色模式。
   - 加入 screenshot tests 或手動截圖驗收流程。

6. 產品化準備
   - 補隱私權、資料刪除、備份策略。
   - 若要上線帳號功能，先定義 auth、sync、conflict resolution 與資料安全模型。
   - 建立 release build、signing、版本命名與 changelog 流程。
