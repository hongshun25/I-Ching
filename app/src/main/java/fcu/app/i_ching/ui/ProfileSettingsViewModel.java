package fcu.app.i_ching.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fcu.app.i_ching.data.RecordRepository;

public class ProfileSettingsViewModel extends AndroidViewModel {
    public enum ExportKind {
        JSON,
        TEXT
    }

    public static class ExportState {
        public final ExportKind kind;
        public final String label;
        public final String fileName;
        public final String content;

        ExportState(ExportKind kind, String label, String fileName, String content) {
            this.kind = kind;
            this.label = label;
            this.fileName = fileName;
            this.content = content;
        }
    }

    private final RecordRepository repository;
    private final MutableLiveData<Event<ExportState>> exportEvents = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> deleteAllEvents = new MutableLiveData<>();

    public ProfileSettingsViewModel(@NonNull Application application) {
        super(application);
        repository = RecordRepository.get(application);
    }

    public LiveData<Event<ExportState>> exportEvents() {
        return exportEvents;
    }

    public LiveData<Event<Boolean>> deleteAllEvents() {
        return deleteAllEvents;
    }

    public void exportJson() {
        repository.exportJson(content -> exportEvents.setValue(new Event<>(
                new ExportState(ExportKind.JSON, "JSON", "i_ching_records.json", content))));
    }

    public void exportText() {
        repository.exportText(content -> exportEvents.setValue(new Event<>(
                new ExportState(ExportKind.TEXT, "純文字", "i_ching_records.txt", content))));
    }

    public void deleteAllRecords() {
        repository.deleteAll(success -> deleteAllEvents.setValue(new Event<>(Boolean.TRUE.equals(success))));
    }
}
