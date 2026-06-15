# Project Status

更新日期：2026-06-16

## 總覽

I Ching Android 專案目前處於「本機 Beta」階段。這一版的目標不是 production app，而是把 Stitch 設計稿中的主要畫面、端到端本機流程、核心易經資料一致性與本機資料控制落到 Android 原生實作中，讓使用者可以不依賴網路或後端完成、保存、匯出與刪除一次完整體驗。

目前的 app 可以啟動、完成 onboarding、進入本機模式、查看每日一卦、提出問題、選擇占法、完成靜心儀式、取得含本卦/變爻/之卦的占卜結果、自動或手動保存紀錄、搜尋/篩選/編輯/刪除紀錄筆記、搜尋與篩選學習中心、查看卦象詳情、收藏卦象，以及在設定中切換深色模式、匯出 JSON、匯出純文字、刪除全部紀錄。

## 已實作範圍

### App Shell 與導覽

- 單 Activity 架構：`MainActivity`。
- `activity_main.xml` 現在 hosts default `NavHostFragment`。
- `app/src/main/res/navigation/main_graph.xml` 定義 splash、onboarding、本機入口、daily、question、method、ritual、result、records、learn center、hexagram detail、profile/settings。
- `MainActivity` 不再手動 replace Fragment；只保留全域 helper，透過 `NavController` 導覽。
- Question/method/ritual/result/hexagram detail 參數以簡單 Bundle / JSON snapshot 傳遞；本階段未導入 Safe Args。`main_graph.xml` 已宣告既有 simple arguments，`NavigationArgs` 是 Fragment argument 的集中讀寫入口，並提供預設問題、預設占法、result JSON、record id 與 hexagram number fallback。
- 啟動時套用 `SettingsStore` 中保存的深色模式，並透過 `RecordRepository` 非同步執行舊紀錄到 Room 的一次性匯入。
- 使用 Edge-to-Edge，系統列 padding 目前只處理左右，內容本身透過 top/bottom chrome 留白。

### 畫面

目前已新增以下 Fragment：

- `SplashFragment`
- `OnboardingFragment`
- `AuthFragment`：已改為明確的本機模式入口，不再呈現假登入/註冊表單。
- `DailyFragment`
- `QuestionFragment`
- `MethodFragment`
- `RitualFragment`
- `ResultFragment`
- `RecordsFragment`
- `LearnCenterFragment`
- `HexagramDetailFragment`
- `ProfileSettingsFragment`

畫面對應設計稿中的主要資訊架構與文案，但不是精準像素還原。Beta 優先確保本機可用、流程連貫、資料映射正確與資料可控。

### 資料層

`data/` 目前包含：

- `AppSettings`
- `AppExecutors`
- `DivinationEngine`
- `DivinationMethod`
- `DivinationRecord`
- `DivinationRecordDao`
- `DivinationRecordEntity`
- `DivinationResult`
- `Hexagram`
- `HexagramRepository`
- `IChingDatabase`
- `LocalRecordStore`
- `RecordRepository`
- `RecordTypeConverters`
- `SettingsStore`
- `Trigram`

目前資料來源皆為本機：

- 64 卦資料在 `HexagramRepository`，每卦保存上下卦 `Trigram`、bottom-to-top 六爻 pattern、卦辭、六爻爻辭、標籤、現代摘要與行動建議。
- `fromLines(boolean[])` 以完整 64 pattern 查表；無效輸入才 fallback 到第 15 卦。
- `relatingFrom(...)` / `relatingFromLineValues(...)` 統一處理變爻翻轉與之卦映射。
- 第 15 卦「地山謙」與第 29 卦「坎為水」保留較完整的設計稿對應文案；其他卦提供精簡 Beta 版現代解析。

### 占卜邏輯

`DivinationEngine` 支援三種模式：

- `SIMPLE`：產生六條靜爻，並由這組爻映射本卦；無變爻。
- `COINS`：使用 16 桶近似三枚銅錢爻值分布。
- `YARROW`：使用 16 桶近似蓍草法爻值分布。

目前會產生 6 個爻值、陰陽 line pattern、changing line index list、本卦 `Hexagram` 與之卦 `Hexagram` / `relatingHexagramNumber`。尚未產生或展示互卦、綜卦、錯卦等延伸資訊；蓍草模式仍是機率近似，不是互動十八變流程。

### 本機持久化

`SettingsStore` 使用 `SharedPreferences` 保存：

