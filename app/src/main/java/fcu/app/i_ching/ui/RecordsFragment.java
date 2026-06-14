package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.List;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.data.LocalRecordStore;

public class RecordsFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        LinearLayout content = Ui.column(requireContext());
        List<DivinationRecord> records = new LocalRecordStore(requireContext()).all();
        if (records.isEmpty()) {
            content.setGravity(Gravity.CENTER_HORIZONTAL);
            TextView art = Ui.text(requireContext(), "↺", 96, android.graphics.Typeface.NORMAL, R.color.ic_outline_strong, false); art.setGravity(Gravity.CENTER);
            TextView title = Ui.text(requireContext(), "你的占卜紀錄會出現在這裡", 24, android.graphics.Typeface.NORMAL, R.color.ic_ink, true); title.setGravity(Gravity.CENTER);
            TextView body = Ui.text(requireContext(), "靜心凝神，透過易經尋求指引，記錄下每一次的智慧對話。", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false); body.setGravity(Gravity.CENTER);
            Button start = Ui.pill(requireContext(), "✦ 開始第一次占卜", true); start.setOnClickListener(v -> activity.showQuestion());
            content.addView(art, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 180))); content.addView(title); Ui.addWithMargins(content, body, -1, -2, 0, 8, 0, 28); content.addView(start, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 52)));
        } else {
            content.addView(Ui.text(requireContext(), "占卜紀錄", 36, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
            for (DivinationRecord record : records) {
                Hexagram hex = HexagramRepository.get(record.hexagramNumber);
                LinearLayout card = Ui.card(requireContext());
                card.addView(Ui.text(requireContext(), "第" + hex.number + "卦｜" + hex.fullName, 22, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
                card.addView(Ui.text(requireContext(), record.question, 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
                card.addView(Ui.text(requireContext(), record.method.label + " · " + DateFormat.format("yyyy/MM/dd HH:mm", new Date(record.createdAt)), 12, android.graphics.Typeface.NORMAL, R.color.ic_outline_strong, false));
                if (record.note != null && !record.note.isEmpty()) card.addView(Ui.text(requireContext(), record.note, 15, android.graphics.Typeface.NORMAL, R.color.ic_gold, false));
                card.setOnClickListener(v -> activity.showHexagramDetail(hex.number));
                Ui.addWithMargins(content, card, -1, -2, 0, 14, 0, 0);
            }
        }
        return Ui.pageWithChrome(activity, content, "紀錄");
    }
}
