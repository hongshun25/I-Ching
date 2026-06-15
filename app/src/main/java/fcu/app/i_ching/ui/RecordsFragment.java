package fcu.app.i_ching.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.data.LocalRecordStore;
import fcu.app.i_ching.databinding.FragmentRecordsBinding;
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
    private RecordsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        binding = FragmentRecordsBinding.inflate(inflater, container, false);
        methodChips.clear();
        changeChips.clear();
        if (savedInstanceState != null) {
            activeMethod = savedInstanceState.getString(STATE_METHOD, METHOD_ALL);
            activeChangeFilter = changeFilterFromName(savedInstanceState.getString(STATE_CHANGE_FILTER));
        }

        setupList(activity);
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
        adapter = null;
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

    private void setupList(MainActivity activity) {
        adapter = new RecordsAdapter(new RecordsAdapter.Callbacks() {
            @Override public void open(DivinationRecord record) {
                activity.showHexagramDetail(record.hexagramNumber);
            }

            @Override public void edit(DivinationRecord record) {
                showEditDialog(record);
            }

            @Override public void delete(DivinationRecord record) {
                confirmDelete(record);
            }
        });
        binding.recordsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recordsList.addItemDecoration(new VerticalSpacingDecoration(Ui.dp(requireContext(), 14)));
        binding.recordsList.setAdapter(adapter);
        binding.recordsList.setHasFixedSize(false);
        binding.recordsEmptyState.emptyStateAction.setOnClickListener(v -> activity.showQuestion());
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
        if (binding == null || adapter == null) return;
        binding.recordsEmptyState.getRoot().setVisibility(allRecords.isEmpty() ? View.VISIBLE : View.GONE);
        if (allRecords.isEmpty()) {
            binding.recordsNoResults.setVisibility(View.GONE);
            binding.recordsList.setVisibility(View.GONE);
            adapter.submitList(new ArrayList<>());
            return;
        }
        List<DivinationRecord> records = LocalRecordStore.filter(
                allRecords,
                binding.recordsSearchInput.getText().toString(),
                activeMethod(),
                activeChangeFilter
        );
        binding.recordsNoResults.setVisibility(records.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recordsList.setVisibility(records.isEmpty() ? View.GONE : View.VISIBLE);
        adapter.submitList(records);
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
                .setPositiveButton("儲存", (dialog, which) -> viewModel.updateNote(record.id, input.getText().toString()))
                .show();
    }

    private void confirmDelete(DivinationRecord record) {
        new AlertDialog.Builder(requireContext())
                .setTitle("刪除紀錄？")
                .setMessage("這筆占卜紀錄將從本機移除。")
                .setNegativeButton("取消", null)
                .setPositiveButton("刪除", (dialog, which) -> viewModel.delete(record.id))
                .show();
    }

    private static final class RecordsAdapter extends ListAdapter<DivinationRecord, RecordsAdapter.Holder> {
        private final Callbacks callbacks;

        RecordsAdapter(Callbacks callbacks) {
            super(new DiffUtil.ItemCallback<DivinationRecord>() {
                @Override public boolean areItemsTheSame(@NonNull DivinationRecord oldItem, @NonNull DivinationRecord newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override public boolean areContentsTheSame(@NonNull DivinationRecord oldItem, @NonNull DivinationRecord newItem) {
                    return oldItem.id == newItem.id
                            && oldItem.hexagramNumber == newItem.hexagramNumber
                            && oldItem.relatingHexagramNumber == newItem.relatingHexagramNumber
                            && Objects.equals(oldItem.method, newItem.method)
                            && oldItem.createdAt == newItem.createdAt
                            && Objects.equals(oldItem.question, newItem.question)
                            && Objects.equals(oldItem.note, newItem.note)
                            && Arrays.equals(oldItem.lineValues, newItem.lineValues)
                            && Objects.equals(oldItem.changingLines, newItem.changingLines);
                }
            });
            this.callbacks = callbacks;
            setHasStableIds(true);
        }

        @Override public long getItemId(int position) { return getItem(position).id; }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(ItemRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            DivinationRecord record = getItem(position);
            Hexagram hex = HexagramRepository.get(record.hexagramNumber);
            RecordCardPresentation presentation = RecordCardPresentation.from(record);
            holder.binding.recordItemTitle.setText(presentation.titleText);
            holder.binding.recordItemRelation.setText(presentation.relationText);
            holder.binding.recordItemChanging.setText(presentation.changingText);
            holder.binding.recordItemQuestion.setText(presentation.questionText);
            holder.binding.recordItemMeta.setText(presentation.metaText);
            holder.binding.recordItemNote.setText(presentation.noteText);
            holder.binding.recordItemNote.setVisibility(presentation.hasNote() ? View.VISIBLE : View.GONE);
            holder.binding.recordItemEdit.setContentDescription(presentation.editContentDescription);
            holder.binding.recordItemEdit.setOnClickListener(v -> callbacks.edit(record));
            holder.binding.recordItemDelete.setContentDescription(presentation.deleteContentDescription);
            holder.binding.recordItemDelete.setOnClickListener(v -> callbacks.delete(record));
            holder.itemView.setOnClickListener(v -> callbacks.open(record));
            holder.itemView.setContentDescription(hex.fullName + "，" + presentation.questionText);
        }

        interface Callbacks {
            void open(DivinationRecord record);
            void edit(DivinationRecord record);
            void delete(DivinationRecord record);
        }

        static final class Holder extends RecyclerView.ViewHolder {
            final ItemRecordBinding binding;

            Holder(ItemRecordBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    private static final class VerticalSpacingDecoration extends RecyclerView.ItemDecoration {
        private final int top;

        VerticalSpacingDecoration(int top) {
            this.top = top;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.top = top;
        }
    }
}
