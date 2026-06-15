package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.data.LocalRecordStore;
import fcu.app.i_ching.databinding.FragmentRecordsBinding;
import fcu.app.i_ching.databinding.IncludeEmptyStateBinding;
import fcu.app.i_ching.databinding.ItemRecordBinding;
import fcu.app.i_ching.ui.presentation.RecordCardPresentation;

public class RecordsFragment extends Fragment {
    private static final String STATE_QUERY = "query";
    private static final String STATE_METHOD = "method";
    private static final String STATE_CHANGE_FILTER = "changeFilter";
    private static final String METHOD_ALL = "ALL";

    private final List<TextView> methodChips = new ArrayList<>();
    private final List<TextView> changeChips = new ArrayList<>();
    private String activeMethod = METHOD_ALL;
    private LocalRecordStore.ChangeFilter activeChangeFilter = LocalRecordStore.ChangeFilter.ALL;
    private List<DivinationRecord> allRecords = new ArrayList<>();
    private RecordsViewModel viewModel;
    private FragmentRecordsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        binding = FragmentRecordsBinding.inflate(inflater, container, false);
        methodChips.clear();
        changeChips.clear();
        if (savedInstanceState != null) {
            activeMethod = savedInstanceState.getString(STATE_METHOD, METHOD_ALL);
            activeChangeFilter = changeFilterFromName(savedInstanceState.getString(STATE_CHANGE_FILTER));
        }

        setupSearch(activity, savedInstanceState);
        setupFilterChips(activity);
        updateChipStyles();

        viewModel.records().observe(getViewLifecycleOwner(), records -> {
            allRecords = records == null ? new ArrayList<>() : records;
            renderList(activity);
        });
        viewModel.actionEvents().observe(getViewLifecycleOwner(), event -> {
            RecordsViewModel.ActionState state = event.getContentIfNotHandled();
            if (state == null) return;
            String message;
            if (state.action == RecordsViewModel.Action.UPDATE_NOTE) {
                message = state.success ? "筆記已更新" : "筆記更新失敗";
            } else {
                message = state.success ? "紀錄已刪除" : "刪除失敗";
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
        renderList(activity);
        return Ui.pageWithChrome(activity, binding.getRoot(), "紀錄");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        methodChips.clear();
        changeChips.clear();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_METHOD, activeMethod);
        outState.putString(STATE_CHANGE_FILTER, activeChangeFilter.name());
        if (binding != null) outState.putString(STATE_QUERY, binding.recordsSearchInput.getText().toString());
    }

    private void setupSearch(MainActivity activity, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            binding.recordsSearchInput.setText(savedInstanceState.getString(STATE_QUERY, ""));
        }
        binding.recordsSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderList(activity); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterChips(MainActivity activity) {
        bindMethodChip(binding.recordsMethodAllChip, METHOD_ALL, activity);
        bindMethodChip(binding.recordsMethodCoinsChip, DivinationMethod.COINS.name(), activity);
        bindMethodChip(binding.recordsMethodYarrowChip, DivinationMethod.YARROW.name(), activity);
        bindMethodChip(binding.recordsMethodSimpleChip, DivinationMethod.SIMPLE.name(), activity);

        bindChangeChip(binding.recordsChangeAllChip, LocalRecordStore.ChangeFilter.ALL, activity);
        bindChangeChip(binding.recordsChangeWithChip, LocalRecordStore.ChangeFilter.WITH_CHANGES, activity);
        bindChangeChip(binding.recordsChangeWithoutChip, LocalRecordStore.ChangeFilter.WITHOUT_CHANGES, activity);
    }

    private void bindMethodChip(TextView chip, String methodName, MainActivity activity) {
        chip.setContentDescription("篩選" + chip.getText() + "紀錄");
        chip.setTag(methodName);
        chip.setOnClickListener(v -> {
            activeMethod = methodName;
            updateChipStyles();
            renderList(activity);
        });
        methodChips.add(chip);
    }

    private void bindChangeChip(TextView chip, LocalRecordStore.ChangeFilter filter, MainActivity activity) {
        chip.setContentDescription("篩選" + filter.label + "紀錄");
        chip.setTag(filter);
        chip.setOnClickListener(v -> {
            activeChangeFilter = filter;
            updateChipStyles();
            renderList(activity);
        });
        changeChips.add(chip);
    }

    private void updateChipStyles() {
        for (TextView chip : methodChips) {
            setChipSelected(chip, activeMethod.equals(chip.getTag()));
        }
        for (TextView chip : changeChips) {
            setChipSelected(chip, activeChangeFilter == chip.getTag());
        }
    }

    private void setChipSelected(TextView chip, boolean selected) {
        chip.setTextColor(Ui.color(requireContext(), selected ? R.color.ic_background : R.color.ic_text_muted));
        chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip);
        chip.setSelected(selected);
    }

