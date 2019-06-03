package com.example.thomas.mybooksscanner;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Xml;

import com.example.thomas.mybooksscanner.model.BookEntity;
import com.google.android.gms.common.internal.FallbackServiceBroker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 21.07.2018.
 */

public class DataRepository {

    private static DataRepository sInstance;

    private MutableLiveData<List<BookEntity>> mObservableBooks;

    private Boolean mModified;

    private DataRepository() {
        mObservableBooks = new MutableLiveData<>();
        Clear();
    }

    private void Clear() {
        List<BookEntity> list = new ArrayList<BookEntity>(1);
        mObservableBooks.setValue(list);

        mModified = Boolean.FALSE;

    }

    public DataRepository(File file) {
        this();
        ImportBooksFromXML(file);
        sInstance = this;
    }

    public static DataRepository getInstance() {
        if (sInstance == null) {
                    sInstance = new DataRepository();
            }
        return sInstance;
    }

    public void storeBook(BookEntity bookEntity) {
        List<BookEntity> list = new ArrayList<BookEntity>(1);
        list.addAll(mObservableBooks.getValue());
        list.add(bookEntity);
        mModified = Boolean.TRUE;
        mObservableBooks.setValue(list);
    }

    public Boolean getModified() {
        return mModified;
    }

    public Boolean bookExists(BookEntity bookEntity) {
        Iterator<BookEntity> iter = mObservableBooks.getValue().iterator();

        while (iter.hasNext()) {
            if (iter.next().getISBN().equals(bookEntity.getISBN())) return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<BookEntity>> getBooks() {
        return mObservableBooks;
    }

    private String buildExportFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());
        return "scannedBooks_"+currentDateAndTime+".xml";
    }

    public Uri exportBooksToXML(Context context) {

        if (mObservableBooks.getValue().size() == 0) return null;

        String filename = buildExportFilename();
        FileOutputStream outputStream = null;
        Uri uri = null;

        try {
            File file = new File(context.getFilesDir() + "/" + filename);
            uri = FileProvider.getUriForFile(context,"com.example.thomas.mybooksscanner.fileprovider",file);
            outputStream = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if ( outputStream == null) return null;

        XmlSerializer serializer = Xml.newSerializer();
        try{
            serializer.setOutput(outputStream, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.startTag(null, "scannedBooks");

            Iterator iter = mObservableBooks.getValue().iterator();

            while( iter.hasNext()) {
                BookEntity book = (BookEntity) iter.next();
                serializer.startTag(null,"book");
                serializer.attribute(null, "isbn", book.getISBN());
                serializer.attribute(null, "title", book.getTitle());
                serializer.endTag(null,"book");
            }
            serializer.endTag(null,"scannedBooks");
            serializer.endDocument();
            serializer.flush();
            outputStream.close();
        }catch(Exception e)
        {
            Log.e("Exception","Exception occured in writing");
        }
        Clear();
        return uri;
    }

    public void ImportBooksFromXML(File file) {

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
        mModified=Boolean.FALSE;
    }
}