- onboarding 是否完成。
- 深色模式。
- 減少動態效果。
- 自動儲存。
- 收藏卦象集合。

占卜紀錄已改由 Room 保存：

- Database：`i_ching_records.db`。
- Table：`divination_records`。
- Entity 欄位：`id`、`question`、`hexagramNumber`、`relatingHexagramNumber`、`method`、`lineValues`、`changingLines`、`createdAt`、`note`。
- 陣列與變爻清單透過 `RecordTypeConverters` 以 JSON 字串保存。
- DAO 查詢預設 `createdAt DESC`。
- `IChingDatabase` 已啟用 `exportSchema = true`，v1 schema 提交於 `app/schemas/fcu.app.i_ching.data.IChingDatabase/1.json`。

`RecordRepository` 是 UI 的紀錄入口：

- `records()`
- `recordsNow()`：僅供測試、匯出 helper 或非 UI 背景工作使用。
- `addOrUpdate()` / `updateNote()` / `delete()` / `deleteAll()` / `find()` / `exportJson()` / `exportText()` / `migrateFromLegacyPrefsIfNeeded()`：UI 透過 async callback overload 呼叫，repository 以單一 `AppExecutors.diskIo()` 執行 DAO 工作並回到主執行緒回呼。

舊版 `SharedPreferences` 的 `i_ching_records.records` JSON array 仍保留為 migration source。首次啟動成功匯入後寫入 `i_ching_record_migration.roomMigrated = true`，但不立即刪除舊資料。讀取舊 JSON 紀錄仍支援缺少 `relatingHexagramNumber` 時由 `lineValues` 補算，缺少爻值時以本卦 fallback。

備份規則：舊 `i_ching_records.xml` 與新的 Room DB / WAL / SHM 已排除 cloud backup；device-to-device transfer 繼續允許 settings 與 database。

### UI 狀態持有

新增 ViewModel：

- `RecordsViewModel`：提供 records LiveData，並以 LiveData event 回報筆記更新與單筆刪除結果。
- `ResultViewModel`：處理 result auto-save 與筆記保存，並以 LiveData event 回報 auto-save / note-save 狀態。
- `ProfileSettingsViewModel`：非同步準備紀錄匯出與 delete-all，並以 LiveData event 交給 Fragment 啟動 SAF 或顯示結果。

`DailyFragment`、`QuestionFragment`、`MethodFragment`、`RitualFragment`、`RecordsFragment`、`LearnCenterFragment`、`HexagramDetailFragment`、`ResultFragment`、`ProfileSettingsFragment` 的主要畫面結構已改用 XML/ViewBinding。Fragment 目前只負責 bind state、註冊 listener、呼叫 ViewModel、repository-backed helpers 或 navigation helpers。

`RecordsFragment` 與 `LearnCenterFragment` 已改用 RecyclerView `ListAdapter` / `DiffUtil`，並保留 stable item IDs、既有搜尋/篩選規則、收藏狀態、紀錄編輯與刪除行為。
程式化 View 與 XML layout 已補常用穩定 resource id，涵蓋提問、占法卡片、儀式略過、結果儲存/分享、紀錄搜尋/列表、篩選 chip、設定匯出/刪除與 bottom nav，供 Espresso workflows 使用。

結果頁分享文字/變爻摘要、紀錄卡顯示文字、學習中心收藏按鈕、Daily card、Question presets、Method options、Ritual reduce-motion 狀態、Hexagram list item 與 Hexagram detail sections 已抽到 `ui/presentation/` 的純 Java mapper，讓 UI 文案組合與狀態決策可用 JVM tests 驗證，不必透過 Fragment workflow 才能覆蓋。

「減少動態效果」目前會實際影響 `RitualFragment`：開啟時不再執行三秒 scale/alpha 動畫與長按等待，改為短反饋與短延遲完成；關閉時維持原本長按儀式。

### 設計資源

目前已有：

- `values/colors.xml`
- `values-night/colors.xml`
- `values/themes.xml`
- `values-night/themes.xml`
- `values/dimens.xml`

ViewBinding 已啟用，並已用於 `fragment_daily.xml`、`fragment_question.xml`、`fragment_method.xml`、`fragment_ritual.xml`、`fragment_records.xml`、`fragment_learn_center.xml`、`fragment_hexagram_detail.xml`、`fragment_result.xml`、`fragment_profile_settings.xml`。Splash、Onboarding 與本機模式入口仍保留少量 programmatic View，`Ui` 仍是過渡 helper，但 Beta 2 主要頁面內容不再由它整頁生成。尚未導入 bundled font、正式 vector/icon asset system 或 production-ready 墨洗資產。

