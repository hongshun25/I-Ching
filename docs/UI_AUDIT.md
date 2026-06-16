# UI Audit

更新日期：2026-06-16

## 範圍與標準

本次審計以 `design/stitch_export/` 的 `screen.png` 與 `code.html` 為設計意圖來源，採人工比對，不做像素級還原，也不導入截圖回歸工具。判斷重點是資訊架構、文案層級、紙感色票、卡片/按鈕/chip/input 語言、間距節奏、卦象主視覺與 light/night 風格。

已實作的工程處置：

- `fragment_question.xml`、`fragment_method.xml`、`fragment_result.xml` 已改成 XML 內完整 `ScrollView`/背景/padding shell，Fragment 直接回傳 binding root。
- 主要 runtime-bound `TextView`、`EditText`、`RecyclerView` 與 item layout 已補 `tools:` preview sample。
- `HexagramView` 在 `isInEditMode()` 且尚未 `configure(...)` 時會繪製第 15 卦代表性卦象，避免 Layout Editor 空白。
- Daily trigram chips、Question presets、Hexagram detail chips/sections 已補 XML sample child；runtime 仍由現有 binding 邏輯清空後綁定真實資料。
- Profile/settings 固定 rows 已轉為 XML 結構，runtime 只綁定 switch 狀態與匯出/刪除 click handler。
- Bottom nav 已補 design-time icon/label，runtime 仍由 `NavigationChrome` 綁定目前分頁。
- 自訂 pill action 已統一由 clickable `TextView` + `IChingPrimaryButton` / `IChingSecondaryButton` style 呈現，避開 Material/AppCompat `Button` 的預設 background tint / text appearance 對文字可見性的干擾。

尚未在本次 CLI 環境完成的驗收：

- Android Studio Layout Editor 的 Design/Split 實際開啟檢查。
- Layout Validation 的 reference devices / font sizes 檢查。
- Emulator/managed-device 上以 Layout Inspector overlay 疊 Stitch `screen.png` 的視覺比對。

