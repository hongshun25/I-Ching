package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class SplashFragment extends Fragment {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout root = new FrameLayout(requireContext());
        root.setBackgroundColor(Ui.color(requireContext(), R.color.ic_background));
        LinearLayout content = Ui.column(requireContext());
        content.setGravity(Gravity.CENTER);
        TextView title = Ui.text(requireContext(), "I CHING", 42, android.graphics.Typeface.BOLD, R.color.ic_ink, true);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.16f);
        TextView sub = Ui.text(requireContext(), "古老智慧 現代心境", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
        sub.setGravity(Gravity.CENTER);
        content.addView(title, new LinearLayout.LayoutParams(-1, -2));
        Ui.addWithMargins(content, sub, -1, -2, 0, 8, 0, 0);
        root.addView(content, new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER));
        handler.postDelayed(() -> {
            if (isAdded()) ((MainActivity) requireActivity()).routeAfterSplash();
        }, 3500);
        return root;
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
