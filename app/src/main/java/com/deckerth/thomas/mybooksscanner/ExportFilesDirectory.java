package com.deckerth.thomas.mybooksscanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deckerth.thomas.mybooksscanner.model.ExportFileEntity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExportFilesDirectory {

    private static ExportFilesDirectory sInstance;

    private final MutableLiveData<List<ExportFileEntity>> mObservableFiles;

    private Boolean mContentRead = Boolean.FALSE;

    private ExportFilesDirectory() {
        mObservableFiles = new MutableLiveData<>();
    }

    public LiveData<List<ExportFileEntity>> getFiles() {
        if (!mContentRead) ReadDirectory();
        return mObservableFiles;
    }

    void Invalidate() {
        mContentRead = Boolean.FALSE;
    }

    private void ReadDirectory() {
        File dir = BasicApp.getContext().getFilesDir();
        FileFilter filter = pathname -> pathname.getName().endsWith(".xml");

        File[] files = dir.listFiles(filter);

        List<ExportFileEntity> list = new ArrayList<>(1);

        for (File file : files) {
            list.add(new ExportFileEntity(file));
        }

        mObservableFiles.setValue(list);
        mContentRead = Boolean.TRUE;
    }

    public void selectAll(boolean isChecked) {

        List<ExportFileEntity> list = new ArrayList<>(1);

        for (ExportFileEntity f : Objects.requireNonNull(mObservableFiles.getValue()))
            list.add(new ExportFileEntity(f.getFile(), isChecked));

        mObservableFiles.setValue(list);
    }

    public int getSelectedCount() {
        int count = 0;

        for (ExportFileEntity exportFileEntity : Objects.requireNonNull(mObservableFiles.getValue()))
            if (exportFileEntity.getSelected()) count++;
        return count;
    }

    public File getFile(String filename) {
        for (ExportFileEntity f : Objects.requireNonNull(mObservableFiles.getValue())) {
            if (f.getFile().getName().equals(filename)) return f.getFile();
        }
        return null;
    }

    public void deleteSelectedFiles() {
        List<ExportFileEntity> list = new ArrayList<>(1);

        for (ExportFileEntity f : Objects.requireNonNull(mObservableFiles.getValue())) {
            if (f.getSelected()) {
                //noinspection ResultOfMethodCallIgnored
                f.getFile().delete();
            } else list.add(f);
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
