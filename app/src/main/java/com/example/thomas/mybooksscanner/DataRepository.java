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

public class DataRepository {

    private static DataRepository sInstance;

    private MutableLiveData<List<BookEntity>> mObservableBooks;
    private MutableLiveData<Boolean> mObservableBookListFilled;

    private Boolean mModified;

    private DataRepository() {
        mObservableBooks = new MutableLiveData<>();
        mObservableBookListFilled = new MutableLiveData<>();
        Clear();
    }

    public void Clear() {
        List<BookEntity> list = new ArrayList<BookEntity>(1);
        mObservableBooks.setValue(list);
        mObservableBookListFilled.setValue(Boolean.FALSE);

        mModified = Boolean.FALSE;
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
        mObservableBookListFilled.setValue(Boolean.TRUE);
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
    public LiveData<Boolean> getBooksExist() {return  mObservableBookListFilled; }

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
            ExportFilesDirectory.getInstance().Invalidate();
        }catch(Exception e)
        {
            Log.e("Exception","Exception occured in writing");
        }
        return uri;
    }

}

