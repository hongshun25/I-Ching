# Beta 3 Acceptance

更新日期：2026-06-17

## Status

此文件保留作歷史驗收紀錄。Beta 3 穩定化已完成；後續 UI 技術債清理與 asset pipeline 狀態以 `docs/UI_AUDIT.md`、`docs/ASSET_PIPELINE.md`、`docs/ASSET_LICENSES.md`、`docs/PROJECT_STATUS.md` 為準。

## Historical Beta 3 Scope

Beta 3 穩定化維持既有技術棧：Java、AndroidX Fragment、Navigation Component、Room、XML/ViewBinding、Material/AppCompat。此階段不導入 Compose、WebView、後端、帳號同步、加密資料庫或 Room v2 schema。

Beta 3 必須完成：

- 本機功能完整：splash、onboarding、本機模式入口、daily、三步驟占卜、結果頁、紀錄、學習中心、卦象詳情、profile/settings、匯出與刪除。
- `SplashFragment`、`OnboardingFragment`、`LocalEntryFragment` 使用 XML/ViewBinding。
- Navigation graph 使用 `localEntryFragment`；不保留假登入或 `AuthFragment` 命名。
- Room schema 維持 v1，`app/schemas/` 保持 committed schema。
- Export 取消時必須清空 pending export state。
- SAF JSON/text 匯出必須透過 provider-backed instrumentation test 驗證實際寫入內容。
- Espresso workflow accessibility checks 由 root view 執行。
- Cloud backup 仍排除 `i_ching_records.xml` 與 `i_ching_records.db` / WAL / SHM。

## UI Refactor Acceptance

本輪新增驗收：

- Top bar 使用 `MaterialToolbar`。
- Bottom nav 使用 `BottomNavigationView` + menu XML。
- Records / Learn 不再有 `RecyclerView` inside `ScrollView`。
- Chips 使用 Material `Chip`/`ChipGroup` 或 shared chip layout inflation。
- Production UI 不新增 allowlist 外 icon-like text symbols。
- Production font/icon/image/texture assets 必須有 manifest、license、source URL、transform notes、checksum。
- `Ui.java` 不再存在；`HexagramView` 是保留的 custom rendering primitive。
- `FavoriteHexagramPresentation` / list/detail presentation 不再輸出 `♥/♡` 字串。

## Verification Commands

本機基線：

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest
```

具備 emulator / managed-device system image 時：

```bash
./gradlew pixel2Api35DebugAndroidTest
```

連接實機或 emulator 時：

```bash
./gradlew connectedDebugAndroidTest
```

## Still Deferred

- 不做真實登入、帳號、同步、後端或加密資料庫。
- 不導入 Safe Args；`NavigationArgs` 仍是目前 Fragment argument contract。
- 不升級 Room schema。
- 不建立 full screenshot regression、完整 TalkBack 人工驗收、release signing 或隱私權政策。
