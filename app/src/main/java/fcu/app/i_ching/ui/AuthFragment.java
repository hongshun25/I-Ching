package fcu.app.i_ching.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.AccountStore;
import fcu.app.i_ching.data.RecordRepository;
import fcu.app.i_ching.databinding.FragmentAuthBinding;

public class AuthFragment extends Fragment {
    private FragmentAuthBinding binding;
    private AccountStore accountStore;
    private boolean showingRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        accountStore = AccountStore.get(requireContext());
        InsetsHelper.applyFullscreenScrollInsets(binding.getRoot());
        bindActions((MainActivity) requireActivity());
        showLogin();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void bindActions(MainActivity activity) {
        binding.authLoginTab.setOnClickListener(v -> showLogin());
        binding.authRegisterTab.setOnClickListener(v -> showRegister());
        binding.authSwitchRegisterButton.setOnClickListener(v -> showRegister());
        binding.authSwitchLoginButton.setOnClickListener(v -> showLogin());
        binding.authLoginButton.setOnClickListener(v -> login(activity));
        binding.authRegisterButton.setOnClickListener(v -> register(activity));
        binding.authSkipButton.setOnClickListener(v -> {
            accountStore.useGuest();
            activity.enterLocalMode();
        });
    }

    private void showLogin() {
        showingRegister = false;
        renderMode();
    }

    private void showRegister() {
        showingRegister = true;
        renderMode();
    }

    private void renderMode() {
        if (binding == null) return;
        hideMessage();
        binding.authLoginForm.setVisibility(showingRegister ? View.GONE : View.VISIBLE);
        binding.authRegisterForm.setVisibility(showingRegister ? View.VISIBLE : View.GONE);
        binding.authLoginTab.setTextColor(color(showingRegister ? R.color.ic_text_muted : R.color.ic_ink));
        binding.authRegisterTab.setTextColor(color(showingRegister ? R.color.ic_ink : R.color.ic_text_muted));
        binding.authLoginTab.setTypeface(Typeface.DEFAULT, showingRegister ? Typeface.NORMAL : Typeface.BOLD);
        binding.authRegisterTab.setTypeface(Typeface.DEFAULT, showingRegister ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void login(MainActivity activity) {
        AccountStore.AuthResult result = accountStore.login(
                text(binding.authLoginEmail),
                text(binding.authLoginPassword)
        );
        if (!result.success) {
            showMessage(result.message);
            return;
        }
        activity.enterAuthenticatedMode();
    }

    private void register(MainActivity activity) {
        AccountStore.AuthResult result = accountStore.register(
                text(binding.authRegisterEmail),
                text(binding.authRegisterPassword),
                text(binding.authRegisterConfirm)
        );
        if (!result.success) {
            showMessage(result.message);
            return;
        }
        if (result.transferGuestData) {
            activity.settings().transferGuestSettingsTo(result.account.id);
            RecordRepository.get(requireContext()).transferGuestRecordsTo(result.account.id, success -> {
                Toast.makeText(requireContext(), "本機帳號已建立", Toast.LENGTH_SHORT).show();
                activity.enterAuthenticatedMode();
            });
        } else {
            Toast.makeText(requireContext(), "本機帳號已建立", Toast.LENGTH_SHORT).show();
            activity.enterAuthenticatedMode();
        }
    }

    private String text(EditText input) {
        return input == null || input.getText() == null ? "" : input.getText().toString();
    }

    private void showMessage(String message) {
        binding.authMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        binding.authMessage.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        binding.authMessage.setVisibility(View.GONE);
        binding.authMessage.setText("");
    }

    private int color(int colorRes) {
        return requireContext().getColor(colorRes);
    }
}
