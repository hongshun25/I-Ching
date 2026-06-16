package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.databinding.FragmentOnboardingBinding;
import fcu.app.i_ching.databinding.ItemOnboardingPageBinding;

public class OnboardingFragment extends Fragment {
    private final PagerSnapHelper snapHelper = new PagerSnapHelper();
    private FragmentOnboardingBinding binding;
    private final int[] artDrawables = {
            R.drawable.art_scholar_waterfall,
            R.drawable.ic_auto_awesome_24,
            R.drawable.ic_history_24
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        binding.onboardingRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        binding.onboardingRecycler.setAdapter(new Adapter(
                getResources().getStringArray(R.array.onboarding_page_titles),
                getResources().getStringArray(R.array.onboarding_page_bodies)
        ));
        snapHelper.attachToRecyclerView(binding.onboardingRecycler);
        binding.onboardingRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) updateIndicator(snappedPosition());
            }
        });
        binding.onboardingStartButton.setOnClickListener(v -> activity.completeOnboarding());
        binding.localModeButton.setOnClickListener(v -> activity.enterLocalMode());
        updateIndicator(0);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        snapHelper.attachToRecyclerView(null);
        binding = null;
        super.onDestroyView();
    }

    private int snappedPosition() {
        if (binding == null) return 0;
        RecyclerView.LayoutManager layoutManager = binding.onboardingRecycler.getLayoutManager();
        if (layoutManager == null) return 0;
        View snap = snapHelper.findSnapView(layoutManager);
        if (snap == null) return 0;
        int position = binding.onboardingRecycler.getChildAdapterPosition(snap);
        return position == RecyclerView.NO_POSITION ? 0 : position;
    }

    private void updateIndicator(int position) {
        if (binding == null) return;
        View[] dots = {
                binding.onboardingDotDaily,
                binding.onboardingDotQuestion,
                binding.onboardingDotRecords
        };
        for (int i = 0; i < dots.length; i++) {
            boolean selected = i == position;
            dots[i].setSelected(selected);
        }
        binding.onboardingIndicator.setContentDescription(
                getString(R.string.onboarding_indicator, position + 1, dots.length));
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        private final String[] titles;
        private final String[] bodies;
        private final int itemCount;

        Adapter(String[] titles, String[] bodies) {
            this.titles = titles;
            this.bodies = bodies;
            this.itemCount = Math.min(artDrawables.length, Math.min(titles.length, bodies.length));
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemOnboardingPageBinding itemBinding = ItemOnboardingPageBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            itemBinding.getRoot().setLayoutParams(new RecyclerView.LayoutParams(
                    parent.getResources().getDisplayMetrics().widthPixels,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return new Holder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.binding.onboardingPageArt.setImageResource(artDrawables[position]);
            holder.binding.onboardingPageTitle.setText(titles[position]);
            holder.binding.onboardingPageBody.setText(bodies[position]);
            holder.itemView.setContentDescription(titles[position] + "。" + bodies[position]);
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        class Holder extends RecyclerView.ViewHolder {
            final ItemOnboardingPageBinding binding;

            Holder(@NonNull ItemOnboardingPageBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
