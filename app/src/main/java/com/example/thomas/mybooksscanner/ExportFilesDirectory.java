package com.example.thomas.mybooksscanner;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.thomas.mybooksscanner.model.ExportFile;
import com.example.thomas.mybooksscanner.model.ExportFileEntity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExportFilesDirectory {

    private static ExportFilesDirectory sInstance;

    private MutableLiveData<List<ExportFileEntity>> mObservableFiles;

    private Boolean mContentRead = Boolean.FALSE;

    private ExportFilesDirectory() {
        mObservableFiles = new MutableLiveData<>();
    }

    public LiveData<List<ExportFileEntity>> getFiles() {
        if (!mContentRead) ReadDirectory();
        return mObservableFiles;
    }

    public void Invalidate() { mContentRead = Boolean.FALSE; }

    private void ReadDirectory() {
        File dir = BasicApp.getContext().getFilesDir();
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".xml");
            }
        };

        File[] files = dir.listFiles(filter);

        List<ExportFileEntity> list = new ArrayList<ExportFileEntity>(1);

        for (File file : files) {
            list.add(new ExportFileEntity(file));
        }

        mObservableFiles.setValue(list);
        mContentRead = Boolean.TRUE;
    }

    public void selectAll(boolean isChecked) {

        List<ExportFileEntity> list = new ArrayList<ExportFileEntity>(1);

        Iterator<ExportFileEntity> iter = mObservableFiles.getValue().iterator();

        while (iter.hasNext())  {
            ExportFileEntity f = iter.next();
            list.add(new ExportFileEntity(f.getFile(),isChecked));
        }

        mObservableFiles.setValue(list);
    }

    public int getSelectedCount() {
        int count = 0;
        Iterator<ExportFileEntity> iter = mObservableFiles.getValue().iterator();

        while (iter.hasNext())  {
            if ( iter.next().getSelected() ) count++;
        }
        return count;
    }

    public File getFile(String filename) {
        Iterator<ExportFileEntity> iter = mObservableFiles.getValue().iterator();

        while (iter.hasNext())  {
            ExportFileEntity f = iter.next();
            if ( f.getFile().getName().equals(filename) ) return f.getFile();
        }
        return null;
    }

    public void deleteSelectedFiles() {
        Iterator<ExportFileEntity> iter = mObservableFiles.getValue().iterator();
        List<ExportFileEntity> list = new ArrayList<ExportFileEntity>(1);

        while (iter.hasNext())  {
            ExportFileEntity f = iter.next();
            if ( f.getSelected() ) {
                f.getFile().delete();
            } else {
                list.add(f);
            }
        }
        mObservableFiles.setValue(list);
    }

    public static ExportFilesDirectory getInstance() {
        if (sInstance == null) {
            sInstance = new ExportFilesDirectory();
        }
        return sInstance;
    }

}