本輪已新增 `HexagramView` 自訂 View，集中處理六爻卦象繪製，`Ui.hexagramView(...)` 目前作為相容 factory。也已建立並開始使用 XML component foundations：

- `include_top_bar.xml`
- `include_bottom_nav.xml`
- `include_empty_state.xml`
- `item_record.xml`
- `row_settings.xml`
- `fragment_daily.xml`
- `fragment_question.xml`
- `fragment_method.xml`
- `fragment_ritual.xml`
- `fragment_records.xml`
- `fragment_learn_center.xml`
- `fragment_hexagram_detail.xml`
- `fragment_result.xml`
- `fragment_profile_settings.xml`
- `item_hexagram.xml`
- `item_detail_section.xml`
- card / pill / chip / input / bottom-nav drawable backgrounds

Beta 2 主要使用流程已切換到這批 layout；啟動與入口類畫面仍待後續整理。

### 測試

目前測試狀態：

- `IChingLogicTest`：覆蓋三枚銅錢爻值 bucket、蓍草爻值 bucket、乾/坤/謙/坎/既濟/未濟 pattern mapping、64 pattern 唯一性、簡易占法 line/result 一致性、變爻與之卦。
- `DivinationPersistenceTest`：覆蓋 `DivinationResult` / `DivinationRecord` JSON round-trip、舊紀錄 fallback、舊 JSON 由 lineValues 補算之卦、record upsert/update/delete，以及 auto-save 使用穩定 id 避免重複紀錄的資料層行為。
- `NavigationArgsTest`：覆蓋 Fragment argument 建立/讀取、fallback 與 result JSON snapshot round-trip。
- `RecordRepositoryTest`：覆蓋 Room entity mapper、匯出 JSON、匯出純文字、空/null 匯出邊界、Room v1 exported schema、repository async export callback 與 legacy JSON parser。
- `SettingsStoreTest`：以 Robolectric 覆蓋設定預設值、toggle persistence 與收藏加入/移除。
- `ProfileSettingsFragmentTest`：覆蓋 Profile SAF writer 的 UTF-8 內容寫入與 null content fallback。
- `HexagramRepositoryFilterTest`：覆蓋卦名、全名、標籤、摘要搜尋，上經/下經篩選與收藏篩選。
- `LocalRecordStoreFilterTest`：覆蓋紀錄搜尋問題/筆記/卦名/標籤、占法篩選與有無變爻篩選。
- `ResultPresentationTest`、`RecordCardPresentationTest`、`FavoriteHexagramPresentationTest`、`Beta2PresentationTest`：覆蓋結果分享/變爻摘要、紀錄卡關係/變爻/日期/操作 label、收藏符號與 TalkBack label，以及 Daily、Question presets、Method selected state、Hexagram list/detail 與 Ritual reduce-motion 狀態。
- `ExampleUnitTest`：Android Studio 預設範例。
- `ExampleInstrumentedTest`：Android Studio 預設 app context test。
- `RecordDaoInstrumentedTest`：Room in-memory DAO insert/update/delete-all 測試。
- `StableBetaWorkflowInstrumentedTest`：Espresso workflow tests，啟用 accessibility checks，覆蓋 onboarding → 本機 daily、占卜 → result auto-save → records、占法 selected state、result recreate 不重複 auto-save、records 搜尋/占法篩選狀態保留、紀錄筆記編輯/單筆刪除、學習中心搜尋/detail、學習中心收藏、深色模式切換、JSON/text SAF export contracts 與 profile delete-all。

Gradle managed device 已設定：

- `pixel2Api35DebugAndroidTest`
- `allDevicesDebugAndroidTest`

