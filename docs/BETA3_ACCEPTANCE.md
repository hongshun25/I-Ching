# Beta 3 Acceptance

更新日期：2026-06-16

## 範圍

Beta 3 穩定化維持既有技術棧：Java、AndroidX Fragment、Navigation Component、Room、XML/ViewBinding、Material/AppCompat。此階段不導入 Compose、WebView、後端、帳號同步、加密資料庫或 Room v2 schema。

## 必須通過

- 本機功能仍完整：splash、onboarding、本機模式入口、daily、三步驟占卜、結果頁、紀錄、學習中心、卦象詳情、profile/settings、匯出與刪除。
- `SplashFragment`、`OnboardingFragment`、`LocalEntryFragment` 使用 XML/ViewBinding。
- navigation graph 使用 `localEntryFragment`；不保留假登入或 `AuthFragment` 命名。
- Daily、Records、Learn、Profile 的 top bar / bottom nav 由 XML include 承載並由 binder 綁定，不再由 `Ui.pageWithChrome()` 生成。
- `Ui` 只保留小型 primitive / helper；不得重新加入整頁 chrome helper。
- Room schema 維持 v1，`app/schemas/` 保持 committed schema。
- export 取消時必須清空 pending export state。
- SAF JSON/text 匯出必須透過 provider-backed instrumentation test 驗證實際寫入內容。
- Espresso workflow accessibility checks 由 root view 執行；若未來需要 suppression，必須窄範圍且有註解。
- Cloud backup 仍排除 `i_ching_records.xml` 與 `i_ching_records.db` / WAL / SHM。
- `.github/workflows/android-beta.yml` 執行 JVM test、lint、debug APK、debug androidTest APK build。
- `.github/workflows/android-managed-device.yml` 提供 manual/nightly managed-device 驗收。

## 驗證命令

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

## Deferred

- 不做真實登入、帳號、同步、後端或加密資料庫。
- 不導入 Safe Args；`NavigationArgs` 仍是目前 Fragment argument contract。
- 不升級 Room schema。
- 不建立 screenshot regression、完整 TalkBack 人工驗收、release signing、隱私權政策或 production asset pipeline。
