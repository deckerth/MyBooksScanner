package com.deckerth.thomas.mybooksscanner;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Xml;

import com.deckerth.thomas.mybooksscanner.model.BookEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 21.07.2018.
 */

public class ExportFileContent {

    private static ExportFileContent sInstance;

    private final MutableLiveData<List<BookEntity>> mObservableBooks;
    private final MutableLiveData<Boolean> mObservableBookListFilled;

    private ExportFileContent() {
        mObservableBooks = new MutableLiveData<>();
        mObservableBookListFilled = new MutableLiveData<>();
        Clear();
    }

    private void Clear() {
        List<BookEntity> list = new ArrayList<>(1);
        mObservableBooks.setValue(list);
        mObservableBookListFilled.setValue(Boolean.FALSE);
    }

    public void LoadFile(File file) {
        Clear();
        ImportBooksFromXML(file);
    }

    public static ExportFileContent getInstance() {
        if (sInstance == null) {
            sInstance = new ExportFileContent();
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<BookEntity>> getBooks() {
        return mObservableBooks;
    }
    public LiveData<Boolean> getBooksExist() {return  mObservableBookListFilled; }

    private void ImportBooksFromXML(File file) {

        XmlPullParser xmlPullParser = Xml.newPullParser();
        BufferedReader br;
        List<BookEntity> list = new ArrayList<>(1);

        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return;
        }

        try {
            xmlPullParser.setInput(br);
            int eventType;
            eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if (xmlPullParser.getName().equals("book")) {
                        String title = "";
                        String isbn = "";
                        for( int i=0; i<xmlPullParser.getAttributeCount(); i++){
                            if (xmlPullParser.getAttributeName(i).equals("title")) {
                                title = xmlPullParser.getAttributeValue(i);
                            } else if (xmlPullParser.getAttributeName(i).equals("isbn")) {
                                isbn = xmlPullParser.getAttributeValue(i);
                            }
                        }
                        if (!isbn.isEmpty()) list.add(new BookEntity(isbn,title));
                    }
                }
                eventType = xmlPullParser.next();
            }
            System.out.println("End document");
        } catch (XmlPullParserException | IOException e) {
            return;
        }

        mObservableBooks.setValue(list);
        mObservableBookListFilled.setValue(list.size() > 0);
    }
}

