package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.NavigationArgs;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.databinding.FragmentMethodBinding;
import fcu.app.i_ching.ui.presentation.MethodOptionPresentation;

public class MethodFragment extends Fragment {
    private static final String STATE_SELECTED = "selected";

    private DivinationMethod selected = DivinationMethod.COINS;
    private String question;
    private FragmentMethodBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        question = NavigationArgs.question(getArguments());
        selected = readSelected(savedInstanceState);
        binding = FragmentMethodBinding.inflate(inflater, container, false);
        bindMethodCards();
        binding.methodNextButton.setOnClickListener(v ->
                ((MainActivity) requireActivity()).showRitual(question, selected));
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
        outState.putString(STATE_SELECTED, selected.name());
    }

    private DivinationMethod readSelected(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) return DivinationMethod.COINS;
        try {
            return DivinationMethod.valueOf(savedInstanceState.getString(STATE_SELECTED, DivinationMethod.COINS.name()));
        } catch (IllegalArgumentException e) {
            return DivinationMethod.COINS;
        }
    }

    private void bindMethodCards() {
        bindCard(binding.methodSimpleCard, binding.methodSimpleStatus, DivinationMethod.SIMPLE);
        bindCard(binding.methodCoinsCard, binding.methodCoinsStatus, DivinationMethod.COINS);
        bindCard(binding.methodYarrowCard, binding.methodYarrowStatus, DivinationMethod.YARROW);
    }

    private void bindCard(LinearLayout card, TextView status, DivinationMethod method) {
        MethodOptionPresentation presentation = MethodOptionPresentation.from(method, selected);
        card.setSelected(presentation.selected);
        card.setBackgroundResource(presentation.selected ? R.drawable.bg_card_selected : R.drawable.bg_card);
        card.setContentDescription(presentation.contentDescription);
        status.setVisibility(presentation.selected ? View.VISIBLE : View.GONE);
        card.setOnClickListener(v -> {
            selected = method;
            bindMethodCards();
        });
    }
}
