package com.example.thomas.mybooksscanner.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.example.thomas.mybooksscanner.DataRepository;
import com.example.thomas.mybooksscanner.model.BookEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 21.07.2018.
 */

public class BookListViewModel extends AndroidViewModel {

    public static BookListViewModel sInstance;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<BookEntity>> mObservableBooks;

    public BookListViewModel(Application application) {
        super(application);

        mObservableBooks = new MediatorLiveData<>();

        // set by default null, until we get data from the database.
        mObservableBooks.setValue(null);

        DataRepository repo = DataRepository.getInstance();
        LiveData<List<BookEntity>> books = repo.getBooks();

        // observe the changes of the bookings from the database and forward them
        mObservableBooks.addSource(books, mObservableBooks::setValue);

        sInstance = this;
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<List<BookEntity>> getBooks() {
        return mObservableBooks;
    }


}
