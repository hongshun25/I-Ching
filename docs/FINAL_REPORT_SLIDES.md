# I-Ching 期末報告投影片與講稿

本文件可直接作為 PowerPoint / Google Slides 製作稿。每一頁包含「投影片內容」、「截圖建議」與「講稿」。截圖欄位優先使用實機或 emulator 截圖；若發表前來不及補圖，可先保留「待補截圖」標記。`design/stitch_export/` 只作設計參考，不建議當作正式成品截圖。

---

## 投影片 1｜封面

### 投影片內容

- **I-Ching**
- 古老智慧，現代心境的本機易經 APP
- 原生 Android 本機 Beta
- 報告重點：開發動機、競品比較、功能展示、技術工具

### 截圖建議

- 待補截圖：Splash 畫面或 Daily 首頁
- 設計參考：`design/stitch_export/splash_screen/screen.png`

### 講稿

大家好，今天要介紹的是 I-Ching，一款以「古老智慧、現代心境」為核心的本機易經 APP。  
這個專題不是把易經做成單純抽籤工具，而是把提問、占卜、解讀、保存與回顧整理成完整流程。  
目前版本是原生 Android 本機 Beta，主要資料都保存在使用者裝置中，不依賴遠端帳號或後端服務。  
接下來會依序說明開發動機、和現有 APP 的差異、主要功能畫面，以及實作時使用的技術。

---

## 投影片 2｜開發動機

### 投影片內容

- 傳統易經工具門檻較高，需要理解卦象、變爻與解讀脈絡
- 一般查詢資料分散，占卜結果不容易和個人問題連結
- 使用者占卜後常缺少保存、筆記與回顧機制
- 目標：用手機完成「提問 → 占卜 → 解讀 → 記錄 → 回看」

### 截圖建議

- 待補截圖：Onboarding 三頁或 Question 提問頁
- 設計參考：`design/stitch_export/onboarding_flow/screen.png`

### 講稿

我們的開發動機來自三個問題：易經入門門檻高、查詢資料分散，以及占卜後的反思不容易保存。  
如果只是隨機產生一卦，使用者可能看完就結束，很難把它和當下真正想問的問題連起來。  
所以這個 APP 的目標是把占卜流程完整化，讓使用者先整理問題，再完成占法、看到解讀，最後保存紀錄與筆記。  
也就是說，它不是只做抽籤，而是希望幫助使用者建立一個可回顧的自我反思流程。

---

## 投影片 3｜使用者好處

### 投影片內容

- **離線可用**：主要流程不需要網路
- **本機資料**：Guest 與本機帳號都可使用，資料保存在裝置
- **可回顧**：保存問題、結果、占法、變爻與筆記
- **可搜尋**：依問題、筆記、卦名、標籤、占法與變爻篩選
- **可學習**：學習中心提供 64 卦瀏覽與卦象詳情

### 截圖建議

- 待補截圖：Daily、Records、Learn Center 各一張小圖
- 設計參考：
  - `design/stitch_export/daily_insight/screen.png`
  - `design/stitch_export/learn_center/screen.png`

### 講稿

對使用者來說，第一個好處是主要功能離線也可以使用，適合在安靜或沒有網路的情境下使用。  
第二個好處是資料保存在本機，而且 Guest 和本機帳號都有各自的資料分區。  
占卜之後，使用者可以保存問題、卦象、變爻與筆記，未來再透過搜尋或篩選回來看。  
學習中心也讓使用者不只停留在結果頁，而是能逐步理解六十四卦的內容。

---

## 投影片 4｜與現有 APP 比較

### 投影片內容

| 比較面向 | 一般易經 / 占卜 APP | I-Ching 本機 Beta |
| --- | --- | --- |
| 資料隱私 | 常見遠端登入、廣告、同步或不透明儲存 | 不使用 Firebase / 後端登入，資料留在裝置 |
| 使用模式 | 多數需要登入或只提供單一模式 | Guest 與本機帳號都可使用，資料分區隔離 |
| 占卜流程 | 常見一鍵抽取或結果導向 | 三步驟提問、選占法、靜心，另有蓍草十八變 |
| 紀錄回顧 | 可能只存結果或無搜尋 | 可搜尋問題、筆記、卦名、標籤，也可篩選占法與變爻 |
| 學習內容 | 常見片段式解釋 | 64 卦列表、卦象詳情、六爻爻辭、收藏 |
| 資料匯出 | 不一定提供 | 支援 JSON / 純文字匯出 |

### 截圖建議

