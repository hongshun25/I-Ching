package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;

public class HexagramDetailFragment extends Fragment {
    private static final String ARG_NUMBER = "number";
    public static HexagramDetailFragment newInstance(int number) { Bundle args = new Bundle(); args.putInt(ARG_NUMBER, number); HexagramDetailFragment f = new HexagramDetailFragment(); f.setArguments(args); return f; }

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        int number = getArguments() == null ? 15 : getArguments().getInt(ARG_NUMBER, 15);
        Hexagram hex = HexagramRepository.get(number);
        FrameLayout frame = new FrameLayout(requireContext()); frame.setBackgroundColor(Ui.color(requireContext(), R.color.ic_background));
        LinearLayout page = Ui.column(requireContext());
        page.addView(Ui.topBar(requireContext(), "←", v -> requireActivity().getOnBackPressedDispatcher().onBackPressed(), activity.settings().isFavorite(number) ? "♥" : "♡", v -> { activity.settings().toggleFavorite(number); refresh(number); }), new LinearLayout.LayoutParams(-1, -2));
        LinearLayout content = Ui.column(requireContext()); content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.addView(Ui.hexagramView(requireContext(), hex, 130, 12, false));
        content.addView(Ui.chipsRow(requireContext(), hex.fullName, hex.tags.contains("吉卦") ? "吉卦" : hex.tags.get(0)));
        TextView title = Ui.text(requireContext(), "第" + hex.number + "卦｜" + hex.name, 40, android.graphics.Typeface.BOLD, R.color.ic_ink, true); title.setGravity(Gravity.CENTER); Ui.addWithMargins(content, title, -1, -2, 0, 14, 0, 8);
        TextView summary = Ui.text(requireContext(), hex.summary, 18, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false); summary.setGravity(Gravity.CENTER); content.addView(summary);
        addSection(content, "主旨核心", hex.theme);
        addSection(content, "適宜情境", "• " + String.join("\n• ", hex.doItems));
        addSection(content, "《易經》原文", hex.judgment + "\n\n" + hex.classicalText);
        addSection(content, "現代解析", hex.modernText);
        page.addView(Ui.scrollPage(requireContext(), content, false), new LinearLayout.LayoutParams(-1, 0, 1));
        frame.addView(page, new FrameLayout.LayoutParams(-1, -1));
        return frame;
    }

    private void refresh(int number) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, HexagramDetailFragment.newInstance(number))
                .commit();
    }

    private void addSection(LinearLayout content, String title, String body) {
        LinearLayout section = Ui.card(requireContext());
        section.addView(Ui.text(requireContext(), title, 22, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
        Ui.addWithMargins(section, Ui.text(requireContext(), body, 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false), -1, -2, 0, 8, 0, 0);
        Ui.addWithMargins(content, section, -1, -2, 0, 20, 0, 0);
    }
}
