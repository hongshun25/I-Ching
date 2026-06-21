package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.NavigationArgs;
import fcu.app.i_ching.data.YarrowCastingSession;
import fcu.app.i_ching.databinding.FragmentYarrowCastingBinding;

public class YarrowCastingFragment extends Fragment {
    private YarrowCastingSession session;
    private String question;
    private FragmentYarrowCastingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        question = NavigationArgs.question(getArguments());
        session = new YarrowCastingSession();
        binding = FragmentYarrowCastingBinding.inflate(inflater, container, false);
        NavigationChrome.bind(activity, binding.yarrowTopBar, binding.yarrowBottomNav, NavigationChrome.TAB_DIVINATION);
        bind(activity);
        render(null);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void bind(MainActivity activity) {
        binding.yarrowNextButton.setOnClickListener(v -> {
            YarrowCastingSession.Step step = session.completeNextChange();
            if (session.isComplete()) {
                activity.showResult(session.result(question));
            } else {
                render(step);
            }
        });
        binding.yarrowQuickButton.setOnClickListener(v -> {
            session.quickComplete();
            activity.showResult(session.result(question));
        });
    }

    private void render(@Nullable YarrowCastingSession.Step lastStep) {
        int completed = session.completedChanges();
        binding.yarrowProgress.setProgress(completed);
        binding.yarrowProgress.setMax(YarrowCastingSession.TOTAL_CHANGES);
        binding.yarrowStepTitle.setText("第" + session.currentLineNumber()
                + "爻・第" + session.currentChangeNumber() + "變");
        if (lastStep == null) {
            binding.yarrowStepBody.setText("從四十九策開始。按下按鈕完成第一變。");
        } else if (lastStep.lineCompleted) {
            binding.yarrowStepBody.setText("第" + lastStep.lineNumber + "爻完成，成爻值 "
                    + lastStep.completedLineValue + "。下一步進入下一爻。");
        } else {
            binding.yarrowStepBody.setText("本變取出 " + lastStep.removedStalks
                    + " 策，餘 " + lastStep.remainingStalks + " 策。");
        }
        binding.yarrowLineValues.setText(lineValuesText());
    }

    private String lineValuesText() {
        int[] values = session.lineValues();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            labels.add("第" + (i + 1) + "爻 " + (values[i] == 0 ? "待定" : values[i]));
        }
        return String.join("\n", labels);
    }
}
