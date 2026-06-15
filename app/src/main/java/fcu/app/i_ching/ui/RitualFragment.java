package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.NavigationArgs;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.databinding.FragmentRitualBinding;
import fcu.app.i_ching.ui.presentation.RitualPresentation;

public class RitualFragment extends Fragment {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable finishRunnable;
    private String question;
    private DivinationMethod method = DivinationMethod.COINS;
    private FragmentRitualBinding binding;
    private RitualPresentation presentation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        readArguments();
        presentation = RitualPresentation.from(activity.settings().isReduceMotion());
        binding = FragmentRitualBinding.inflate(inflater, container, false);
        bindRitual(activity);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        binding = null;
        super.onDestroyView();
    }

    private void readArguments() {
        question = NavigationArgs.question(getArguments());
        method = NavigationArgs.method(getArguments());
    }

    private void bindRitual(MainActivity activity) {
        binding.ritualHeader.setText(getString(presentation.reduceMotion
                ? R.string.ritual_header_reduce_motion
                : R.string.ritual_header));
        binding.ritualSkipButton.setOnClickListener(v -> activity.showResult(question, method));
        finishRunnable = () -> activity.showResult(question, method);
        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                beginPress();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                endPress();
                return true;
            }
            return true;
        });
    }

    private void beginPress() {
        binding.ritualHeader.setAlpha(presentation.pressedHeaderAlpha);
        binding.ritualFocus.animate()
                .scaleX(presentation.pressedFocusScale)
                .scaleY(presentation.pressedFocusScale)
                .alpha(presentation.pressedFocusAlpha)
                .setDuration(presentation.pressAnimationMs)
                .start();
        handler.removeCallbacks(finishRunnable);
        handler.postDelayed(finishRunnable, presentation.finishDelayMs);
    }

    private void endPress() {
        if (presentation.cancelOnRelease) {
            handler.removeCallbacks(finishRunnable);
        }
        binding.ritualHeader.setAlpha(1f);
        binding.ritualFocus.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(presentation.releaseAnimationMs)
                .start();
    }
}
