package com.example.thomas.mybooksscanner.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.example.thomas.mybooksscanner.DataRepository;
import com.example.thomas.mybooksscanner.ExportFilesDirectory;
import com.example.thomas.mybooksscanner.model.BookEntity;
import com.example.thomas.mybooksscanner.model.ExportFileEntity;

import java.util.List;

public class FilesListViewModel extends AndroidViewModel {

    public static FilesListViewModel sInstance;

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

        sInstance = this;
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<List<ExportFileEntity>> getFiles() {
        return mObservableFiles;
    }


}

