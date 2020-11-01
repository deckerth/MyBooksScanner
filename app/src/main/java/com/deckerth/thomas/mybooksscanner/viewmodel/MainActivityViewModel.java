package com.deckerth.thomas.mybooksscanner.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.deckerth.thomas.mybooksscanner.DataRepository;
import com.deckerth.thomas.mybooksscanner.ExportFileContent;

public class MainActivityViewModel extends AndroidViewModel {

    // --Commented out by Inspection (09.12.2019 20:01):private static MainActivityViewModel sInstance;

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
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<Boolean> getBooksExist() {
        return mObservableBookListFilled;
    }
    public LiveData<Boolean> getExportFileFilled() { return  mObservableExportFileFilled; }
}
