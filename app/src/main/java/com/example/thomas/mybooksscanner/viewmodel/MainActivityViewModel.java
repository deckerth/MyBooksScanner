package com.example.thomas.mybooksscanner.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.example.thomas.mybooksscanner.DataRepository;
import com.example.thomas.mybooksscanner.ExportFileContent;

public class MainActivityViewModel extends AndroidViewModel {

    public static MainActivityViewModel sInstance;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<Boolean> mObservableBookListFilled;
    private final MediatorLiveData<Boolean> mObservableExportFileFilled;

    public MainActivityViewModel(Application application) {
        super(application);

        mObservableBookListFilled = new MediatorLiveData<>();
        mObservableExportFileFilled = new MediatorLiveData<>();

        // set by default null, until we get data from the database.
        mObservableBookListFilled.setValue(null);
        mObservableExportFileFilled.setValue(null);

        DataRepository repo = DataRepository.getInstance();
        LiveData<Boolean> booksExist = repo.getBooksExist();

        ExportFileContent content = ExportFileContent.getInstance();
        LiveData<Boolean> contentFileFilled = content.getBooksExist();

        // observe the changes of the flag from the database and forward them
        mObservableBookListFilled.addSource(booksExist, mObservableBookListFilled::setValue);
        mObservableExportFileFilled.addSource(contentFileFilled,mObservableExportFileFilled::setValue);

        sInstance = this;
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<Boolean> getBooksExist() {
        return mObservableBookListFilled;
    }
    public LiveData<Boolean> getExportFileFilled() { return  mObservableExportFileFilled; }
}
