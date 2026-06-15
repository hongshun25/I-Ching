package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;

public class OnboardingFragment extends Fragment {
    private final String[] titles = {"每日靜心", "專注提問", "保存與回看"};
    private final String[] bodies = {"每天用一卦，整理當下的心。", "把模糊的不安，轉化成可以思考的問題。", "記錄你的提問、選擇與後來的答案。"};

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        FrameLayout root = new FrameLayout(requireContext());
        root.setBackgroundColor(Ui.color(requireContext(), R.color.ic_background));
        RecyclerView recycler = new RecyclerView(requireContext());
        recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recycler.setAdapter(new Adapter());
        new PagerSnapHelper().attachToRecyclerView(recycler);
        root.addView(recycler, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout bottom = Ui.column(requireContext());
        bottom.setPadding(Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 32), Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 28));
        bottom.setBackgroundColor(Ui.color(requireContext(), R.color.ic_background));
        LinearLayout dots = Ui.row(requireContext());
        dots.setGravity(Gravity.CENTER);
        dots.addView(Ui.chip(requireContext(), "●  ·  ·"));
        bottom.addView(dots, new LinearLayout.LayoutParams(-1, -2));
        Button start = Ui.pill(requireContext(), "開始使用", true);
        Button local = Ui.pill(requireContext(), "以本機模式使用", false);
        start.setId(R.id.onboarding_start_button);
        local.setId(R.id.local_mode_button);
        start.setOnClickListener(v -> activity.completeOnboarding());
        local.setOnClickListener(v -> activity.enterLocalMode());
        Ui.addWithMargins(bottom, start, -1, Ui.dp(requireContext(), 48), 0, 16, 0, 8);
        Ui.addWithMargins(bottom, local, -1, Ui.dp(requireContext(), 48), 0, 0, 0, 0);
        root.addView(bottom, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM));
        return root;
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout page = Ui.column(parent.getContext());
            page.setGravity(Gravity.CENTER);
            page.setPadding(Ui.dp(parent.getContext(), 24), Ui.dp(parent.getContext(), 40), Ui.dp(parent.getContext(), 24), Ui.dp(parent.getContext(), 180));
            page.setLayoutParams(new RecyclerView.LayoutParams(parent.getResources().getDisplayMetrics().widthPixels, -1));
            return new Holder(page);
        }
        @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
            LinearLayout page = (LinearLayout) holder.itemView;
            page.removeAllViews();
            TextView ink = Ui.text(page.getContext(), position == 0 ? "◯" : position == 1 ? "◌" : "≋", 130, android.graphics.Typeface.NORMAL, R.color.ic_outline_strong, true);
            ink.setGravity(Gravity.CENTER);
            TextView title = Ui.text(page.getContext(), titles[position], 30, android.graphics.Typeface.NORMAL, R.color.ic_ink, true);
            title.setGravity(Gravity.CENTER);
            TextView body = Ui.text(page.getContext(), bodies[position], 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
            body.setGravity(Gravity.CENTER);
            page.addView(ink, new LinearLayout.LayoutParams(-1, Ui.dp(page.getContext(), 240)));
            Ui.addWithMargins(page, title, -1, -2, 0, 48, 0, 8);
            page.addView(body, new LinearLayout.LayoutParams(-1, -2));
        }
        @Override public int getItemCount() { return titles.length; }
        class Holder extends RecyclerView.ViewHolder { Holder(@NonNull View itemView) { super(itemView); } }
    }
}