- 待補截圖：I-Ching 功能拼貼
- 若要具名比較，發表前人工補 2-3 款已查證 APP 名稱與截圖；未查證前不在投影片寫死競品名稱。

### 講稿

這頁用功能面向做比較，不直接批評特定產品，因為如果要具名比較需要先人工查證競品版本。  
I-Ching 的主要差異在於本機隱私、完整占卜流程、紀錄搜尋、學習中心與匯出功能。  
它不使用 Firebase、後端登入或網路同步，因此更適合作為一個裝置本機的反思工具。  
同時，Guest 與本機帳號資料隔離，讓使用者可以先試用，也可以建立本機身份保存自己的紀錄。

---

## 投影片 5｜APP 流程總覽

### 投影片內容

```text
Splash
  → Onboarding
  → 本機入口 / 登入 / 註冊 / Guest
  → Daily 每日一卦
  → Step 1 提問
  → Step 2 選占法
  → Step 3 靜心 / 蓍草十八變
  → Result 結果
  → Records / Learn / Profile
```

- 單 Activity + Navigation Component 管理畫面
- 主要底部導覽：Daily、Records、Learn、Profile
- 結果可自動保存到紀錄，之後可回到 Records 查看

### 截圖建議

- 待補截圖：流程圖或 navigation graph 摘要圖
- 參考檔案：`app/src/main/res/navigation/main_graph.xml`

### 講稿

整體流程從 Splash 與 Onboarding 開始，接著進入本機模式、登入註冊或 Guest 使用。  
進入 APP 後，Daily 是主要起點，使用者可以看每日一卦，也可以輸入今日想問並帶到提問流程。  
占卜流程分成提問、選占法與靜心，若選擇蓍草法，還會進入十八變互動畫面。  
最後結果頁可以保存與分享，保存後的內容會出現在 Records，也可以從 Learn 和 Profile 進入學習與設定功能。

---

## 投影片 6｜註冊 / 登入 / Guest 模式

### 投影片內容

- 支援本機帳號註冊與登入
- 支援 Guest 略過登入
- 本機帳號只存在此裝置
- 無 email 驗證、雲端同步、Firebase 或遠端 token
- 密碼不保存明文，使用 salted PBKDF2 verifier
- Guest 與本機帳號的紀錄、設定、收藏分開保存

### 截圖建議

- 待補截圖：本機入口、登入畫面、註冊畫面
- 設計參考：`design/stitch_export/login_register/screen.png`

### 講稿

登入註冊功能在這個專題裡是本機身份系統，不是遠端會員系統。  
使用者可以建立本機帳號，也可以用 Guest 直接進入 APP，兩種模式都能實際使用主要功能。  
本機密碼不會以明文保存，而是由 `AccountStore` 轉成 salted PBKDF2 verifier 後存在 app-private SharedPreferences。  
另外，Guest 和本機帳號的紀錄、設定與收藏會隔離，第一次註冊本機帳號時也可以把 Guest 資料移入新帳號。

---

## 投影片 7｜每日一卦

### 投影片內容

- 依本機日期產生每日穩定卦象
- 顯示今日主題、卦象摘要與宜忌提示
- 「今日想問」可作為占卜提問草稿
- 支援深色模式與字體設定
- 可從 Daily 進入占卜、紀錄或學習該卦

### 截圖建議

- 待補截圖：Daily 首頁、Daily 深色模式
- 設計參考：
  - `design/stitch_export/daily_insight/screen.png`
  - `design/stitch_export/daily_insight_dark_mode/screen.png`

### 講稿

Daily 每日一卦是 APP 的首頁功能，它會依照本機日期與時區產生當天穩定的卦象。  
這個設計讓使用者每天都能看到固定的主題，而不是每次開啟 APP 都亂數變動。  
頁面中的「今日想問」是提問草稿，按下開始占卜後會帶入 Step 1，不會另外建立每日筆記資料。  
使用者也可以從這裡查看過去紀錄或進入學習中心了解當天的卦。

---

## 投影片 8｜占卜流程

### 投影片內容

- Step 1：輸入真正想理解的問題
- Step 2：選擇占法
- Step 3：靜心儀式，讓使用者回到問題本身
- 支援三種占法：
  - 簡易占法
  - 銅錢占法
  - 蓍草十八變
- 蓍草法提供 6 爻 × 3 變的互動流程，也可快速完成

### 截圖建議