本輪 CLI 驗證：

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` 通過。
- `./gradlew pixel2Api35DebugAndroidTest` 通過 15/15；AGP 仍印出 `testedAbi` setup 提醒，但不阻擋任務完成。

## 審計矩陣

| 畫面 | Android XML | Stitch 來源 | 預覽狀態 | 設計偏移 | 處置與原因 |
| --- | --- | --- | --- | --- | --- |
| Splash | `fragment_splash.xml` | `splash_screen/` | 靜態非空白；補 `tools:context` | Stitch 以中文「易經」與卦象線條作主視覺；Android 目前使用 `I CHING` 與 tagline | 文件化。屬品牌呈現差異，後續應與正式字體/brand asset 一起收斂 |
| Onboarding | `fragment_onboarding.xml`, `item_onboarding_page.xml` | `onboarding_flow/` | RecyclerView 補 `tools:listitem` / `tools:itemCount`，item 補 sample | Stitch 有墨洗 bitmap 感；Android 以簡化符號呈現 | 預覽已修正；正式墨洗資產 pipeline 文件化為未來工作 |
| 本機入口 | `fragment_local_entry.xml` | `login_register/` | 靜態非空白；補 `tools:context` | Stitch 是登入/註冊表單；Android 改為本機模式入口 | 文件化。Authentication 未實作，本機模式是目前產品決策 |
| Daily light | `fragment_daily.xml` | `daily_insight/` | runtime 文字補 `tools:text`，兩個 trigram chip 補 XML sample，卦象由 `HexagramView` fallback 顯示 | 缺正式 Material icons、字體與墨洗/紙張 asset | 小修預覽；asset/font/icon 差異文件化 |
| Daily dark | `fragment_daily.xml` + night colors | `daily_insight_dark_mode/` | 同 Daily light | Android 依 night resources 套色，未做完整 dark-specific layout | 文件化。Beta 保留同 layout，後續可做專屬 dark-mode polish |
| Step 1 Question | `fragment_question.xml` | `step_1_question/` | 改為完整 XML `ScrollView` shell；input/count/presets 補 sample | Stitch 有 flow header / close icon；Android 保持簡化流程頁 chrome | 小修預覽與 shell；flow chrome 差異文件化 |
| Step 2 Method | `fragment_method.xml` | `step_2_method/` | 改為完整 XML `ScrollView` shell；預設選中的三枚銅錢卡在 XML 可見 | Stitch 有 icon-enhanced cards；Android 以文字卡與 selected label 表達 | 小修預覽；正式 icon system 未導入，文件化 |
| Step 3 Ritual | `fragment_ritual.xml` | `step_3_ritual/` | 靜態非空白；補 `tools:context` | Layout Editor 無法呈現長按/動畫狀態 | 文件化。互動狀態以 Espresso workflow 覆蓋，視覺動畫需人工驗收 |
| Result | `fragment_result.xml` | `divination_result/` | 改為完整 XML `ScrollView` shell；主要欄位補 sample，卦象可預覽 | Stitch 有 top app bar / desktop two-column intent；Android 為 mobile single-column cards | 小修預覽；responsive/tablet polish 文件化 |
| Records empty | `fragment_records.xml`, `include_empty_state.xml` | `empty_state/` | empty state include 補 `tools:showIn`；records list 另補 populated sample | Stitch 只提供 empty state，缺 populated records 專屬稿 | 預覽已補；source 限制文件化 |
| Records populated | `fragment_records.xml`, `item_record.xml` | 無專屬 populated Stitch 稿 | RecyclerView 補 `tools:listitem` / sample item text | 無法逐稿比對 populated item | 依現有 design system 審核，記錄來源限制 |
| Learn center | `fragment_learn_center.xml`, `item_hexagram.xml` | `learn_center/` | RecyclerView 補 `tools:listitem` / sample item text，篩選 chip 預覽 selected | 缺正式 favorite icon / Material icon asset | 小修預覽；icon asset 文件化 |
| Hexagram detail | `fragment_hexagram_detail.xml`, `item_detail_section.xml` | `hexagram_detail/` | 兩個 chips 與六個 sections 補 XML sample；卦象可預覽 | Stitch 有 richer section layout/icons；Android 使用簡化 card sections | 小修預覽；richer section/icon system 文件化 |
| Profile/settings | `fragment_profile_settings.xml`, `row_settings.xml` | `profile_settings/` | 固定 rows 已轉 XML；switch/action IDs 保留；row component 補 sample | Stitch 的帳號/密碼項目不適用本機 Beta | 小修預覽；auth/account 差異文件化 |
| Top / bottom chrome | `include_top_bar.xml`, `include_bottom_nav.xml` | 多個 Stitch top/bottom bars | bottom nav 補 design-time label/icon；top bar 補 `tools:showIn` | Android 使用文字符號近似 Material Symbols | 小修預覽；正式 icon system 文件化 |

## 文件化例外

- 登入/註冊：Stitch 的 `login_register` 不直接落地，因目前 Beta 沒有 backend/auth/sync；`LocalEntryFragment` 是真實入口。
- 字體：Stitch 使用 Noto Sans / Noto Serif；Android 目前用系統 sans/serif，未 bundled Noto Sans TC / Noto Serif TC。
- Icons：Stitch 使用 Material Symbols；Android 目前多用文字符號近似。
- 墨洗/紙張資產：Stitch 有 ink wash 與更細緻視覺素材；Android 尚未建立 production-ready raster/vector asset pipeline。
- Records populated：目前沒有專屬 Stitch populated records screen，只能依 `empty_state` 與既有 design system 判斷。
- Layout Editor 限制：runtime 資料、LiveData、RecyclerView adapter 狀態、動畫、長按儀式與 navigation selected state 只能透過 sample data 或 runtime 測試近似。

## 後續建議

1. 在 Android Studio 逐一開啟主要 layout 的 Design/Split preview，確認無 render crash、空白 custom view、空白 list item 或 bottom nav。
2. 用 Layout Validation 檢查 Daily、Result、Records、Learn、Profile 的 reference devices 與 font sizes。
3. 建立正式 font/icon/ink asset pipeline 後，重新審計 Splash、Onboarding、Daily、Method、Profile 的視覺差異。
4. 若要收斂 Result tablet/desktop intent，再新增 dedicated wide layout 或 ConstraintLayout-based responsive spec。
5. 未導入 screenshot regression 前，每次明顯 UI 調整都應更新本文件或附人工驗收紀錄。

## 參考來源

- Android `tools:` attributes: https://developer.android.com/studio/views/tool-attributes-views
- Android Studio Layout Editor: https://developer.android.com/studio/views/layout-editor
- Layout Inspector: https://developer.android.com/studio/debug/layout-inspector
- Custom views: https://developer.android.com/develop/ui/views/layout/custom-views/create-view
