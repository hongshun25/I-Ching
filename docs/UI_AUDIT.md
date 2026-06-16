# UI Audit

更新日期：2026-06-17

## Scope

本文件記錄 UI 技術債清理後的狀態。審計標準仍以 `design/stitch_export/` 的 Stitch screens 作為資訊架構與視覺方向參考，但 Android 實作不是逐像素轉譯。Production UI 必須維持原生 Android、XML/ViewBinding、Material/AppCompat、local-first assets。

## Completed Refactor

- Top bar：`include_top_bar.xml` 改為 `MaterialToolbar`。
- Bottom nav：`include_bottom_nav.xml` 改為 `BottomNavigationView` + `res/menu/bottom_nav.xml`。
- Lists：`RecordsFragment` / `LearnCenterFragment` 移除 nested scrolling list 結構。
- Chips：records filters、learn filters、question presets、daily trigram chips、hexagram detail chips 改用 Material `Chip`/`ChipGroup` 或 shared chip inflation。
- Icons：toolbar、bottom nav、favorite、method cards、empty state、ritual focus、button prefix/arrow affordances 改為 VectorDrawable/ImageView/ImageButton。
- Typography：打包 Noto Sans TC / Noto Serif CJK TC，theme/styles 使用 committed font resources。
- Texture/image：打包 light/night paper texture 與 Met Open Access artwork；build 不依賴網路。
- `Ui.java`：已刪除。`HexagramView` 保留，並內聚 dp/color behavior 與 XML attrs。
- Dialog：record note edit 改用 `dialog_edit_note.xml`。
- Guards：新增 `UiDebtGuardTest` 與 `AssetManifestTest`。

## Remaining Exceptions

| Area | Status | Rationale / Next Step |
| --- | --- | --- |
| Stitch auth screens | Not implemented | Beta 實際模式是本機模式，auth/sync 不在本輪 scope。 |
| Pixel parity | Not targeted | 目前追求 native Android consistency 與 workflow fidelity，不做 pixel-perfect Stitch clone。 |
| Screenshot regression | Not implemented | 成本與工具鏈風險較高；先以 JVM guards、instrumentation workflow、Layout Validation 文件化補位。 |
| Layout Validation | Pending manual IDE check | CLI 無法替代 Android Studio Layout Validation；明顯 UI 變更後需補人工紀錄。 |
| Accessibility manual pass | Pending | Espresso checks 已啟用，但 TalkBack、focus order、font scale、contrast 仍需人工驗收。 |
| Rich artwork set | Partial | 已導入一張 Public Domain artwork 與紙張紋理；更多畫面插圖需逐一走 manifest/license pipeline。 |

## Screen Matrix

| Screen | Refactor State | Notes |
| --- | --- | --- |
| Splash | XML/ViewBinding + bundled font/page texture | Brand art 仍可後續強化。 |
| Onboarding | RecyclerView pages use drawable-backed art; indicator is stateful View dots | First page uses Met Open Access artwork; other pages use committed vectors. |
| Local Entry | XML/ViewBinding, local-first copy | Keeps product divergence from Stitch login/register. |
| Daily | Material toolbar/bottom nav, ChipGroup trigrams, drawable-backed buttons | Light/night page texture applied. |
| Question | Material preset chips, drawable arrow button affordance | No text-symbol icons remain. |
| Method | Drawable-backed method cards and selected state | Presentation exposes icon resource ids. |
| Ritual | ImageView focus ring vector | Reduce-motion behavior remains covered by tests. |
| Result | XML/ViewBinding result shell retained | Semantic relation arrows in reading text remain allowed. |
| Records | Header/filter + weighted RecyclerView content area | Empty state art and action icon are drawables. |
| Learn Center | Header/filter + weighted RecyclerView content area | Favorite state is ImageButton drawable state. |
| Hexagram Detail | MaterialToolbar back/favorite menu, Material chips | Favorite state uses drawable resource id. |
| Profile/Settings | XML rows retained | Auth/account rows intentionally omitted. |

## Verification

已執行：

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest`
- `./gradlew pixel2Api35DebugAndroidTest` 15/15

## Asset Sources

Asset details live in:

- `tools/assets/asset_manifest.json`
- `docs/ASSET_PIPELINE.md`
- `docs/ASSET_LICENSES.md`

No production asset may be added without source, license, target path, transform notes, and checksum.
