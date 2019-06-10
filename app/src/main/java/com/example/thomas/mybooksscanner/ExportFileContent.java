package com.example.thomas.mybooksscanner;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Xml;

import com.example.thomas.mybooksscanner.model.BookEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 21.07.2018.
 */

public class ExportFileContent {

    private static ExportFileContent sInstance;

    private MutableLiveData<List<BookEntity>> mObservableBooks;
    private MutableLiveData<Boolean> mObservableBookListFilled;

    private ExportFileContent() {
        mObservableBooks = new MutableLiveData<>();
        mObservableBookListFilled = new MutableLiveData<>();
        Clear();
    }

    private void Clear() {
        List<BookEntity> list = new ArrayList<BookEntity>(1);
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
        List<BookEntity> list = new ArrayList<BookEntity>(1);

        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return;
        }

        try {
            xmlPullParser.setInput(br);
            int eventType = 0;
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
        } catch (XmlPullParserException e) {
            return;
        } catch (IOException e) {
            return;
        }

        mObservableBooks.setValue(list);
        mObservableBookListFilled.setValue(list.size() > 0);
    }
}

