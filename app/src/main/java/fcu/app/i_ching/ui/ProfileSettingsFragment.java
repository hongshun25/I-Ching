package fcu.app.i_ching.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.SettingsStore;
import fcu.app.i_ching.databinding.FragmentProfileSettingsBinding;
import fcu.app.i_ching.databinding.RowSettingsBinding;

public class ProfileSettingsFragment extends Fragment {
    private ActivityResultLauncher<String> exportJsonLauncher;
    private ActivityResultLauncher<String> exportTextLauncher;
    private ProfileSettingsViewModel viewModel;
    private String pendingExportContent;
    private String pendingExportLabel;
    private FragmentProfileSettingsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileSettingsViewModel.class);
        exportJsonLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> writeExport(uri));
        exportTextLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"), uri -> writeExport(uri));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        SettingsStore settings = activity.settings();
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false);
        NavigationChrome.bind(activity, binding.topBar, binding.bottomNav, NavigationChrome.TAB_PROFILE);
        bindRows(settings);
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

    private void bindRows(SettingsStore settings) {
        addValueRow(binding.profileInfoGroup, "使用模式", "本機模式");
        addValueRow(binding.profileInfoGroup, "帳號同步", "未啟用");

        addToggleRow(binding.profileAppearanceGroup, "深色模式", R.id.profile_dark_mode_switch,
                settings.isDarkMode(), checked -> {
                    settings.setDarkMode(checked);
                    AppCompatDelegate.setDefaultNightMode(checked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
                });
        addValueRow(binding.profileAppearanceGroup, "字體大小", "適中");
        addToggleRow(binding.profileAppearanceGroup, "減少動態效果", R.id.profile_reduce_motion_switch,
                settings.isReduceMotion(), settings::setReduceMotion);

        addValueRow(binding.profileDivinationGroup, "預設占法", "金錢卦");
        addToggleRow(binding.profileDivinationGroup, "自動儲存", R.id.profile_auto_save_switch,
                settings.isAutoSave(), settings::setAutoSave);
        addValueRow(binding.profileDivinationGroup, "提醒通知", "未啟用");

        addActionRow(binding.profileDataGroup, "匯出 JSON", "完整本機紀錄", R.id.profile_export_json, v -> startJsonExport());
        addActionRow(binding.profileDataGroup, "匯出純文字", "方便閱讀與備份", R.id.profile_export_text, v -> startTextExport());
        addActionRow(binding.profileDataGroup, "刪除全部紀錄", "不可復原", R.id.profile_delete_all, v -> confirmDeleteAll());
    }

    private void addValueRow(LinearLayout parent, String label, String value) {
        RowSettingsBinding row = RowSettingsBinding.inflate(getLayoutInflater(), parent, false);
        row.settingsRowLabel.setText(label);
        row.settingsRowValue.setText((value == null ? "" : value) + " ›");
        row.settingsRowValue.setVisibility(View.VISIBLE);
        row.settingsRowToggle.setVisibility(View.GONE);
        parent.addView(row.getRoot());
    }

    private void addToggleRow(LinearLayout parent, String label, int id, boolean checked, Toggle toggle) {
        RowSettingsBinding row = RowSettingsBinding.inflate(getLayoutInflater(), parent, false);
        row.settingsRowLabel.setText(label);
        row.settingsRowValue.setVisibility(View.GONE);
        row.settingsRowToggle.setId(id);
        row.settingsRowToggle.setContentDescription(label);
        row.settingsRowToggle.setVisibility(View.VISIBLE);
        row.settingsRowToggle.setChecked(checked);
        row.settingsRowToggle.setOnCheckedChangeListener((buttonView, isChecked) -> toggle.changed(isChecked));
        parent.addView(row.getRoot());
    }

    private void addActionRow(LinearLayout parent, String label, String value, int id, View.OnClickListener click) {
        RowSettingsBinding row = RowSettingsBinding.inflate(getLayoutInflater(), parent, false);
        row.getRoot().setId(id);
        row.getRoot().setClickable(true);
        row.getRoot().setFocusable(true);
        row.getRoot().setContentDescription(label + "，" + value);
        row.getRoot().setOnClickListener(click);
        row.settingsRowLabel.setText(label);
        row.settingsRowValue.setText(value + " ›");
        row.settingsRowValue.setVisibility(View.VISIBLE);
        row.settingsRowToggle.setVisibility(View.GONE);
        parent.addView(row.getRoot());
    }

    private void startJsonExport() {
        viewModel.exportJson();
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
                .setMessage("所有本機占卜紀錄與筆記都會被移除，此操作無法復原。")
                .setNegativeButton("取消", null)
                .setPositiveButton("刪除全部", (dialog, which) -> {
                    viewModel.deleteAllRecords();
                })
                .show();
    }

    private interface Toggle {
        void changed(boolean checked);
    }
}
