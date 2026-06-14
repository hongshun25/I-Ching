package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;

public class AuthFragment extends Fragment {
    private boolean registerMode;
    private LinearLayout form;

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout content = Ui.column(requireContext());
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 72), Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 24));
        TextView title = Ui.text(requireContext(), "I CHING", 40, android.graphics.Typeface.BOLD, R.color.ic_ink, true);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.14f);
        TextView subtitle = Ui.text(requireContext(), "古老智慧 現代心境", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
        subtitle.setGravity(Gravity.CENTER);
        content.addView(title, new LinearLayout.LayoutParams(-1, -2));
        Ui.addWithMargins(content, subtitle, -1, -2, 0, 4, 0, 36);
        form = Ui.card(requireContext());
        content.addView(form, new LinearLayout.LayoutParams(-1, -2));
        Button local = Ui.pill(requireContext(), "暫不登入，以本機模式使用", false);
        local.setOnClickListener(v -> ((MainActivity) requireActivity()).enterLocalMode());
        Ui.addWithMargins(content, local, -1, Ui.dp(requireContext(), 48), 0, 32, 0, 0);
        rebuildForm();
        return Ui.scrollPage(requireContext(), content, false);
    }

    private void rebuildForm() {
        form.removeAllViews();
        LinearLayout tabs = Ui.row(requireContext());
        tabs.setGravity(Gravity.CENTER);
        Button login = Ui.pill(requireContext(), "登入", !registerMode);
        Button register = Ui.pill(requireContext(), "註冊", registerMode);
        login.setOnClickListener(v -> { registerMode = false; rebuildForm(); });
        register.setOnClickListener(v -> { registerMode = true; rebuildForm(); });
        tabs.addView(login, new LinearLayout.LayoutParams(0, Ui.dp(requireContext(), 44), 1));
        tabs.addView(register, new LinearLayout.LayoutParams(0, Ui.dp(requireContext(), 44), 1));
        form.addView(tabs);
        addInput("電子郵件", "輸入您的電子郵件", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        addInput("密碼", registerMode ? "設定密碼" : "輸入您的密碼", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (registerMode) addInput("確認密碼", "再次輸入密碼", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Button done = Ui.pill(requireContext(), registerMode ? "註冊" : "登入", true);
        done.setOnClickListener(v -> ((MainActivity) requireActivity()).enterLocalMode());
        Ui.addWithMargins(form, done, -1, Ui.dp(requireContext(), 48), 0, 20, 0, 0);
    }

    private void addInput(String label, String hint, int inputType) {
        TextView l = Ui.text(requireContext(), label, 14, android.graphics.Typeface.BOLD, R.color.ic_ink, false);
        EditText input = Ui.bottomInput(requireContext(), hint, 1);
        input.setInputType(inputType);
        Ui.addWithMargins(form, l, -1, -2, 0, 18, 0, 4);
        form.addView(input, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 50)));
    }
}
