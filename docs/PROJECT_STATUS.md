# Project Status

更新日期：2026-06-15

## 總覽

I Ching Android 專案目前處於「高保真本機 MVP」階段。這一版的目標不是完成 production app，而是把 Stitch 設計稿中的主要畫面和端到端本機流程落到 Android 原生實作中，讓使用者可以不依賴網路或後端完成一次完整體驗。

目前的 app 可以啟動、完成 onboarding、進入本機模式、查看每日一卦、提出問題、選擇占法、完成靜心儀式、取得占卜結果、自動或手動保存紀錄、編輯/刪除紀錄筆記、搜尋與篩選學習中心、查看卦象詳情、收藏卦象，以及在設定中切換深色模式。

## 已實作範圍

### App Shell 與導覽

- 單 Activity 架構：`MainActivity`。
- `activity_main.xml` 僅提供 `FragmentContainerView`。
- `MainActivity` 直接管理 Fragment replacement 與 back stack。
- 已有全域路由方法：daily、question、method、ritual、result、records、learn center、hexagram detail、profile。
- 啟動時套用 `SettingsStore` 中保存的深色模式。
- 使用 Edge-to-Edge，系統列 padding 目前只處理左右，內容本身透過 top/bottom chrome 留白。

### 畫面

目前已新增以下 Fragment：

- `SplashFragment`
- `OnboardingFragment`
- `AuthFragment`
- `DailyFragment`
- `QuestionFragment`
- `MethodFragment`
- `RitualFragment`
- `ResultFragment`
- `RecordsFragment`
- `LearnCenterFragment`
- `HexagramDetailFragment`
- `ProfileSettingsFragment`

畫面對應設計稿中的主要資訊架構與文案，但不是精準像素還原。MVP 優先確保本機可用與流程連貫。

### 資料層

`data/` 目前包含：

- `AppSettings`
- `DivinationEngine`
- `DivinationMethod`
- `DivinationRecord`
- `DivinationResult`
- `Hexagram`
- `HexagramRepository`
- `LocalRecordStore`
- `SettingsStore`
- `Trigram`

目前資料來源皆為本機：

- 64 卦基本列表在 `HexagramRepository`，並提供學習中心共用的搜尋與篩選 helper。
- 第 15 卦「地山謙」內容較完整，對應 light daily、result、detail 設計稿。
- 第 29 卦「坎為水」內容較完整，對應 dark daily 設計稿。
- 其他卦保留基本名稱、標籤與 placeholder 解釋。

### 占卜邏輯

`DivinationEngine` 支援三種模式：

- `SIMPLE`：直接隨機一卦，無變爻概念。
- `COINS`：使用 16 桶近似三枚銅錢爻值分布。
- `YARROW`：使用 16 桶近似蓍草法爻值分布。

目前會產生：

- 6 個爻值：6、7、8、9。
- 陰陽 line pattern。
- changing line index list。
- 本卦 `Hexagram`。

尚未產生或展示：

- 之卦。
- 變爻爻辭。
- 本卦/之卦合併解讀。
- 互卦、綜卦、錯卦等延伸資訊。

### 本機持久化

`SettingsStore` 使用 `SharedPreferences` 保存：

- onboarding 是否完成。
- 深色模式。
- 減少動態效果。
- 自動儲存。
- 收藏卦象集合。

`LocalRecordStore` 使用 `SharedPreferences` 保存：

- 占卜紀錄 JSON array。
- 每筆紀錄包含 id、問題、卦號、占法、爻值、變爻、建立時間、反思筆記。
- 既有舊 JSON 紀錄可繼續讀取，缺少的新欄位會以空陣列或預設占法 fallback。
- 紀錄支援新增/upsert、查找單筆、更新筆記與刪除單筆。

這些資料都只存在本機 app private storage。

### 設計資源

目前已有：

- `values/colors.xml`
- `values-night/colors.xml`
- `values/themes.xml`
- `values-night/themes.xml`
- `values/dimens.xml`

色票大致對應 Stitch design tokens：紙色背景、墨色主文字、金色 accent、outline、surface container。深色模式提供對應 night palette。

目前尚未導入：

- bundled Noto Sans TC / Noto Serif TC font resources。
- Material Symbols icon font。
- production-ready vector icon set。
- 墨洗 raster/vector assets。

### 測試

目前測試狀態：

