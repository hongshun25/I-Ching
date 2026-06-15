package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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

public class QuestionFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        LinearLayout content = Ui.column(requireContext());
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView step = Ui.text(requireContext(), "Step 1", 14, android.graphics.Typeface.BOLD, R.color.ic_gold, false); step.setGravity(Gravity.CENTER);
        TextView title = Ui.text(requireContext(), "你想問什麼？", 36, android.graphics.Typeface.NORMAL, R.color.ic_ink, true); title.setGravity(Gravity.CENTER);
        TextView body = Ui.text(requireContext(), "試著問一個你真正想理解的情境。", 18, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false); body.setGravity(Gravity.CENTER);
        content.addView(step); content.addView(title); Ui.addWithMargins(content, body, -1, -2, 0, 8, 0, 28);
        LinearLayout card = Ui.card(requireContext());
        EditText question = Ui.bottomInput(requireContext(), "我目前在工作上最需要調整的是什麼？", 5);
        question.setId(R.id.question_input);
        question.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)});
        TextView count = Ui.text(requireContext(), "0 / 120", 12, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
        count.setGravity(Gravity.END);
        question.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s, int st, int c, int a) {} public void onTextChanged(CharSequence s, int st, int b, int c) { count.setText(s.length() + " / 120"); } public void afterTextChanged(Editable e) {} });
        card.addView(question, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 170))); card.addView(count);
        content.addView(card, new LinearLayout.LayoutParams(-1, -2));
        TextView shortcuts = Ui.text(requireContext(), "或選擇常見主題", 14, android.graphics.Typeface.BOLD, R.color.ic_text_muted, false); shortcuts.setGravity(Gravity.CENTER);
        Ui.addWithMargins(content, shortcuts, -1, -2, 0, 30, 0, 10);
        LinearLayout chips = Ui.row(requireContext()); chips.setGravity(Gravity.CENTER);
        String[][] presets = {{"職涯","職涯發展的方向"},{"感情","這段感情未來的發展"},{"人際","如何改善當前的人際關係"},{"財務","近期的財務狀況與建議"},{"學業","學業上的挑戰與突破"},{"家庭","家庭關係的調和"},{"健康","健康狀態的保養建議"},{"決策","面對這個選擇，我該如何決定"}};
        for (String[] preset : presets) { TextView chip = Ui.chip(requireContext(), preset[0]); chip.setOnClickListener(v -> question.setText(preset[1])); Ui.addWithMargins(chips, chip, -2, -2, 4, 4, 4, 4); }
        content.addView(Ui.horizontalChips(requireContext(), chips), new LinearLayout.LayoutParams(-1, -2));
        Button next = Ui.pill(requireContext(), "確認提問 →", true);
        next.setId(R.id.question_next_button);
        next.setOnClickListener(v -> activity.showMethod(question.getText().toString()));
        Ui.addWithMargins(content, next, -1, Ui.dp(requireContext(), 52), 0, 36, 0, 0);
        return Ui.scrollPage(requireContext(), content, false);
    }
}
