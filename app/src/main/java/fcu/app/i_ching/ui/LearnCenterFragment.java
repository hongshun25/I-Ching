package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.android.material.chip.Chip;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.data.SettingsStore;
import fcu.app.i_ching.databinding.FragmentLearnCenterBinding;
import fcu.app.i_ching.databinding.ItemHexagramBinding;
import fcu.app.i_ching.ui.presentation.HexagramListItemPresentation;

public class LearnCenterFragment extends Fragment {
    private static final String STATE_FILTER = "filter";
    private static final String STATE_QUERY = "query";

    private final List<Chip> filterChips = new ArrayList<>();
    private String activeFilter = HexagramRepository.FILTER_ALL;
    private FragmentLearnCenterBinding binding;
    private HexagramAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = FragmentLearnCenterBinding.inflate(inflater, container, false);
        filterChips.clear();
        if (savedInstanceState != null) {
            activeFilter = savedInstanceState.getString(STATE_FILTER, HexagramRepository.FILTER_ALL);
        }
        setupList(activity);
        setupSearch(savedInstanceState);
        setupFilterChips(activity);
        updateChipStyles();
        NavigationChrome.bind(activity, binding.topBar, binding.bottomNav, NavigationChrome.TAB_LEARN);
        renderList(activity);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
        filterChips.clear();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_FILTER, activeFilter);
        if (binding != null) outState.putString(STATE_QUERY, binding.learnSearchInput.getText().toString());
    }

    private void setupList(MainActivity activity) {
        adapter = new HexagramAdapter(activity.settings(), new HexagramAdapter.Callbacks() {
            @Override public void open(Hexagram hexagram) { activity.showHexagramDetail(hexagram.number); }
            @Override public void favoriteChanged(Hexagram hexagram, boolean favorite) {
                if (HexagramRepository.FILTER_FAVORITES.equals(activeFilter)) {
                    renderList(activity);
                } else {
                    int position = adapter.currentIndexOf(hexagram.number);
                    if (position != RecyclerView.NO_POSITION) adapter.notifyItemChanged(position);
                }
            }
        });
        binding.learnList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.learnList.setAdapter(adapter);
        binding.learnList.setHasFixedSize(false);
    }

    private void setupSearch(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            binding.learnSearchInput.setText(savedInstanceState.getString(STATE_QUERY, ""));
        }
        binding.learnSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderList((MainActivity) requireActivity());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterChips(MainActivity activity) {
        bindFilterChip(binding.learnFilterAllChip, HexagramRepository.FILTER_ALL, activity);
        bindFilterChip(binding.learnFilterUpperChip, HexagramRepository.FILTER_UPPER_CANON, activity);
        bindFilterChip(binding.learnFilterLowerChip, HexagramRepository.FILTER_LOWER_CANON, activity);
        bindFilterChip(binding.learnFilterFavoritesChip, HexagramRepository.FILTER_FAVORITES, activity);
    }

    private void bindFilterChip(Chip chip, String label, MainActivity activity) {
        chip.setContentDescription("篩選" + label);
        chip.setTag(label);
        chip.setOnClickListener(v -> {
            activeFilter = label;
            updateChipStyles();
            renderList(activity);
        });
        filterChips.add(chip);
    }

    private void updateChipStyles() {
        for (Chip chip : filterChips) {
            boolean selected = activeFilter.equals(chip.getTag());
            chip.setChecked(selected);
            chip.setSelected(selected);
        }
    }

    private void renderList(MainActivity activity) {
        if (binding == null || adapter == null) return;
        List<Hexagram> hexagrams = HexagramRepository.filter(
                binding.learnSearchInput.getText().toString(),
                activeFilter,
                activity.settings().favoriteHexagrams()
        );
        binding.learnNoResults.setVisibility(hexagrams.isEmpty() ? View.VISIBLE : View.GONE);
        binding.learnList.setVisibility(hexagrams.isEmpty() ? View.GONE : View.VISIBLE);
        adapter.submitList(hexagrams);
    }

    private static final class HexagramAdapter extends ListAdapter<Hexagram, HexagramAdapter.Holder> {
        private final SettingsStore settings;
        private final Callbacks callbacks;

        HexagramAdapter(SettingsStore settings, Callbacks callbacks) {
            super(new DiffUtil.ItemCallback<Hexagram>() {
                @Override public boolean areItemsTheSame(@NonNull Hexagram oldItem, @NonNull Hexagram newItem) {
                    return oldItem.number == newItem.number;
                }

                @Override public boolean areContentsTheSame(@NonNull Hexagram oldItem, @NonNull Hexagram newItem) {
                    return oldItem.number == newItem.number
                            && Objects.equals(oldItem.name, newItem.name)
                            && Objects.equals(oldItem.fullName, newItem.fullName)
                            && Objects.equals(oldItem.upper, newItem.upper)
                            && Objects.equals(oldItem.lower, newItem.lower)
                            && Objects.equals(oldItem.summary, newItem.summary)
                            && Objects.equals(oldItem.tags, newItem.tags);
                }
            });
            this.settings = settings;
            this.callbacks = callbacks;
            setHasStableIds(true);
        }

        @Override public long getItemId(int position) { return getItem(position).number; }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(ItemHexagramBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Hexagram hexagram = getItem(position);
            boolean favorite = settings.isFavorite(hexagram.number);
            HexagramListItemPresentation presentation = HexagramListItemPresentation.from(hexagram, favorite);
            holder.binding.hexagramItemHexagram.configure(hexagram, 34, 4, false);
            holder.binding.hexagramItemNumber.setText(presentation.numberText);
            holder.binding.hexagramItemName.setText(presentation.nameText);
            holder.binding.hexagramItemTrigrams.setText(presentation.trigramsText);
            holder.binding.hexagramItemTags.setText(presentation.tagsText);
            holder.itemView.setContentDescription(presentation.openContentDescription);
            holder.binding.hexagramItemFavorite.setImageResource(presentation.favoriteIconRes);
            holder.binding.hexagramItemFavorite.setSelected(favorite);
            holder.binding.hexagramItemFavorite.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(),
                    favorite ? R.color.ic_gold : R.color.ic_outline_strong));
            holder.binding.hexagramItemFavorite.setContentDescription(presentation.favoriteContentDescription);
            holder.binding.hexagramItemFavorite.setOnClickListener(v -> {
                boolean on = settings.toggleFavorite(hexagram.number);
                callbacks.favoriteChanged(hexagram, on);
            });
            holder.itemView.setOnClickListener(v -> callbacks.open(hexagram));
        }

        int currentIndexOf(int number) {
            for (int i = 0; i < getItemCount(); i++) {
                if (getItem(i).number == number) return i;
            }
            return RecyclerView.NO_POSITION;
        }

        interface Callbacks {
            void open(Hexagram hexagram);
            void favoriteChanged(Hexagram hexagram, boolean favorite);
        }

        static final class Holder extends RecyclerView.ViewHolder {
            final ItemHexagramBinding binding;

            Holder(ItemHexagramBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