- 待補截圖：Question、Method、Ritual、Yarrow Casting
- 設計參考：
  - `design/stitch_export/step_1_question/screen.png`
  - `design/stitch_export/step_2_method/screen.png`
  - `design/stitch_export/step_3_ritual/screen.png`

### 講稿

占卜流程刻意拆成三個步驟，是為了避免使用者一進來就直接抽結果。  
第一步先整理問題，第二步選擇適合當下的占法，第三步用靜心儀式把注意力拉回問題。  
目前支援簡易、銅錢與蓍草三種方式，其中蓍草法有完整的十八變互動流程。  
不過為了展示與測試效率，蓍草流程也提供快速完成，讓使用者可以直接進入結果頁。

---

## 投影片 9｜占卜結果

### 投影片內容

- 顯示本卦、變爻與之卦
- 呈現卦名、卦辭、六爻爻辭與現代解析
- 依變爻提供重點提醒與盲點提示
- 可撰寫筆記
- 可分享啟示
- 可儲存或更新紀錄

### 截圖建議

- 待補截圖：Result 主畫面、變爻區塊、筆記與分享按鈕
- 設計參考：`design/stitch_export/divination_result/screen.png`

### 講稿

結果頁是占卜流程的核心，它會把本卦、變爻和之卦一起呈現。  
資料來源不是用卦序亂數推測，而是由 `HexagramRepository` 依六爻 pattern 查出正確卦象，再由變爻推導之卦。  
頁面也包含古典文字與現代解析，讓使用者可以同時看到傳統內容和比較容易理解的說明。  
最後使用者可以寫下筆記、分享結果，或把這次占卜保存到紀錄中。

---

## 投影片 10｜紀錄管理

### 投影片內容

- 占卜結果可自動保存到 Room database
- 紀錄包含問題、卦象、之卦、占法、爻值、變爻、時間與筆記
- 支援搜尋：
  - 問題
  - 筆記
  - 卦名 / 完整卦名
  - 標籤
- 支援占法篩選與變爻篩選
- 可編輯筆記、刪除單筆紀錄

### 截圖建議

- 待補截圖：Records 清單、搜尋 / 篩選狀態、編輯筆記 dialog、空狀態
- 設計參考：`design/stitch_export/empty_state/screen.png`

### 講稿

紀錄管理讓占卜不只是一次性的結果，而是可以持續累積與回顧的資料。  
每筆紀錄會保存問題、卦象、之卦、占法、爻值、變爻、建立時間與筆記。  
搜尋不只比對問題，也會比對筆記、卦名、完整卦名與標籤，另外也能用占法和變爻做篩選。  
這樣使用者之後想回看某一類問題，或整理某段時間的反思，就不需要手動翻找所有結果。

---

## 投影片 11｜學習中心與卦象詳情

### 投影片內容

- 瀏覽六十四卦
- 支援卦名與關鍵字搜尋
- 支援收藏卦象
- 卦象詳情包含：
  - 上下卦組成
  - 主旨核心
  - 適宜情境
  - 《易經》原文
  - 六爻爻辭
  - 現代解析

### 截圖建議

- 待補截圖：Learn Center、Hexagram Detail、收藏狀態
- 設計參考：
  - `design/stitch_export/learn_center/screen.png`
  - `design/stitch_export/hexagram_detail/screen.png`

### 講稿

學習中心的目的，是讓 APP 不只提供占卜結果，也能幫助使用者逐步認識六十四卦。  
使用者可以瀏覽所有卦象，也可以用卦名或關鍵字搜尋。  
進入卦象詳情後，可以看到上下卦組成、主旨核心、適宜情境、古典原文、六爻爻辭與現代解析。  
收藏功能則讓使用者把常查或特別有感的卦保存起來，之後更容易回到該內容。

---

## 投影片 12｜個人設定與資料管理

### 投影片內容

- 外觀設定：深色模式、字體大小、減少動態
- 占卜偏好：預設占法、自動保存
- 每日提醒：本機通知與重開機恢復
- 資料匯出：JSON / 純文字，使用 Storage Access Framework
- 資料刪除：刪除目前帳號紀錄、刪除本機帳號
- 敏感資料排除 cloud backup

### 截圖建議

- 待補截圖：Profile / Settings、匯出選項、刪除確認
- 設計參考：`design/stitch_export/profile_settings/screen.png`

### 講稿

Profile 和 Settings 主要負責個人偏好與資料管理。  
使用者可以調整深色模式、字體大小、預設占法、自動保存與每日提醒。  
匯出功能使用 Android 的 Storage Access Framework，因此不需要要求 broad storage permission，也可以讓使用者選擇輸出位置。  
資料刪除與帳號刪除都只針對目前帳號分區，另外帳號 credential 和紀錄資料也已在 backup rules 中排除雲端備份。

