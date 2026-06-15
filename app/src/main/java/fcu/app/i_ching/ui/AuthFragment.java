package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;

public class AuthFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        LinearLayout content = Ui.column(requireContext());
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 72), Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 24));

        TextView title = Ui.text(requireContext(), "本機模式", 40, android.graphics.Typeface.BOLD, R.color.ic_ink, true);
        title.setGravity(Gravity.CENTER);
        TextView subtitle = Ui.text(requireContext(), "不建立帳號，資料只保存在這台裝置。", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
        subtitle.setGravity(Gravity.CENTER);
        content.addView(title, new LinearLayout.LayoutParams(-1, -2));
        Ui.addWithMargins(content, subtitle, -1, -2, 0, 8, 0, 32);

        LinearLayout card = Ui.card(requireContext());
        card.addView(Ui.text(requireContext(), "目前內測版未啟用登入、同步或雲端備份。占卜問題、筆記與紀錄會寫入此裝置的本機資料庫；之後可在「我的」匯出 JSON 或純文字，也可以刪除全部紀錄。", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        Ui.addWithMargins(content, card, -1, -2, 0, 0, 0, 28);

        Button enter = Ui.pill(requireContext(), "進入本機模式", true);
        enter.setId(R.id.local_mode_button);
        enter.setContentDescription("進入本機模式");
        enter.setOnClickListener(v -> activity.enterLocalMode());
        content.addView(enter, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 52)));

        Button learn = Ui.pill(requireContext(), "先看看每日靜心", false);
        learn.setOnClickListener(v -> activity.enterLocalMode());
        Ui.addWithMargins(content, learn, -1, Ui.dp(requireContext(), 52), 0, 12, 0, 0);
        return Ui.scrollPage(requireContext(), content, false);
    }
}
