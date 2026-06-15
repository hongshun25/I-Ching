package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationMethod;

public class RitualFragment extends Fragment {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable finishRunnable;
    private String question;
    private DivinationMethod method = DivinationMethod.COINS;

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        readArguments();
        FrameLayout root = new FrameLayout(requireContext());
        root.setBackgroundColor(Ui.color(requireContext(), R.color.ic_background));
        TextView header = Ui.text(requireContext(), "請深呼吸，將注意力放回你的問題。\n長按螢幕，直到光圈合一。", 24, android.graphics.Typeface.NORMAL, R.color.ic_ink, true);
        header.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams hp = new FrameLayout.LayoutParams(-1, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL); hp.setMargins(Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 72), Ui.dp(requireContext(), 24), 0); root.addView(header, hp);
        ProgressBar circle = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleLarge);
        root.addView(circle, new FrameLayout.LayoutParams(Ui.dp(requireContext(), 148), Ui.dp(requireContext(), 148), Gravity.CENTER));
        TextView focus = Ui.text(requireContext(), "◌", 72, android.graphics.Typeface.NORMAL, R.color.ic_gold, true); focus.setGravity(Gravity.CENTER);
        root.addView(focus, new FrameLayout.LayoutParams(Ui.dp(requireContext(), 180), Ui.dp(requireContext(), 180), Gravity.CENTER));
        Button skip = Ui.pill(requireContext(), "略過儀式", false); skip.setOnClickListener(v -> activity.showResult(question, method));
        FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(-1, Ui.dp(requireContext(), 52), Gravity.BOTTOM); sp.setMargins(Ui.dp(requireContext(), 24), 0, Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 32)); root.addView(skip, sp);
        finishRunnable = () -> activity.showResult(question, method);
        root.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                header.setAlpha(0.45f); focus.animate().scaleX(1.45f).scaleY(1.45f).alpha(0.65f).setDuration(3000).start();
                handler.postDelayed(finishRunnable, 3000);
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                handler.removeCallbacks(finishRunnable);
                header.setAlpha(1f); focus.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).start();
                return true;
            }
            return true;
        });
        return root;
    }

    private void readArguments() {
        Bundle args = getArguments();
        if (args == null) return;
        question = args.getString(MainActivity.ARG_QUESTION);
        try {
            method = DivinationMethod.valueOf(args.getString(MainActivity.ARG_METHOD, DivinationMethod.COINS.name()));
        } catch (IllegalArgumentException e) {
            method = DivinationMethod.COINS;
        }
    }

    @Override public void onDestroyView() { handler.removeCallbacksAndMessages(null); super.onDestroyView(); }
}
