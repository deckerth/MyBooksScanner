package com.deckerth.thomas.mybooksscanner.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.deckerth.thomas.mybooksscanner.ExportFileContent;
import com.deckerth.thomas.mybooksscanner.model.BookEntity;

import java.util.List;

/**
 * Created by Thomas on 21.07.2018.
 */

public class ExportFileContentViewModel extends AndroidViewModel {

    // --Commented out by Inspection (09.12.2019 19:59):private static ExportFileContentViewModel sInstance;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<BookEntity>> mObservableBooks;

    public ExportFileContentViewModel(Application application) {
        super(application);

        mObservableBooks = new MediatorLiveData<>();

        // set by default null, until we get data from the database.
        mObservableBooks.setValue(null);

        ExportFileContent repo = ExportFileContent.getInstance();
        LiveData<List<BookEntity>> books = repo.getBooks();

        // observe the changes of the bookings from the database and forward them
        mObservableBooks.addSource(books, mObservableBooks::setValue);
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<List<BookEntity>> getBooks() {
        return mObservableBooks;
    }
}
