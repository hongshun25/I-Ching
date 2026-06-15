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
import fcu.app.i_ching.data.DivinationMethod;

public class MethodFragment extends Fragment {
    private DivinationMethod selected = DivinationMethod.COINS;
    private LinearLayout methods;
    private String question;

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        question = getArguments() == null ? null : getArguments().getString(MainActivity.ARG_QUESTION);
        LinearLayout content = Ui.column(requireContext());
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView step = Ui.text(requireContext(), "STEP 2 / 4", 14, android.graphics.Typeface.BOLD, R.color.ic_text_muted, false);
        TextView title = Ui.text(requireContext(), "選擇占法", 34, android.graphics.Typeface.NORMAL, R.color.ic_ink, true); title.setGravity(Gravity.CENTER);
        TextView body = Ui.text(requireContext(), "請選擇最適合當下心境的卜筮方式。不同的方式將引導出不同的沉浸體驗與變爻機率。", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false); body.setGravity(Gravity.CENTER);
        content.addView(step); Ui.addWithMargins(content, title, -1, -2, 0, 24, 0, 8); content.addView(body);
        methods = Ui.column(requireContext()); Ui.addWithMargins(content, methods, -1, -2, 0, 30, 0, 22); rebuild();
        Button next = Ui.pill(requireContext(), "開始靜心 →", true); next.setOnClickListener(v -> ((MainActivity) requireActivity()).showRitual(question, selected));
        content.addView(next, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 52)));
        return Ui.scrollPage(requireContext(), content, false);
    }

    private void rebuild() {
        methods.removeAllViews();
        addMethod(DivinationMethod.SIMPLE, "簡易占法", "適合快速反思，直接得出一卦，無變爻。");
        addMethod(DivinationMethod.COINS, "三枚銅錢", "模擬傳統銅錢占法，六次投擲，可產生變爻，體現事物變動之機。");
        addMethod(DivinationMethod.YARROW, "蓍草靈感模式", "節奏較慢，十八變而成卦。適合重大決策前的深度提問與冥想沉澱。");
    }

    private void addMethod(DivinationMethod method, String title, String detail) {
        LinearLayout card = Ui.card(requireContext());
        card.setBackground(Ui.strokeBg(requireContext(), method == selected ? R.color.ic_surface_container : R.color.ic_surface, method == selected ? R.color.ic_ink : R.color.ic_outline, 16));
        TextView t = Ui.text(requireContext(), (method == selected ? "✓ " : "") + title, 24, android.graphics.Typeface.NORMAL, R.color.ic_ink, true);
        TextView d = Ui.text(requireContext(), detail, 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
        card.addView(t); Ui.addWithMargins(card, d, -1, -2, 0, 6, 0, 0);
        card.setOnClickListener(v -> { selected = method; rebuild(); });
        Ui.addWithMargins(methods, card, -1, -2, 0, 0, 0, 12);
    }
}
