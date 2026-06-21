# UI Audit

更新日期：2026-06-22

## Scope

本文件記錄 UI 技術債清理後的狀態。審計標準仍以 `design/stitch_export/` 的 Stitch screens 作為資訊架構與視覺方向參考，但 Android 實作不是逐像素轉譯。Production UI 必須維持原生 Android、XML/ViewBinding、Material/AppCompat、local-first assets。

## Completed Refactor

- Top bar：`include_top_bar.xml` 改為 `MaterialToolbar`。
- Bottom nav：`include_bottom_nav.xml` 改為 `BottomNavigationView` + `res/menu/bottom_nav.xml`。
- Lists：`RecordsFragment` / `LearnCenterFragment` 移除 nested scrolling list 結構。
- Chips：records filters、learn filters、question presets、daily trigram chips、hexagram detail chips 改用 Material `Chip`/`ChipGroup` 或 shared chip inflation。
- Icons：toolbar、bottom nav、favorite、method cards、empty state、ritual focus、button prefix/arrow affordances 改為 VectorDrawable/ImageView/ImageButton。
- Typography：打包 Noto Sans TC / Noto Serif TC variable fonts，theme/styles 使用 family resources，不再從 UI 直接引用 raw font files。
- Texture/image：打包 light/night paper texture 與 Met Open Access artwork；build 不依賴網路。
- Stitch alignment pass：補上 project-owned onboarding ink-wash assets、較平的 paper card radius、wrap chips、question/result pill hierarchy、result blind-spot card、daily 宜忌卡、日期驅動每日一卦、原生 ritual focus view 與蓍草十八變步進畫面。
- Native UX refinement pass：`NavigationChrome` 統一 system inset padding，top-level toolbar 移除無功能 hamburger，bottom nav active item 改用 tint-only state，Daily 的「今日想問」成為 Step 1 提問草稿。
- Safe areas：fullscreen splash/onboarding/auth/local-entry/question/method/ritual/result flows use a shared `InsetsHelper`; bottom nav height/icon/label sizing moved to `dimens.xml`.
- Controls：CTA/action rows using app button styles are now `MaterialButton`; guards block styled clickable TextView controls from returning.
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
| Splash | XML/ViewBinding + bundled font/page texture + native hexagram mark | Brand lockup follows Stitch splash hierarchy. |
| Onboarding | RecyclerView pages use committed circular ink-wash WebP art; indicator is stateful View dots | Project-owned Stitch-assisted assets are documented in the manifest. |
| Local Entry | XML/ViewBinding, local-first copy in login/register-inspired card | Keeps product divergence from Stitch auth; no fake login. |
| Daily | Material toolbar/bottom nav, ChipGroup trigrams, drawable-backed buttons, compact 宜忌 guide cards, question draft input | Uses local-date-driven `DailyInsightProvider`; draft is passed to Question only and is not persisted as a daily note. |
| Question | Material preset chips, drawable arrow button affordance, optional draft prefill | Empty draft remains empty until the user submits; method navigation still normalizes blank questions. |
| Method | Drawable-backed method cards and selected state | Presentation exposes icon resource ids. |
| Ritual / Yarrow | Custom native `RitualFocusView`; Yarrow method then enters an 18-step XML/ViewBinding screen | Reduce-motion behavior and Yarrow session logic remain covered by tests. |
| Result | Question pill, lighter hero, changing-line highlight, stacked guide cards, presentation-driven blind-spot card, collapsible classical text | Semantic relation arrows in reading text remain allowed. |
| Records | Header/filter + weighted RecyclerView content area + mini hexagram cards | Empty state art and action icon are drawables. |
| Learn Center | Header/filter + weighted RecyclerView content area | Favorite state is ImageButton drawable state. |
| Hexagram Detail | MaterialToolbar back/favorite menu, Material chips | Favorite state uses drawable resource id. |
| Profile/Settings | XML rows for account, appearance, divination preferences, local reminder, exports, deletion | Font size/default method/reminder rows are interactive and account-scoped. |

## Verification

已執行：

Previous baseline:

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest`
- `./gradlew pixel2Api35DebugAndroidTest` 15/15

Current Stitch-alignment pass (2026-06-21):

- `./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` passed.
- Stitch alignment reinforcement:
  - `./gradlew testDebugUnitTest` passed.
  - `./gradlew lintDebug` passed.
  - `./gradlew assembleDebug` passed.
  - `./gradlew assembleDebugAndroidTest` passed.
  - `./gradlew pixel2Api35DebugAndroidTest` passed 19/19. AGP still prints the known `testedAbi` warning during managed-device setup.

Native UX refinement pass (2026-06-22):

- `./gradlew testDebugUnitTest` passed.
- `./gradlew lintDebug` passed.
- `./gradlew assembleDebug` passed.
- `./gradlew assembleDebugAndroidTest` passed.
- `./gradlew pixel2Api35DebugAndroidTest` passed 22/22. AGP still prints the known `testedAbi` warning during managed-device setup.

## Asset Sources

Asset details live in:

- `tools/assets/asset_manifest.json`
- `docs/ASSET_PIPELINE.md`
- `docs/ASSET_LICENSES.md`

No production asset may be added without source, license, target path, transform notes, and checksum.
