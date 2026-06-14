package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

public class LearnCenterFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        LinearLayout content = Ui.column(requireContext());
        content.addView(Ui.text(requireContext(), "六十四卦", 38, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
        content.addView(Ui.text(requireContext(), "探索易經六十四卦的深層智慧，每一卦皆象徵自然與人生的變化規律。", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        EditText search = Ui.bottomInput(requireContext(), "搜尋卦名或關鍵字...", 1);
        Ui.addWithMargins(content, search, -1, Ui.dp(requireContext(), 54), 0, 24, 0, 14);
        LinearLayout filters = Ui.row(requireContext()); filters.setGravity(Gravity.START); filters.addView(Ui.chip(requireContext(), "全部")); filters.addView(Ui.chip(requireContext(), "上經")); filters.addView(Ui.chip(requireContext(), "下經")); filters.addView(Ui.chip(requireContext(), "我的收藏")); content.addView(Ui.horizontalChips(requireContext(), filters));
        for (Hexagram hex : HexagramRepository.all()) {
            LinearLayout card = Ui.card(requireContext());
            LinearLayout row = Ui.row(requireContext());
            row.addView(Ui.hexagramView(requireContext(), hex, 34, 4, false), new LinearLayout.LayoutParams(Ui.dp(requireContext(), 46), -2));
            LinearLayout texts = Ui.column(requireContext());
            texts.addView(Ui.text(requireContext(), "第" + hex.number + "卦", 12, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
            texts.addView(Ui.text(requireContext(), hex.name, 24, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
            row.addView(texts, new LinearLayout.LayoutParams(0, -2, 1));
            TextView fav = Ui.text(requireContext(), activity.settings().isFavorite(hex.number) ? "♥" : "♡", 26, android.graphics.Typeface.NORMAL, activity.settings().isFavorite(hex.number) ? R.color.ic_gold : R.color.ic_outline_strong, false);
            fav.setOnClickListener(v -> { boolean on = activity.settings().toggleFavorite(hex.number); fav.setText(on ? "♥" : "♡"); fav.setTextColor(Ui.color(requireContext(), on ? R.color.ic_gold : R.color.ic_outline_strong)); });
            row.addView(fav);
            card.addView(row);
            card.addView(Ui.chipsRow(requireContext(), hex.tags.toArray(new String[0])));
            card.setOnClickListener(v -> activity.showHexagramDetail(hex.number));
            Ui.addWithMargins(content, card, -1, -2, 0, 14, 0, 0);
        }
        return Ui.pageWithChrome(activity, content, "學習");
    }
}
