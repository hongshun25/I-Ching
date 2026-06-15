package fcu.app.i_ching.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.NavigationArgs;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.databinding.FragmentResultBinding;
import fcu.app.i_ching.ui.presentation.ResultPresentation;

public class ResultFragment extends Fragment {
    private static final String STATE_RECORD_ID = "recordId";
    private static final String STATE_NOTE = "note";
    public static final long NO_RECORD_ID = NavigationArgs.NO_RECORD_ID;

    private DivinationResult result;
    private long savedRecordId = NO_RECORD_ID;
    private ResultViewModel viewModel;
    private ResultPresentation presentation;
    private FragmentResultBinding binding;

    public static ResultFragment newInstance(DivinationResult value) {
        ResultFragment fragment = new ResultFragment();
        fragment.setArguments(NavigationArgs.result(value));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        result = readResult();
        presentation = ResultPresentation.from(result);
        savedRecordId = readRecordId(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        binding = FragmentResultBinding.inflate(inflater, container, false);

        bindResult(savedInstanceState);
        return Ui.scrollPage(requireContext(), binding.getRoot(), false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) requireActivity();
        viewModel.saveEvents().observe(getViewLifecycleOwner(), event -> {
            ResultViewModel.SaveState state = event.getContentIfNotHandled();
            if (state == null) return;
            handleSaveState(activity, state);
        });
        viewModel.ensureAutoSaved(result, activity.settings().isAutoSave());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_RECORD_ID, savedRecordId);
        if (binding != null) outState.putString(STATE_NOTE, binding.resultNoteInput.getText().toString());
    }

    private void bindResult(@Nullable Bundle savedInstanceState) {
        binding.resultQuestionText.setText(presentation.questionText);
        binding.resultHexagram.configure(result.hexagram, 96, 8, false);
        binding.resultTitle.setText(presentation.titleText);
        binding.resultTags.setText(presentation.tagText);
        binding.resultInsight.setText(result.hexagram.summary);
        binding.resultRelation.setText(presentation.relationText);
        binding.resultChangingSummary.setText(presentation.changingSummary);
        binding.resultChangedLines.setText(presentation.changedLineText);
        binding.resultChangedLines.setVisibility(presentation.changedLineText.isEmpty() ? View.GONE : View.VISIBLE);
        binding.resultDoItems.setText(String.join("\n", result.hexagram.doItems));
        binding.resultAvoidItems.setText(String.join("\n", result.hexagram.avoidItems));
        binding.resultClassicalText.setText(result.hexagram.judgment + "\n\n" + result.hexagram.classicalText);
        binding.resultSharePreview.setText(presentation.shareText);
        String restoredNote = savedInstanceState == null ? null : savedInstanceState.getString(STATE_NOTE);
        if (restoredNote != null) {
            binding.resultNoteInput.setText(restoredNote);
        }
        binding.resultSaveButton.setText(saveButtonLabel());
        binding.resultSaveButton.setOnClickListener(v -> saveNote());
        binding.resultShareButton.setOnClickListener(v -> shareResult());
    }

    private DivinationResult readResult() {
        return NavigationArgs.result(getArguments());
    }

    private long readRecordId(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) return savedInstanceState.getLong(STATE_RECORD_ID, NO_RECORD_ID);
        return NavigationArgs.recordId(getArguments());
    }

    private void saveNote() {
        if (binding == null) return;
        binding.resultSaveButton.setEnabled(false);
        viewModel.saveNote(result, savedRecordId, binding.resultNoteInput.getText().toString());
    }

    private void handleSaveState(MainActivity activity, ResultViewModel.SaveState state) {
        if (state.record != null) {
            rememberRecordId(state.record.id);
            if (binding != null
                    && binding.resultNoteInput.getText().length() == 0
                    && state.record.note != null
                    && !state.record.note.isEmpty()) {
                binding.resultNoteInput.setText(state.record.note);
            }
        }
        if (binding != null) {
            binding.resultSaveButton.setText(saveButtonLabel());
            binding.resultSaveButton.setEnabled(true);
        }
        if (state.action == ResultViewModel.SaveAction.NOTE_SAVE) {
            Toast.makeText(requireContext(), state.success ? "已儲存至紀錄" : "儲存失敗", Toast.LENGTH_SHORT).show();
            if (state.success) activity.showRecords();
        }
    }

    private String saveButtonLabel() {
        return savedRecordId == NO_RECORD_ID ? getString(R.string.result_save_new) : getString(R.string.result_save_existing);
    }

    private void rememberRecordId(long recordId) {
        savedRecordId = recordId;
        NavigationArgs.putRecordId(getArguments(), recordId);
    }

    private void shareResult() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, presentation.shareText);
        startActivity(Intent.createChooser(intent, "分享啟示"));
    }
}
