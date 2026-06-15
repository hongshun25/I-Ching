package fcu.app.i_ching.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.RecordRepository;

public class RecordsViewModel extends AndroidViewModel {
    public enum Action {
        UPDATE_NOTE,
        DELETE
    }

    public static class ActionState {
        public final Action action;
        public final long recordId;
        public final boolean success;

        ActionState(Action action, long recordId, boolean success) {
            this.action = action;
            this.recordId = recordId;
            this.success = success;
        }
    }

    private final RecordRepository repository;
    private final LiveData<List<DivinationRecord>> records;
    private final MutableLiveData<Event<ActionState>> actionEvents = new MutableLiveData<>();

    public RecordsViewModel(@NonNull Application application) {
        super(application);
        repository = RecordRepository.get(application);
        records = repository.records();
    }

    public LiveData<List<DivinationRecord>> records() {
        return records;
    }

    public LiveData<Event<ActionState>> actionEvents() {
        return actionEvents;
    }

    public void updateNote(long id, String note) {
        repository.updateNote(id, note, success ->
                actionEvents.setValue(new Event<>(new ActionState(Action.UPDATE_NOTE, id, Boolean.TRUE.equals(success)))));
    }

    public void delete(long id) {
        repository.delete(id, success ->
                actionEvents.setValue(new Event<>(new ActionState(Action.DELETE, id, Boolean.TRUE.equals(success)))));
    }
}
