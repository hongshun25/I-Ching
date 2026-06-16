package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

import fcu.app.i_ching.NavigationArgs;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.databinding.FragmentHexagramDetailBinding;
import fcu.app.i_ching.databinding.ItemDetailSectionBinding;
import fcu.app.i_ching.ui.presentation.HexagramDetailPresentation;

public class HexagramDetailFragment extends Fragment {
    private FragmentHexagramDetailBinding binding;
    private Hexagram hexagram;

    public static HexagramDetailFragment newInstance(int number) {
        HexagramDetailFragment fragment = new HexagramDetailFragment();
        fragment.setArguments(NavigationArgs.hexagramDetail(number));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int number = NavigationArgs.hexagramNumber(getArguments());
        hexagram = HexagramRepository.get(number);
        binding = FragmentHexagramDetailBinding.inflate(inflater, container, false);
        bindDetail(HexagramDetailPresentation.from(hexagram, ((fcu.app.i_ching.MainActivity) requireActivity())
                .settings().isFavorite(number)));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void bindDetail(HexagramDetailPresentation presentation) {
        bindTopBar(presentation);
        binding.hexDetailHexagram.configure(hexagram, 130, 12, false);
        binding.hexDetailChips.removeAllViews();
        addChip(presentation.primaryChipText);
        addChip(presentation.secondaryChipText);
        binding.hexDetailTitle.setText(presentation.titleText);
        binding.hexDetailSummary.setText(presentation.summaryText);
        binding.hexDetailSections.removeAllViews();
        for (HexagramDetailPresentation.Section section : presentation.sections) {
            addSection(section);
        }
    }

    private void bindTopBar(HexagramDetailPresentation presentation) {
        MaterialToolbar toolbar = binding.hexDetailTopBar.getRoot();
        toolbar.setTitle(getString(R.string.brand_title));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);
        toolbar.setNavigationContentDescription(R.string.nav_back);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
        toolbar.getMenu().clear();
        MenuItem favoriteItem = toolbar.getMenu()
                .add(presentation.favoriteContentDescription)
                .setIcon(presentation.favoriteIconRes);
        favoriteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItemCompat.setContentDescription(favoriteItem, presentation.favoriteContentDescription);
        toolbar.setOnMenuItemClickListener(item -> {
            boolean favorite = ((fcu.app.i_ching.MainActivity) requireActivity())
                    .settings().toggleFavorite(hexagram.number);
            HexagramDetailPresentation updated = HexagramDetailPresentation.from(hexagram, favorite);
            item.setTitle(updated.favoriteContentDescription);
            item.setIcon(updated.favoriteIconRes);
            MenuItemCompat.setContentDescription(item, updated.favoriteContentDescription);
            return true;
        });
    }

    private void addChip(String label) {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_filter_chip, binding.hexDetailChips, false);
        chip.setText(label);
        binding.hexDetailChips.addView(chip);
    }

    private void addSection(HexagramDetailPresentation.Section section) {
        ItemDetailSectionBinding item = ItemDetailSectionBinding.inflate(getLayoutInflater(),
                binding.hexDetailSections, false);
        item.detailSectionTitle.setText(section.title);
        item.detailSectionBody.setText(section.body);
        binding.hexDetailSections.addView(item.getRoot());
    }
}