- `IChingLogicTest`：覆蓋三枚銅錢爻值 bucket、蓍草爻值 bucket、第 15 卦 pattern mapping、repository size/content smoke checks。
- `DivinationPersistenceTest`：覆蓋 `DivinationResult` / `DivinationRecord` JSON round-trip、舊紀錄 fallback、record upsert/update/delete，以及 auto-save 使用穩定 id 避免重複紀錄的資料層行為。
- `HexagramRepositoryFilterTest`：覆蓋卦名、全名、標籤、摘要搜尋，上經/下經篩選與收藏篩選。
- `ExampleUnitTest`：Android Studio 預設範例。
- `ExampleInstrumentedTest`：Android Studio 預設 app context test。

已驗證：

- `./gradlew testDebugUnitTest` 通過。
- `./gradlew lintDebug` 通過。
- `./gradlew assembleDebug` 通過。

未完成驗證：

- `./gradlew connectedDebugAndroidTest` 需要實機或 emulator。最近一次執行因 `No connected devices!` 無法完成。

## 目前主要風險

### UI 技術債

大部分畫面以 programmatic View 建立。這讓 MVP 快速落地，但也帶來以下風險：

- 版面可讀性不如 XML 或 Compose component tree。
- 狀態管理分散在 Fragment 中。
- 複雜 responsive layout 不易維護。
- 視覺一致性仰賴 `Ui` helper，缺少正式 design system enforcement。

### 導覽技術債

目前 `MainActivity` 手動 replace Fragment。這可以支撐 MVP，但不適合長期複雜導覽：

- 沒有 Navigation Component graph。
- Deep link 與複雜 saved state recovery 仍不完整。
- `ResultFragment` 已改用 Bundle JSON snapshot，不再依賴 static result 欄位。

### 資料完整性不足

`HexagramRepository` 只有兩個設計稿重點卦較完整，其餘卦多為 placeholder。`fromLines` 對第 15、29 卦做了 design-critical pattern 優先匹配，其餘 mapping 仍是 MVP fallback，不應視為完整易經資料模型。

### 本機儲存限制

SharedPreferences 仍適合目前 MVP，且已補單筆更新與刪除，但長期仍有不足：

- JSON array 越大越不適合。
- 無 schema migration。
- 無 transaction。
- 無加密。
- 無 sync 或 conflict resolution。
- 無使用者匯出/刪除資料流程。

### 測試不足

目前已補資料序列化、紀錄 mutation 與學習中心 filter 的 JVM tests，但仍缺少：

- Fragment navigation tests。
- Onboarding 到 daily 的 instrumentation tests。
- 占卜完整流程 instrumentation tests。
- 深色模式切換測試。
- 收藏與紀錄 persistence tests。
- 視覺截圖測試。
- Accessibility tests。

### 產品化缺口

目前還不能視為 production-ready：

- 無真實登入。
- 無隱私權政策與資料使用說明。
- 無 release signing 設定。
- 無錯誤回報或 analytics。
- 無完整 accessibility。
- 無正式 icon/font/illustration assets。

## 建議下一步

### 短期：穩定 MVP

1. 補最小 Espresso tests：onboarding、本機模式、占卜流程、保存紀錄、深色模式。
2. 將 `connectedDebugAndroidTest` 納入有 emulator 的 CI 或本機驗收流程。
3. 延伸紀錄能力：搜尋、篩選、匯出與批次刪除。
4. 擴充 accessibility 驗收：focus order、字級縮放、TalkBack 與對比檢查。

### 中期：整理架構

1. 評估導入 Navigation Component。
2. 評估將 UI 從 programmatic View 遷移到 XML layout component 或 Compose。
3. 抽出 design system：Typography、spacing、button、card、chip、hexagram renderer。
4. 將 records/settings 從 SharedPreferences 遷移到 DataStore 或 Room。
5. 建立完整 hexagram mapping 與資料驗證測試。

### 長期：產品化

1. 補完整 64 卦內容與變爻解讀。
2. 支援之卦、變爻、互卦、綜卦、錯卦。
3. 加入資料匯出、備份與刪除功能。
4. 若需要帳號，設計 auth、sync、資料加密與隱私模型。
5. 建立 screenshot regression tests 與 accessibility acceptance checklist。
6. 建立 release pipeline、版本策略與 changelog。

## 文件維護規則

每次完成以下變更時，請更新本文件：

- 新增或移除主要畫面。
- 改變資料儲存方式。
- 改變占卜演算法或 hexagram mapping。
- 補齊或改變 64 卦資料內容。
- 新增測試策略或 CI 流程。
- 修正本文件列出的已知不足。
