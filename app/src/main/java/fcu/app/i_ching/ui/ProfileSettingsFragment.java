package fcu.app.i_ching.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
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

public class ProfileSettingsFragment extends Fragment {
    private ActivityResultLauncher<String> exportJsonLauncher;
    private ActivityResultLauncher<String> exportTextLauncher;
    private ProfileSettingsViewModel viewModel;
    private String pendingExportContent;
    private String pendingExportLabel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileSettingsViewModel.class);
        exportJsonLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> writeExport(uri));
        exportTextLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"), uri -> writeExport(uri));
    }

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        SettingsStore settings = activity.settings();
        LinearLayout content = Ui.column(requireContext());
        TextView title = Ui.text(requireContext(), "我的", 38, android.graphics.Typeface.NORMAL, R.color.ic_ink, true);
        title.setGravity(Gravity.CENTER);
        content.addView(title);
        TextView sub = Ui.text(requireContext(), "管理本機模式、外觀與資料控制", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
        sub.setGravity(Gravity.CENTER);
        content.addView(sub);

        addGroupTitle(content, "個人資訊");
        LinearLayout info = Ui.card(requireContext());
        addRow(info, "使用模式", "本機模式", null);
        addRow(info, "帳號同步", "未啟用", null);
        content.addView(info);

        addGroupTitle(content, "外觀");
        LinearLayout appearance = Ui.card(requireContext());
        addRow(appearance, "深色模式", null, checked -> {
            settings.setDarkMode(checked);
            AppCompatDelegate.setDefaultNightMode(checked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
        ((Switch) appearance.findViewWithTag("深色模式")).setChecked(settings.isDarkMode());
        addRow(appearance, "字體大小", "適中", null);
        addRow(appearance, "減少動態效果", null, checked -> settings.setReduceMotion(checked));
        ((Switch) appearance.findViewWithTag("減少動態效果")).setChecked(settings.isReduceMotion());
        Ui.addWithMargins(content, appearance, -1, -2, 0, 0, 0, 0);

        addGroupTitle(content, "占卜偏好");
        LinearLayout divination = Ui.card(requireContext());
        addRow(divination, "預設占法", "金錢卦", null);
        addRow(divination, "自動儲存", null, checked -> settings.setAutoSave(checked));
        ((Switch) divination.findViewWithTag("自動儲存")).setChecked(settings.isAutoSave());
        addRow(divination, "提醒通知", "未啟用", null);
        content.addView(divination);

        addGroupTitle(content, "資料控制");
        LinearLayout data = Ui.card(requireContext());
        addActionRow(data, "匯出 JSON", "完整本機紀錄", v -> startJsonExport());
        addActionRow(data, "匯出純文字", "方便閱讀與備份", v -> startTextExport());
        addActionRow(data, "刪除全部紀錄", "不可復原", v -> confirmDeleteAll());
        content.addView(data);

        return Ui.pageWithChrome(activity, content, "我的");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.exportEvents().observe(getViewLifecycleOwner(), event -> {
            ProfileSettingsViewModel.ExportState state = event.getContentIfNotHandled();
            if (state == null) return;
            pendingExportContent = state.content;
            pendingExportLabel = state.label;
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

    private void addGroupTitle(LinearLayout content, String text) {
        Ui.addWithMargins(content, Ui.text(requireContext(), text, 13, android.graphics.Typeface.BOLD, R.color.ic_outline_strong, false), -1, -2, 0, 30, 0, 8);
    }

    private void addRow(LinearLayout parent, String label, String value, Toggle toggle) {
        LinearLayout row = Ui.row(requireContext());
        row.setPadding(0, Ui.dp(requireContext(), 8), 0, Ui.dp(requireContext(), 8));
        TextView l = Ui.text(requireContext(), label, 16, android.graphics.Typeface.NORMAL, R.color.ic_ink, false);
        row.addView(l, new LinearLayout.LayoutParams(0, -2, 1));
        if (toggle == null) {
            row.addView(Ui.text(requireContext(), value == null ? "›" : value + " ›", 14, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        } else {
            Switch sw = new Switch(requireContext());
            sw.setId(toggleId(label));
            sw.setTag(label);
            sw.setOnCheckedChangeListener((buttonView, isChecked) -> toggle.changed(isChecked));
            row.addView(sw);
        }
        parent.addView(row, new LinearLayout.LayoutParams(-1, -2));
    }

    private void addActionRow(LinearLayout parent, String label, String value, View.OnClickListener click) {
        LinearLayout row = Ui.row(requireContext());
        row.setId(actionId(label));
        row.setPadding(0, Ui.dp(requireContext(), 10), 0, Ui.dp(requireContext(), 10));
        row.setClickable(true);
        row.setFocusable(true);
        row.setContentDescription(label + "，" + value);
        row.setOnClickListener(click);
        TextView l = Ui.text(requireContext(), label, 16, android.graphics.Typeface.NORMAL, R.color.ic_ink, false);
        row.addView(l, new LinearLayout.LayoutParams(0, -2, 1));
        row.addView(Ui.text(requireContext(), value + " ›", 14, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        parent.addView(row, new LinearLayout.LayoutParams(-1, -2));
    }

    private int toggleId(String label) {
        if ("深色模式".equals(label)) return R.id.profile_dark_mode_switch;
        if ("減少動態效果".equals(label)) return R.id.profile_reduce_motion_switch;
        if ("自動儲存".equals(label)) return R.id.profile_auto_save_switch;
        return View.NO_ID;
    }

    private int actionId(String label) {
        if ("匯出 JSON".equals(label)) return R.id.profile_export_json;
        if ("匯出純文字".equals(label)) return R.id.profile_export_text;
        if ("刪除全部紀錄".equals(label)) return R.id.profile_delete_all;
        return View.NO_ID;
    }

    private void startJsonExport() {
        viewModel.exportJson();
    }

    private void startTextExport() {
        viewModel.exportText();
    }

    private void writeExport(Uri uri) {
        if (uri == null || pendingExportContent == null) return;
        try (OutputStream stream = requireContext().getContentResolver().openOutputStream(uri);
             OutputStreamWriter writer = stream == null ? null : new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            if (writer == null) throw new IOException("Cannot open export target");
            writer.write(pendingExportContent);
            Toast.makeText(requireContext(), pendingExportLabel + "已匯出", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "匯出失敗", Toast.LENGTH_SHORT).show();
        } finally {
            pendingExportContent = null;
            pendingExportLabel = null;
        }
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

    private interface Toggle { void changed(boolean checked); }
}
