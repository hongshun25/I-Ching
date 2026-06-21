package fcu.app.i_ching.ui;

import android.Manifest;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.data.AccountStore;
import fcu.app.i_ching.data.AppSettings;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.RecordRepository;
import fcu.app.i_ching.data.ReminderScheduler;
import fcu.app.i_ching.data.SettingsStore;
import fcu.app.i_ching.databinding.DialogChangePasswordBinding;
import fcu.app.i_ching.databinding.FragmentProfileSettingsBinding;

public class ProfileSettingsFragment extends Fragment {
    private ActivityResultLauncher<String> exportJsonLauncher;
    private ActivityResultLauncher<String> exportTextLauncher;
    private ActivityResultLauncher<String> reminderPermissionLauncher;
    private ProfileSettingsViewModel viewModel;
    private String pendingExportContent;
    private String pendingExportLabel;
    private FragmentProfileSettingsBinding binding;
    private boolean updatingReminderSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileSettingsViewModel.class);
        exportJsonLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> writeExport(uri));
        exportTextLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"), uri -> writeExport(uri));
        reminderPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            MainActivity activity = (MainActivity) requireActivity();
            if (Boolean.TRUE.equals(granted)) {
                enableDailyReminder(activity, activity.settings());
            } else {
                activity.settings().setDailyReminderEnabled(false);
                new ReminderScheduler(requireContext()).cancel();
                updateReminderViews(activity.settings());
                Toast.makeText(requireContext(), "未允許通知，已關閉每日提醒。", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        SettingsStore settings = activity.settings();
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false);
        NavigationChrome.bind(activity, binding.topBar, binding.bottomNav, NavigationChrome.TAB_PROFILE);
        bindRows(activity, settings);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.exportEvents().observe(getViewLifecycleOwner(), event -> {
            ProfileSettingsViewModel.ExportState state = event.getContentIfNotHandled();
            if (state == null) return;
            preparePendingExport(state.content, state.label);
            if (state.kind == ProfileSettingsViewModel.ExportKind.JSON) {
                exportJsonLauncher.launch(state.fileName);
            } else {
                exportTextLauncher.launch(state.fileName);
            }
        });
        viewModel.deleteAllEvents().observe(getViewLifecycleOwner(), event -> {
            Boolean success = event.getContentIfNotHandled();
            if (success == null) return;
            Toast.makeText(requireContext(), success ? "全部紀錄已刪除" : "刪除失敗", Toast.LENGTH_SHORT).show();
        });
    }

    private void bindRows(MainActivity activity, SettingsStore settings) {
        bindAccountRows(activity);
        binding.profileDarkModeSwitch.setChecked(settings.isDarkMode());
        binding.profileDarkModeSwitch.setOnCheckedChangeListener((buttonView, checked) -> {
            settings.setDarkMode(checked);
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        });
        binding.profileReduceMotionSwitch.setChecked(settings.isReduceMotion());
        binding.profileReduceMotionSwitch.setOnCheckedChangeListener((buttonView, checked) ->
                settings.setReduceMotion(checked));
        binding.profileAutoSaveSwitch.setChecked(settings.isAutoSave());
        binding.profileAutoSaveSwitch.setOnCheckedChangeListener((buttonView, checked) ->
                settings.setAutoSave(checked));
        binding.profileFontSizeValue.setText(settings.fontScale().label);
        binding.profileFontSize.setOnClickListener(v -> showFontScaleDialog(activity, settings));
        binding.profileDefaultMethodValue.setText(settings.defaultMethod().label);
        binding.profileDefaultMethod.setOnClickListener(v -> showDefaultMethodDialog(settings));
        updateReminderViews(settings);
        binding.profileReminderSwitch.setOnCheckedChangeListener((buttonView, checked) -> {
            if (updatingReminderSwitch) return;
            if (checked) {
                ReminderScheduler scheduler = new ReminderScheduler(requireContext());
                if (scheduler.canPostNotifications()) {
                    enableDailyReminder(activity, settings);
                } else if (Build.VERSION.SDK_INT >= 33) {
                    reminderPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            } else {
                settings.setDailyReminderEnabled(false);
                new ReminderScheduler(requireContext()).cancel();
                updateReminderViews(settings);
            }
        });
        binding.profileReminderTime.setOnClickListener(v -> showReminderTimeDialog(settings));

        binding.profileExportJson.setOnClickListener(v -> startJsonExport());
        binding.profileExportText.setOnClickListener(v -> startTextExport());
        binding.profileDeleteAll.setOnClickListener(v -> confirmDeleteAll());
    }

    private void bindAccountRows(MainActivity activity) {
        AccountStore.Account account = activity.accounts().currentAccount();
        boolean guest = account.isGuest();
        binding.profileAccountModeValue.setText(guest ? "本機模式" : "本機帳號");
        binding.profileAccountEmailValue.setText(guest ? "未登入" : account.email);
        binding.profileAuthEntry.setVisibility(guest ? View.VISIBLE : View.GONE);
        binding.profileChangePassword.setVisibility(guest ? View.GONE : View.VISIBLE);
        binding.profileLogout.setVisibility(guest ? View.GONE : View.VISIBLE);
        binding.profileDeleteAccount.setVisibility(guest ? View.GONE : View.VISIBLE);
        binding.profileAuthEntry.setOnClickListener(v -> activity.showAuth());
        binding.profileChangePassword.setOnClickListener(v -> showChangePasswordDialog(activity));
        binding.profileLogout.setOnClickListener(v -> {
            activity.accounts().useGuest();
            activity.showAuth();
        });
        binding.profileDeleteAccount.setOnClickListener(v -> confirmDeleteAccount(activity));
    }

    private void startJsonExport() {
        viewModel.exportJson();
    }

    private void showFontScaleDialog(MainActivity activity, SettingsStore settings) {
        AppSettings.FontScale[] values = AppSettings.FontScale.values();
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) labels[i] = values[i].label;
        new AlertDialog.Builder(requireContext())
                .setTitle("字體大小")
                .setItems(labels, (dialog, which) -> {
                    settings.setFontScale(values[which]);
                    activity.recreate();
                })
                .show();
    }

    private void showDefaultMethodDialog(SettingsStore settings) {
        DivinationMethod[] values = new DivinationMethod[]{
                DivinationMethod.SIMPLE,
                DivinationMethod.COINS,
                DivinationMethod.YARROW
        };
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) labels[i] = values[i].label;
        new AlertDialog.Builder(requireContext())
                .setTitle("預設占法")
                .setItems(labels, (dialog, which) -> {
                    settings.setDefaultMethod(values[which]);
                    if (binding != null) binding.profileDefaultMethodValue.setText(values[which].label);
                })
                .show();
    }

    private void showReminderTimeDialog(SettingsStore settings) {
        TimePickerDialog dialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    settings.setDailyReminderTime(hourOfDay, minute);
                    if (settings.isDailyReminderEnabled()) {
                        new ReminderScheduler(requireContext()).schedule(hourOfDay, minute);
                    }
                    updateReminderViews(settings);
                },
                settings.dailyReminderHour(),
                settings.dailyReminderMinute(),
                true
        );
        dialog.show();
    }

    private void enableDailyReminder(MainActivity activity, SettingsStore settings) {
        ReminderScheduler scheduler = new ReminderScheduler(requireContext());
        settings.setDailyReminderEnabled(true);
        scheduler.schedule(settings.dailyReminderHour(), settings.dailyReminderMinute());
        updateReminderViews(settings);
        Toast.makeText(requireContext(), "每日提醒已啟用", Toast.LENGTH_SHORT).show();
    }

    private void updateReminderViews(SettingsStore settings) {
        if (binding == null) return;
        updatingReminderSwitch = true;
        binding.profileReminderSwitch.setChecked(settings.isDailyReminderEnabled());
        updatingReminderSwitch = false;
        binding.profileReminderTimeValue.setText(formatTime(settings.dailyReminderHour(), settings.dailyReminderMinute()));
        binding.profileReminderTime.setEnabled(settings.isDailyReminderEnabled());
        binding.profileReminderTime.setAlpha(settings.isDailyReminderEnabled() ? 1f : 0.55f);
    }

    private String formatTime(int hour, int minute) {
        return String.format(java.util.Locale.TAIWAN, "%02d:%02d", hour, minute);
    }

    private void startTextExport() {
        viewModel.exportText();
    }

    void writeExport(Uri uri) {
        String content = pendingExportContent;
        String label = pendingExportLabel;
        clearPendingExport();
        if (uri == null || content == null) return;
        try (OutputStream stream = requireContext().getContentResolver().openOutputStream(uri)) {
            writeExportContent(stream, content);
            Toast.makeText(requireContext(), (label == null ? "" : label) + "已匯出", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "匯出失敗", Toast.LENGTH_SHORT).show();
        }
    }

    static void writeExportContent(OutputStream stream, String content) throws IOException {
        if (stream == null) throw new IOException("Cannot open export target");
        OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        writer.write(content == null ? "" : content);
        writer.flush();
    }

    void preparePendingExport(String content, String label) {
        pendingExportContent = content;
        pendingExportLabel = label;
    }

    boolean hasPendingExport() {
        return pendingExportContent != null || pendingExportLabel != null;
    }

    private void clearPendingExport() {
        pendingExportContent = null;
        pendingExportLabel = null;
    }

    private void confirmDeleteAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle("刪除全部紀錄？")
                .setMessage("目前帳號的占卜紀錄與筆記都會被移除，此操作無法復原。")
                .setNegativeButton("取消", null)
                .setPositiveButton("刪除全部", (dialog, which) -> {
                    viewModel.deleteAllRecords();
                })
                .show();
    }

    private void showChangePasswordDialog(MainActivity activity) {
        DialogChangePasswordBinding dialogBinding = DialogChangePasswordBinding.inflate(getLayoutInflater());
        new AlertDialog.Builder(requireContext())
                .setTitle("更改密碼")
                .setView(dialogBinding.getRoot())
                .setNegativeButton("取消", null)
                .setPositiveButton("儲存", (dialog, which) -> {
                    AccountStore.AuthResult result = activity.accounts().changePassword(
                            dialogBinding.changePasswordCurrent.getText().toString(),
                            dialogBinding.changePasswordNew.getText().toString(),
                            dialogBinding.changePasswordConfirm.getText().toString()
                    );
                    Toast.makeText(requireContext(), result.success ? "密碼已更新" : result.message, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void confirmDeleteAccount(MainActivity activity) {
        new AlertDialog.Builder(requireContext())
                .setTitle("刪除帳號？")
                .setMessage("此本機帳號、紀錄、收藏與設定都會從這台裝置移除，此操作無法復原。")
                .setNegativeButton("取消", null)
                .setPositiveButton("刪除帳號", (dialog, which) -> {
                    String accountId = activity.accounts().activeAccountId();
                    activity.accounts().deleteCurrentAccount();
                    activity.settings().clearAccount(accountId);
                    RecordRepository.get(requireContext()).deleteAccountData(accountId, success -> {
                        Toast.makeText(requireContext(), "本機帳號已刪除", Toast.LENGTH_SHORT).show();
                        activity.showAuth();
                    });
                })
                .show();
    }
}
