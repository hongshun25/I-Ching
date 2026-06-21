package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;

import fcu.app.i_ching.R;
import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.NavigationArgs;
import fcu.app.i_ching.databinding.FragmentQuestionBinding;
import fcu.app.i_ching.ui.presentation.QuestionPresetPresentation;

public class QuestionFragment extends Fragment {
    private static final String STATE_QUESTION = "question";
    private static final int MAX_QUESTION_LENGTH = 120;

    private FragmentQuestionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = FragmentQuestionBinding.inflate(inflater, container, false);
        setupQuestion(savedInstanceState);
        binding.questionNextButton.setOnClickListener(v ->
                activity.showMethod(binding.questionInput.getText().toString()));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding != null) outState.putString(STATE_QUESTION, binding.questionInput.getText().toString());
    }

    private void setupQuestion(@Nullable Bundle savedInstanceState) {
        binding.questionInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_QUESTION_LENGTH)});
        if (savedInstanceState != null) {
            binding.questionInput.setText(savedInstanceState.getString(STATE_QUESTION, ""));
        } else {
            binding.questionInput.setText(NavigationArgs.draftQuestion(getArguments()));
        }
        updateCount(binding.questionInput.getText());
        binding.questionInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateCount(s); }
            @Override public void afterTextChanged(Editable s) {}
        });
        binding.questionPresets.removeAllViews();
        for (QuestionPresetPresentation preset : QuestionPresetPresentation.all()) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_filter_chip, binding.questionPresets, false);
            chip.setText(preset.label);
            chip.setContentDescription("套用提問主題：" + preset.label);
            chip.setOnClickListener(v -> binding.questionInput.setText(preset.question));
            binding.questionPresets.addView(chip);
        }
    }

    private void updateCount(CharSequence value) {
        binding.questionCount.setText(value.length() + " / " + MAX_QUESTION_LENGTH);
    }
}
