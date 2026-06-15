package fcu.app.i_ching.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.RecordRepository;

public class ResultViewModel extends AndroidViewModel {
    private final RecordRepository repository;

    public ResultViewModel(@NonNull Application application) {
        super(application);
        repository = RecordRepository.get(application);
    }

    public DivinationRecord ensureAutoSaved(DivinationResult result, boolean autoSave) {
        DivinationRecord existing = repository.find(result.createdAt);
        if (existing != null) return existing;
        if (!autoSave) return null;
        DivinationRecord record = DivinationRecord.fromResult(result, "");
        repository.addOrUpdate(record);
        return record;
    }

    public DivinationRecord saveNote(DivinationResult result, long recordId, String note) {
        DivinationRecord existing = recordId == ResultFragment.NO_RECORD_ID ? null : repository.find(recordId);
        if (existing == null) existing = repository.find(result.createdAt);
        if (existing == null) {
            DivinationRecord record = DivinationRecord.fromResult(result, note);
            repository.addOrUpdate(record);
            return record;
        }
        repository.updateNote(existing.id, note);
        return existing.withNote(note);
    }
}
