package fcu.app.i_ching.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.data.SettingsStore;
import fcu.app.i_ching.databinding.FragmentProfileSettingsBinding;

public class ProfileSettingsFragment extends Fragment {
    private ActivityResultLauncher<String> exportJsonLauncher;
    private ActivityResultLauncher<String> exportTextLauncher;
    private ProfileSettingsViewModel viewModel;
    private String pendingExportContent;
    private String pendingExportLabel;
    private FragmentProfileSettingsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileSettingsViewModel.class);
        exportJsonLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> writeExport(uri));
        exportTextLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"), uri -> writeExport(uri));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        SettingsStore settings = activity.settings();
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false);
        NavigationChrome.bind(activity, binding.topBar, binding.bottomNav, NavigationChrome.TAB_PROFILE);
        bindRows(settings);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.exportEvents().observe(getViewLifecycleOwner(), event -> {
            ProfileSettingsViewModel.ExportState state = event.getContentIfNotHandled();
            if (state == null) return;
            preparePendingExport(state.content, state.label);
            if (state.kind == ProfileSettingsViewModel.ExportKind.JSON) {
                exportJsonLauncher.launch(state.fileName);
            } else {
                exportTextLauncher.launch(state.fileName);
            }
        });
        viewModel.deleteAllEvents().observe(getViewLifecycleOwner(), event -> {
            Boolean success = event.getContentIfNotHandled();
            if (success == null) return;
            Toast.makeText(requireContext(), success ? "全部紀錄已刪除" : "刪除失敗", Toast.LENGTH_SHORT).show();
        });
    }

    private void bindRows(SettingsStore settings) {
        binding.profileDarkModeSwitch.setChecked(settings.isDarkMode());
        binding.profileDarkModeSwitch.setOnCheckedChangeListener((buttonView, checked) -> {
            settings.setDarkMode(checked);
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        });
        binding.profileReduceMotionSwitch.setChecked(settings.isReduceMotion());
        binding.profileReduceMotionSwitch.setOnCheckedChangeListener((buttonView, checked) ->
                settings.setReduceMotion(checked));
        binding.profileAutoSaveSwitch.setChecked(settings.isAutoSave());
        binding.profileAutoSaveSwitch.setOnCheckedChangeListener((buttonView, checked) ->
                settings.setAutoSave(checked));

        binding.profileExportJson.setOnClickListener(v -> startJsonExport());
        binding.profileExportText.setOnClickListener(v -> startTextExport());
        binding.profileDeleteAll.setOnClickListener(v -> confirmDeleteAll());
    }

    private void startJsonExport() {
        viewModel.exportJson();
    }

    private void startTextExport() {
        viewModel.exportText();
    }

    void writeExport(Uri uri) {
        String content = pendingExportContent;
        String label = pendingExportLabel;
        clearPendingExport();
        if (uri == null || content == null) return;
        try (OutputStream stream = requireContext().getContentResolver().openOutputStream(uri)) {
            writeExportContent(stream, content);
            Toast.makeText(requireContext(), (label == null ? "" : label) + "已匯出", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "匯出失敗", Toast.LENGTH_SHORT).show();
        }
    }

    static void writeExportContent(OutputStream stream, String content) throws IOException {
        if (stream == null) throw new IOException("Cannot open export target");
        OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        writer.write(content == null ? "" : content);
        writer.flush();
    }

    void preparePendingExport(String content, String label) {
        pendingExportContent = content;
        pendingExportLabel = label;
    }

    boolean hasPendingExport() {
        return pendingExportContent != null || pendingExportLabel != null;
    }

    private void clearPendingExport() {
        pendingExportContent = null;
        pendingExportLabel = null;
    }

    private void confirmDeleteAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle("刪除全部紀錄？")
                .setMessage("所有本機占卜紀錄與筆記都會被移除，此操作無法復原。")
                .setNegativeButton("取消", null)
                .setPositiveButton("刪除全部", (dialog, which) -> {
                    viewModel.deleteAllRecords();
                })
                .show();
    }
}
