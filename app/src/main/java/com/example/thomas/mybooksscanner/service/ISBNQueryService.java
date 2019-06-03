package com.example.thomas.mybooksscanner.service;

import android.app.PendingIntent;
import android.content.Intent;

import com.example.thomas.mybooksscanner.DataRepository;
import com.example.thomas.mybooksscanner.model.Book;
import com.example.thomas.mybooksscanner.model.BookEntity;
import com.example.thomas.mybooksscanner.ui.views.MainActivity;

/**
 * Created by Thomas on 22.07.2018.
 */

public class ISBNQueryService {

    private static ISBNQueryService sInstance = null;

    public static final int QUERY_REQUEST_CODE = 0;

    private static BookEntity mCurrentBook;

    public static ISBNQueryService GetInstance() {
        if (sInstance == null) {
            sInstance = new ISBNQueryService();
        }
        return sInstance;
    }

    public void SendRequest(BookEntity book) {
        mCurrentBook = book;
        PendingIntent pendingResult = MainActivity.mInstance.createPendingResult(QUERY_REQUEST_CODE,new Intent(),0);
        Intent intent = new Intent(MainActivity.mInstance.getApplicationContext(), DownloadRequestService.class);
        intent.putExtra(DownloadRequestService.URL_EXTRA,"http://classify.oclc.org/classify2/Classify?isbn="+book.getISBN());
        intent.putExtra(DownloadRequestService.PENDING_RESULT_EXTRA, pendingResult);
        MainActivity.mInstance.startService(intent);
    }

    public void ProcessResult(int requestCode, int resultCode, Intent data) {

        String result = "";

        if (requestCode == QUERY_REQUEST_CODE) {
            switch (resultCode) {
                case DownloadRequestService.INVALID_URL_CODE:
                    // result.setText("Invalid URL");
                    break;
                case DownloadRequestService.ERROR_CODE:
                    // result.setText("Error occurred");
                    break;
                case DownloadRequestService.RESULT_CODE:
                    mCurrentBook.setTitle(data.getStringExtra(DownloadRequestService.QUERY_RESULT_EXTRA));
                    break;
            }
            DataRepository.getInstance().storeBook(mCurrentBook);
        }
    }
}