---

## 投影片 13｜使用技術與工具

### 投影片內容

| 類別 | 已使用 |
| --- | --- |
| 開發語言 | Java 11 |
| Android 架構 | Single Activity、AndroidX Fragment、Navigation Component |
| UI | XML Layout、ViewBinding、Material Components、AppCompat、自訂 `HexagramView` |
| 本機紀錄 | Room SQLite database，schema v2 |
| 帳號 / 設定 / 收藏 | SharedPreferences |
| 密碼保護 | salted PBKDF2 verifier |
| 每日提醒 | AlarmManager、Notification channel、Boot receiver |
| 匯出 | Storage Access Framework，JSON / 純文字 |
| 測試 | JUnit 4、Robolectric、Espresso、Room instrumentation tests、Gradle managed device |

| 未使用 |
| --- |
| Firebase、Google Map、WebView、Jetpack Compose、後端 API、遠端登入、雲端同步、`INTERNET` permission |

### 截圖建議

- 待補圖：技術架構圖
- 可用圖層：UI 層、Data 層、本機儲存、Android 系統服務、測試

### 講稿

技術上，這個專題使用 Java 11 和原生 Android 架構完成，畫面由 Fragment、Navigation Component、XML Layout 與 ViewBinding 組成。  
占卜紀錄使用 Room SQLite database 保存，帳號、設定與收藏則使用 SharedPreferences，並且依 Guest 或本機帳號分區。  
本機密碼用 salted PBKDF2 verifier 保存，不存明文；匯出功能走 Storage Access Framework，每日提醒則使用 AlarmManager 和 Notification。  
需要特別說明的是，本專案沒有使用 Firebase、Google Map、WebView、Compose、後端 API、遠端登入、雲端同步或 `INTERNET` 權限。

---

## 投影片 14｜成果與限制

### 投影片內容

- **成果**
  - 已完成本機 Beta 主要流程
  - 可離線完成 Daily、占卜、結果、紀錄、學習、設定與匯出
  - Guest / 本機帳號資料隔離
  - 64 卦資料與變爻 / 之卦推導在資料層集中處理
- **驗證狀態（2026-06-22 文件紀錄）**
  - `testDebugUnitTest` 通過
  - `lintDebug` 通過
  - `assembleDebug` 通過
  - `assembleDebugAndroidTest` 通過
  - `pixel2Api35DebugAndroidTest` 通過 22/22
- **限制**
  - 無遠端同步、忘記密碼、Firebase、release signing
  - 尚需人工補正式截圖與展示影片
  - TalkBack、focus order、字級縮放與對比仍需人工驗收

### 截圖建議

- 待補圖：功能完成度總覽或測試結果截圖

### 講稿

目前成果是完成一個功能完整的本機 Beta，使用者可以離線完成每日一卦、占卜、解讀、保存、搜尋、學習與匯出。  
資料層集中處理六十四卦、變爻與之卦推導，避免 UI 重複實作占卜邏輯。  
依照專案文件在 2026 年 6 月 22 日的紀錄，單元測試、lint、debug build、instrumentation APK 與 managed-device workflow 都已通過。  
限制方面，這不是雲端會員產品，沒有遠端同步、忘記密碼、Firebase 或 release signing；正式發表前也還需要補上實機截圖與展示影片。

---

## 截圖補件清單

正式簡報建議補以下實機或 emulator 截圖：

- Splash
- Onboarding
- 本機入口
- 登入畫面
- 註冊畫面
- Daily 首頁
- Daily 深色模式
- Question 提問
- Method 選占法
- Ritual 靜心
- Yarrow Casting 蓍草十八變
- Result 結果頁
- Result 變爻區塊
- Records 清單
- Records 搜尋 / 篩選
- 編輯筆記 dialog
- Records 空狀態
- Learn Center
- Hexagram Detail
- Profile / Settings
- JSON / 純文字匯出
- 刪除紀錄 / 刪除帳號確認

## 發表前檢查

- 老師大綱四項已覆蓋：開發動機、與現有 APP 比較、功能與截圖、使用技術與工具。
- 每個主要功能都有用途說明與截圖欄位。
- 技術頁已區分已使用與未使用技術。
- 競品比較採面向式比較，未查證前不寫死競品名稱。
- 每頁都有 3-5 句講稿，可照順序發表。