    private void renderList(MainActivity activity) {
        if (binding == null) return;
        LinearLayout listContainer = binding.recordsList;
        listContainer.removeAllViews();
        listContainer.setGravity(Gravity.NO_GRAVITY);
        if (allRecords.isEmpty()) {
            addEmptyState(listContainer, activity);
            return;
        }
        List<DivinationRecord> records = LocalRecordStore.filter(
                allRecords,
                binding.recordsSearchInput.getText().toString(),
                activeMethod(),
                activeChangeFilter
        );
        if (records.isEmpty()) {
            TextView empty = Ui.text(requireContext(), getString(R.string.records_no_results), 16,
                    android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
            empty.setId(R.id.records_no_results);
            empty.setGravity(Gravity.CENTER);
            Ui.addWithMargins(listContainer, empty, -1, -2, 0, 28, 0, 0);
            return;
        }
        for (DivinationRecord record : records) addRecordCard(activity, listContainer, record);
    }

    private void addEmptyState(LinearLayout parent, MainActivity activity) {
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        IncludeEmptyStateBinding empty = IncludeEmptyStateBinding.inflate(getLayoutInflater(), parent, false);
        empty.emptyStateAction.setOnClickListener(v -> activity.showQuestion());
        parent.addView(empty.getRoot(), new LinearLayout.LayoutParams(-1, -2));
    }

    private void addRecordCard(MainActivity activity, LinearLayout parent, DivinationRecord record) {
        Hexagram hex = HexagramRepository.get(record.hexagramNumber);
        RecordCardPresentation presentation = RecordCardPresentation.from(record);
        ItemRecordBinding item = ItemRecordBinding.inflate(getLayoutInflater(), parent, false);
        item.recordItemTitle.setText(presentation.titleText);
        item.recordItemRelation.setText(presentation.relationText);
        item.recordItemChanging.setText(presentation.changingText);
        item.recordItemQuestion.setText(presentation.questionText);
        item.recordItemMeta.setText(presentation.metaText);
        item.recordItemNote.setText(presentation.noteText);
        item.recordItemNote.setVisibility(presentation.hasNote() ? View.VISIBLE : View.GONE);
        item.recordItemEdit.setContentDescription(presentation.editContentDescription);
        item.recordItemEdit.setOnClickListener(v -> showEditDialog(record));
        item.recordItemDelete.setContentDescription(presentation.deleteContentDescription);
        item.recordItemDelete.setOnClickListener(v -> confirmDelete(record));
        item.getRoot().setOnClickListener(v -> activity.showHexagramDetail(hex.number));
        Ui.addWithMargins(parent, item.getRoot(), -1, -2, 0, 14, 0, 0);
    }

    private DivinationMethod activeMethod() {
        if (METHOD_ALL.equals(activeMethod)) return null;
        try {
            return DivinationMethod.valueOf(activeMethod);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private LocalRecordStore.ChangeFilter changeFilterFromName(String name) {
        try {
            return name == null ? LocalRecordStore.ChangeFilter.ALL : LocalRecordStore.ChangeFilter.valueOf(name);
        } catch (IllegalArgumentException e) {
            return LocalRecordStore.ChangeFilter.ALL;
        }
    }

    private void showEditDialog(DivinationRecord record) {
        EditText input = Ui.bottomInput(requireContext(), "補充這次占卜的反思...", 4);
        input.setText(record.note == null ? "" : record.note);
        input.setSelection(input.getText().length());
        new AlertDialog.Builder(requireContext())
                .setTitle("編輯筆記")
                .setView(input)
                .setNegativeButton("取消", null)
                .setPositiveButton("儲存", (dialog, which) -> {
                    viewModel.updateNote(record.id, input.getText().toString());
                })
                .show();
    }

    private void confirmDelete(DivinationRecord record) {
        new AlertDialog.Builder(requireContext())
                .setTitle("刪除紀錄？")
                .setMessage("這筆占卜紀錄將從本機移除。")
                .setNegativeButton("取消", null)
                .setPositiveButton("刪除", (dialog, which) -> {
                    viewModel.delete(record.id);
                })
                .show();
    }
}
