package fcu.app.i_ching.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        binding.hexDetailTopBar.topBarLeftAction.setText("←");
        binding.hexDetailTopBar.topBarLeftAction.setContentDescription(getString(R.string.nav_back));
        binding.hexDetailTopBar.topBarLeftAction.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
        binding.hexDetailTopBar.topBarTitle.setText(getString(R.string.brand_title));
        binding.hexDetailTopBar.topBarRightAction.setText(presentation.favoriteSymbol);
        binding.hexDetailTopBar.topBarRightAction.setContentDescription(presentation.favoriteContentDescription);
        binding.hexDetailTopBar.topBarRightAction.setOnClickListener(v -> {
            boolean favorite = ((fcu.app.i_ching.MainActivity) requireActivity())
                    .settings().toggleFavorite(hexagram.number);
            HexagramDetailPresentation updated = HexagramDetailPresentation.from(hexagram, favorite);
            binding.hexDetailTopBar.topBarRightAction.setText(updated.favoriteSymbol);
            binding.hexDetailTopBar.topBarRightAction.setContentDescription(updated.favoriteContentDescription);
        });
    }

    private void addChip(String label) {
        TextView chip = Ui.chip(requireContext(), label);
        chip.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        params.setMargins(Ui.dp(requireContext(), 4), Ui.dp(requireContext(), 4),
                Ui.dp(requireContext(), 4), Ui.dp(requireContext(), 4));
        binding.hexDetailChips.addView(chip, params);
    }

    private void addSection(HexagramDetailPresentation.Section section) {
        ItemDetailSectionBinding item = ItemDetailSectionBinding.inflate(getLayoutInflater(),
                binding.hexDetailSections, false);
        item.detailSectionTitle.setText(section.title);
        item.detailSectionBody.setText(section.body);
        binding.hexDetailSections.addView(item.getRoot());
    }
}
