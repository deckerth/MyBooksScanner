package com.deckerth.thomas.mybooksscanner;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Xml;

import com.deckerth.thomas.mybooksscanner.model.BookEntity;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas on 21.07.2018.
 */

public class DataRepository {

    private static DataRepository sInstance;

    private final MutableLiveData<List<BookEntity>> mObservableBooks;
    private final MutableLiveData<Boolean> mObservableBookListFilled;

    private Boolean mModified;

    private DataRepository() {
        mObservableBooks = new MutableLiveData<>();
        mObservableBookListFilled = new MutableLiveData<>();
        Clear();
    }

    public void Clear() {
        List<BookEntity> list = new ArrayList<>(1);
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
        List<BookEntity> list = new ArrayList<>(1);
        list.addAll(Objects.requireNonNull(mObservableBooks.getValue()));
        list.add(bookEntity);
        mModified = Boolean.TRUE;
        mObservableBooks.setValue(list);
        mObservableBookListFilled.setValue(Boolean.TRUE);
    }

// --Commented out by Inspection START (09.12.2019 19:58):
//    public Boolean getModified() {
//        return mModified;
//    }
// --Commented out by Inspection STOP (09.12.2019 19:58)

    public Boolean bookExists(BookEntity bookEntity) {

        for (BookEntity entity : mObservableBooks.getValue()) {
            if (entity.getISBN().equals(bookEntity.getISBN())) return Boolean.TRUE;
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());
        return "scannedBooks_"+currentDateAndTime+".xml";
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public Uri exportBooksToXML(Context context) {

        if (Objects.requireNonNull(mObservableBooks.getValue()).size() == 0) return null;

        String filename = buildExportFilename();
        FileOutputStream outputStream;
        Uri uri;

        try {
            File file = new File(context.getFilesDir() + "/" + filename);
            uri = FileProvider.getUriForFile(context,"com.deckerth.thomas.mybooksscanner.fileprovider",file);
            outputStream = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        XmlSerializer serializer = Xml.newSerializer();
        try{
            serializer.setOutput(outputStream, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.startTag(null, "scannedBooks");

            for (BookEntity book : mObservableBooks.getValue()) {
                serializer.startTag(null, "book");
                serializer.attribute(null, "isbn", book.getISBN());
                serializer.attribute(null, "title", book.getTitle());
                serializer.endTag(null, "book");
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

