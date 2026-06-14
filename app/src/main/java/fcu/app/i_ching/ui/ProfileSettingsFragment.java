package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.SettingsStore;

public class ProfileSettingsFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        SettingsStore settings = activity.settings();
        LinearLayout content = Ui.column(requireContext());
        TextView title = Ui.text(requireContext(), "我的", 38, android.graphics.Typeface.NORMAL, R.color.ic_ink, true); title.setGravity(Gravity.CENTER); content.addView(title);
        TextView sub = Ui.text(requireContext(), "管理您的個人檔案與應用程式設定", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false); sub.setGravity(Gravity.CENTER); content.addView(sub);
        addGroupTitle(content, "個人資訊");
        LinearLayout info = Ui.card(requireContext()); addRow(info, "我的帳號", "本機模式", null); addRow(info, "更改密碼", "未啟用", null); content.addView(info);
        addGroupTitle(content, "外觀");
        LinearLayout appearance = Ui.card(requireContext());
        addRow(appearance, "深色模式", null, checked -> { settings.setDarkMode(checked); AppCompatDelegate.setDefaultNightMode(checked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO); });
        ((Switch) appearance.findViewWithTag("深色模式")).setChecked(settings.isDarkMode());
        addRow(appearance, "字體大小", "適中", null);
        addRow(appearance, "減少動態效果", null, checked -> settings.setReduceMotion(checked));
        ((Switch) appearance.findViewWithTag("減少動態效果")).setChecked(settings.isReduceMotion());
        Ui.addWithMargins(content, appearance, -1, -2, 0, 0, 0, 0);
        addGroupTitle(content, "占卜偏好");
        LinearLayout divination = Ui.card(requireContext()); addRow(divination, "預設占法", "金錢卦", null); addRow(divination, "自動儲存", null, checked -> settings.setAutoSave(checked)); ((Switch) divination.findViewWithTag("自動儲存")).setChecked(settings.isAutoSave()); addRow(divination, "提醒通知", "未啟用", null); content.addView(divination);
        return Ui.pageWithChrome(activity, content, "我的");
    }

    private void addGroupTitle(LinearLayout content, String text) { Ui.addWithMargins(content, Ui.text(requireContext(), text, 13, android.graphics.Typeface.BOLD, R.color.ic_outline_strong, false), -1, -2, 0, 30, 0, 8); }

    private void addRow(LinearLayout parent, String label, String value, Toggle toggle) {
        LinearLayout row = Ui.row(requireContext()); row.setPadding(0, Ui.dp(requireContext(), 8), 0, Ui.dp(requireContext(), 8));
        TextView l = Ui.text(requireContext(), label, 16, android.graphics.Typeface.NORMAL, R.color.ic_ink, false); row.addView(l, new LinearLayout.LayoutParams(0, -2, 1));
        if (toggle == null) { row.addView(Ui.text(requireContext(), value == null ? "›" : value + " ›", 14, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false)); }
        else { Switch sw = new Switch(requireContext()); sw.setTag(label); sw.setOnCheckedChangeListener((buttonView, isChecked) -> toggle.changed(isChecked)); row.addView(sw); }
        parent.addView(row, new LinearLayout.LayoutParams(-1, -2));
    }

    private interface Toggle { void changed(boolean checked); }
}
