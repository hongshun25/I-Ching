package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.LocalRecordStore;

public class ResultFragment extends Fragment {
    private static DivinationResult result;

    public static ResultFragment newInstance(DivinationResult value) { result = value; return new ResultFragment(); }

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (result == null) result = DivinationResult.create("我目前在工作上最需要調整的是什麼？", fcu.app.i_ching.data.DivinationMethod.COINS);
        MainActivity activity = (MainActivity) requireActivity();
        LinearLayout content = Ui.column(requireContext());
        LinearLayout questionBubble = Ui.card(requireContext());
        TextView q = Ui.text(requireContext(), "“" + result.question + "”", 16, android.graphics.Typeface.ITALIC, R.color.ic_text_muted, false); q.setGravity(Gravity.CENTER); questionBubble.addView(q);
        content.addView(questionBubble, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout hero = Ui.card(requireContext()); hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.addView(Ui.hexagramView(requireContext(), result.hexagram, 96, 8, false));
        TextView title = Ui.text(requireContext(), "第" + result.hexagram.number + "卦｜" + result.hexagram.fullName, 36, android.graphics.Typeface.BOLD, R.color.ic_ink, true); title.setGravity(Gravity.CENTER);
        TextView tags = Ui.text(requireContext(), "上" + result.hexagram.upper + "　下" + result.hexagram.lower + "　" + result.method.label, 14, android.graphics.Typeface.BOLD, R.color.ic_text_muted, false); tags.setGravity(Gravity.CENTER);
        TextView insight = Ui.text(requireContext(), result.hexagram.summary, 22, android.graphics.Typeface.NORMAL, R.color.ic_gold, true); insight.setGravity(Gravity.CENTER);
        Ui.addWithMargins(hero, title, -1, -2, 0, 18, 0, 4); hero.addView(tags); Ui.addWithMargins(hero, insight, -1, -2, 0, 18, 0, 0);
        Ui.addWithMargins(content, hero, -1, -2, 0, 18, 0, 18);
        LinearLayout guides = Ui.card(requireContext());
        guides.addView(Ui.text(requireContext(), "適合做", 16, android.graphics.Typeface.BOLD, R.color.ic_gold, false));
        guides.addView(Ui.text(requireContext(), String.join("\n", result.hexagram.doItems), 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        Ui.addWithMargins(guides, Ui.text(requireContext(), "暫時避免", 16, android.graphics.Typeface.BOLD, R.color.ic_error, false), -1, -2, 0, 16, 0, 0);
        guides.addView(Ui.text(requireContext(), String.join("\n", result.hexagram.avoidItems), 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        content.addView(guides);
        LinearLayout classical = Ui.card(requireContext()); classical.addView(Ui.text(requireContext(), "古典卦象解釋", 18, android.graphics.Typeface.BOLD, R.color.ic_ink, true)); classical.addView(Ui.text(requireContext(), result.hexagram.classicalText, 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false)); Ui.addWithMargins(content, classical, -1, -2, 0, 16, 0, 0);
        TextView noteLabel = Ui.text(requireContext(), "這個結果讓你想到什麼？", 14, android.graphics.Typeface.BOLD, R.color.ic_ink, false); Ui.addWithMargins(content, noteLabel, -1, -2, 0, 20, 0, 4);
        EditText note = Ui.bottomInput(requireContext(), "寫下你的靈感或打算採取的行動...", 3); content.addView(note, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 96)));
        Button save = Ui.pill(requireContext(), "儲存至紀錄", true);
        save.setOnClickListener(v -> { new LocalRecordStore(requireContext()).add(DivinationRecord.fromResult(result, note.getText().toString())); Toast.makeText(requireContext(), "已儲存至紀錄", Toast.LENGTH_SHORT).show(); activity.showRecords(); });
        Button share = Ui.pill(requireContext(), "分享啟示", false); share.setOnClickListener(v -> Toast.makeText(requireContext(), "本機 MVP 尚未接入分享", Toast.LENGTH_SHORT).show());
        Ui.addWithMargins(content, save, -1, Ui.dp(requireContext(), 52), 0, 22, 0, 10); content.addView(share, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 52)));
        return Ui.scrollPage(requireContext(), content, false);
    }
}
