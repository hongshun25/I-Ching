package fcu.app.i_ching.ui;

import android.content.res.Configuration;
import android.os.Bundle;
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
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;

public class DailyFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        boolean night = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        Hexagram hexagram = HexagramRepository.get(night ? 29 : 15);
        LinearLayout content = Ui.column(requireContext());
        TextView greeting = Ui.text(requireContext(), night ? "甲辰年 壬申月 丁卯日" : "早安，今天想安靜一下嗎？", night ? 14 : 18, android.graphics.Typeface.NORMAL, night ? R.color.ic_gold : R.color.ic_text_muted, false);
        greeting.setGravity(night ? Gravity.CENTER : Gravity.START);
        content.addView(greeting);
        LinearLayout card = Ui.card(requireContext());
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.addView(Ui.chipsRow(requireContext(), hexagram.upper, hexagram.lower));
        Ui.addWithMargins(card, Ui.hexagramView(requireContext(), hexagram, night ? 128 : 72, night ? 11 : 8, false), -2, -2, 0, 24, 0, 20);
        TextView title = Ui.text(requireContext(), "第" + hexagram.number + "卦｜" + hexagram.fullName, 26, android.graphics.Typeface.NORMAL, R.color.ic_ink, true);
        title.setGravity(Gravity.CENTER);
        TextView judgment = Ui.text(requireContext(), night ? hexagram.judgment : "「謙卑自守，則吉無不利。」", night ? 18 : 24, android.graphics.Typeface.NORMAL, R.color.ic_gold, true);
        judgment.setGravity(Gravity.CENTER);
        TextView summary = Ui.text(requireContext(), hexagram.summary, 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
        summary.setGravity(Gravity.CENTER);
        card.addView(title);
        Ui.addWithMargins(card, judgment, -1, -2, 0, 10, 0, 12);
        card.addView(summary);
        Ui.addWithMargins(content, card, -1, -2, 0, 28, 0, 24);
        TextView reflectionLabel = Ui.text(requireContext(), "今日靜思", 14, android.graphics.Typeface.BOLD, R.color.ic_text_muted, false);
        content.addView(reflectionLabel);
        EditText reflection = Ui.bottomInput(requireContext(), "今天有什麼事情值得慢下來？", 3);
        content.addView(reflection, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 96)));
        Button cast = Ui.pill(requireContext(), "✦ 開始占卜", true);
        Button records = Ui.pill(requireContext(), "↺ 查看紀錄", false);
        Button learn = Ui.pill(requireContext(), "書 學習此卦", false);
        cast.setOnClickListener(v -> activity.showQuestion());
        records.setOnClickListener(v -> activity.showRecords());
        learn.setOnClickListener(v -> activity.showHexagramDetail(hexagram.number));
        Ui.addWithMargins(content, cast, -1, Ui.dp(requireContext(), 52), 0, 28, 0, 10);
        Ui.addWithMargins(content, records, -1, Ui.dp(requireContext(), 52), 0, 0, 0, 10);
        content.addView(learn, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 52)));
        return Ui.pageWithChrome(activity, content, "今日");
    }
}
