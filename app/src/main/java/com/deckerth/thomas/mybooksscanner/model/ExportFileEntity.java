package com.deckerth.thomas.mybooksscanner.model;

import java.io.File;

public class ExportFileEntity implements ExportFile {

    private File file;
    private Boolean selected;

    public ExportFileEntity(File file) {
        this.file = file;
        this.selected = false;
    }

    public ExportFileEntity(File file, Boolean isSelected) {
        this.file = file;
        this.selected = isSelected;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String getName() {
        if (file == null) {
            return null;
        } else {
            return file.getName();
        }
    }

    @Override
    public Boolean getSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
