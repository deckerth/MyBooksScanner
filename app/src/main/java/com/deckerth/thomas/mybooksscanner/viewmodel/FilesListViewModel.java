package com.deckerth.thomas.mybooksscanner.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.deckerth.thomas.mybooksscanner.ExportFilesDirectory;
import com.deckerth.thomas.mybooksscanner.model.ExportFileEntity;

import java.util.List;

public class FilesListViewModel extends AndroidViewModel {

    // --Commented out by Inspection (09.12.2019 19:59):private static FilesListViewModel sInstance;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<ExportFileEntity>> mObservableFiles;

    public FilesListViewModel(Application application) {
        super(application);

        mObservableFiles = new MediatorLiveData<>();

        // set by default null, until we get data from the database.
        mObservableFiles.setValue(null);

        ExportFilesDirectory repo = ExportFilesDirectory.getInstance();
        LiveData<List<ExportFileEntity>> files = repo.getFiles();

        // observe the changes of the bookings from the database and forward them
        mObservableFiles.addSource(files, mObservableFiles::setValue);
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<List<ExportFileEntity>> getFiles() {
        return mObservableFiles;
    }


}

