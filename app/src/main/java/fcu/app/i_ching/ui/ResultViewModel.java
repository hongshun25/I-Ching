package fcu.app.i_ching.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fcu.app.i_ching.NavigationArgs;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.RecordRepository;

public class ResultViewModel extends AndroidViewModel {
    public enum SaveAction {
        AUTO_SAVE,
        NOTE_SAVE
    }

    public static class SaveState {
        public final SaveAction action;
        public final DivinationRecord record;
        public final boolean success;

        SaveState(SaveAction action, DivinationRecord record, boolean success) {
            this.action = action;
            this.record = record;
            this.success = success;
        }
    }

    private final RecordRepository repository;
    private final MutableLiveData<Event<SaveState>> saveEvents = new MutableLiveData<>();

    public ResultViewModel(@NonNull Application application) {
        super(application);
        repository = RecordRepository.get(application);
    }

    public LiveData<Event<SaveState>> saveEvents() {
        return saveEvents;
    }

    public void ensureAutoSaved(DivinationResult result, boolean autoSave) {
        repository.find(result.createdAt, existing -> {
            if (existing != null) {
                emit(SaveAction.AUTO_SAVE, existing, true);
                return;
            }
            if (!autoSave) return;
            DivinationRecord record = DivinationRecord.fromResult(result, "");
            repository.addOrUpdate(record, saved -> emit(SaveAction.AUTO_SAVE, saved, saved != null));
        });
    }

    public void saveNote(DivinationResult result, long recordId, String note) {
        long firstLookup = recordId == NavigationArgs.NO_RECORD_ID ? result.createdAt : recordId;
        repository.find(firstLookup, existing -> {
            if (existing != null || firstLookup == result.createdAt) {
                saveResolved(result, existing, note);
                return;
            }
            repository.find(result.createdAt, fallback -> saveResolved(result, fallback, note));
        });
    }

    private void saveResolved(DivinationResult result, DivinationRecord existing, String note) {
        if (existing == null) {
            DivinationRecord record = DivinationRecord.fromResult(result, note);
            repository.addOrUpdate(record, saved -> emit(SaveAction.NOTE_SAVE, saved, saved != null));
            return;
        }
        repository.updateNote(existing.id, note, success ->
                emit(SaveAction.NOTE_SAVE, existing.withNote(note), Boolean.TRUE.equals(success)));
    }

    private void emit(SaveAction action, DivinationRecord record, boolean success) {
        saveEvents.setValue(new Event<>(new SaveState(action, record, success)));
    }
}
