package fcu.app.i_ching.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import fcu.app.i_ching.data.RecordRepository;

public class ProfileSettingsViewModel extends AndroidViewModel {
    private final RecordRepository repository;

    public ProfileSettingsViewModel(@NonNull Application application) {
        super(application);
        repository = RecordRepository.get(application);
    }

    public String exportJson() {
        return repository.exportJson();
    }

    public String exportText() {
        return repository.exportText();
    }

    public void deleteAllRecords() {
        repository.deleteAll();
    }
}
