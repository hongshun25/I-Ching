package fcu.app.i_ching.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.RecordRepository;

public class RecordsViewModel extends AndroidViewModel {
    private final RecordRepository repository;
    private final LiveData<List<DivinationRecord>> records;

    public RecordsViewModel(@NonNull Application application) {
        super(application);
        repository = RecordRepository.get(application);
        records = repository.records();
    }

    public LiveData<List<DivinationRecord>> records() {
        return records;
    }

    public boolean updateNote(long id, String note) {
        return repository.updateNote(id, note);
    }

    public boolean delete(long id) {
        return repository.delete(id);
    }
}