已驗證：

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` 通過。
- `./gradlew tasks --all` 可列出 `app:pixel2Api35DebugAndroidTest`。
- `./gradlew pixel2Api35DebugAndroidTest` 先前已在具備 managed-device system image 的環境通過；本輪未重跑 managed device。

未完成驗證：

- `./gradlew connectedDebugAndroidTest` 需要連接實機或 emulator；本輪未在無裝置環境執行。

備註：目前 AGP 9.2 managed-device setup 仍印出 `testedAbi` 提醒，即使 DSL 已指定 x86_64；此提醒未阻擋 `pixel2Api35DebugAndroidTest` 完成。

## 目前主要風險

### UI 技術債

Splash、Onboarding 與本機模式入口仍以 programmatic View 建立。這些畫面較小，但仍帶來以下風險：

- 版面可讀性不如 XML 或 Compose component tree。
- 複雜 responsive layout 不易維護。
- 視覺一致性仰賴 `Ui` helper，缺少正式 design system enforcement。

ViewBinding 已開啟，top bar、bottom nav、record item、hexagram item、settings row、empty state、detail section、result sections 與 Beta 2 主要 workflow 畫面已有 XML foundations；但 Splash、Onboarding、本機入口與部分 shared helper 仍待後續收斂。

### 導覽剩餘限制

Navigation Component 已導入，但仍有幾個限制：

- 尚未導入 Safe Args。
- Deep link 與複雜 saved state recovery 仍未完整設計。
- Bottom navigation 已由 XML include 承載結構並由 `Ui` 綁定事件，但仍不是 Material NavigationBarView。

### 資料完整性仍需深化

`HexagramRepository` 已補完整 64 卦 pattern、上下卦、卦辭與六爻爻辭，並移除 ordinal-bit fallback。不足之處轉為內容深度：現代解析仍是精簡 Beta 版，尚未納入彖傳、象傳、文言、互卦、綜卦、錯卦或完整註解體系。

### 本機儲存限制

Room 已取代紀錄 JSON SharedPreferences，但長期仍有不足：

- 尚未加密。
- 無 sync 或 conflict resolution。
- 無單筆使用者匯出範圍選擇。
- 無正式資料保護策略或隱私權政策。

### 測試不足

目前已補資料序列化、紀錄 mutation、Room mapper/export、學習中心 filter、presentation mapper 的 JVM tests，也補了 Room DAO instrumentation test 與第一批 stable-beta Espresso workflow tests，但仍缺少：

- 更完整的 Fragment navigation saved-state 與 RecyclerView action tests。
- SAF 寫入成功內容驗證目前涵蓋 writer 與 ACTION_CREATE_DOCUMENT contract，但尚未透過真實 document provider URI 做端到端驗證。
- 視覺截圖測試。
- 完整 accessibility 驗收；目前 Espresso checks 已啟用，但尚未覆蓋字級縮放、TalkBack 路徑與人工對比驗收。

### 產品化缺口

目前還不能視為 production-ready：

- 無真實登入。
- 無隱私權政策與資料使用說明。
- 無 release signing 設定。
- 無錯誤回報或 analytics。
- 無完整 accessibility。
- 無正式 icon/font/illustration assets。

## 建議下一步

### 短期：穩定本機 Beta

1. 在具備 emulator/system image 的環境執行 `pixel2Api35DebugAndroidTest`，並把 managed device 納入 CI 或固定本機驗收。
2. 補完整 SAF document provider 寫入、分享 intent、字級縮放、focus order 與更多 delete-all 邊界案例測試。
3. 延續 XML/ViewBinding 路線，整理 Splash、Onboarding、本機入口與更多 shared component。
4. 擴充 accessibility 驗收：focus order、字級縮放、TalkBack 與對比檢查。

### 中期：整理架構

1. 評估是否維持 `NavigationArgs` contract，或在後續導入 Safe Args。
2. 決定長期 UI 路線：延續 XML component library、之後再評估 Compose migration，或保留少量 programmatic primitive。
3. 抽出 design system：Typography、spacing、button、card、chip、hexagram renderer。
4. 評估 settings/favorites 是否改用 DataStore。
5. 深化 hexagram 資料內容與資料驗證測試。

### 長期：產品化

1. 補完整易傳內容、註解與多變爻解讀。
2. 支援互卦、綜卦、錯卦。
3. 若需要帳號，設計 auth、sync、資料加密與隱私模型。
4. 建立 screenshot regression tests 與 accessibility acceptance checklist。
5. 建立 release pipeline、版本策略與 changelog。

## 文件維護規則

每次完成以下變更時，請更新本文件：

- 新增或移除主要畫面。
- 改變資料儲存方式。
- 改變占卜演算法或 hexagram mapping。
- 補齊或改變 64 卦資料內容。
- 新增測試策略或 CI 流程。
- 修正本文件列出的已知不足。
