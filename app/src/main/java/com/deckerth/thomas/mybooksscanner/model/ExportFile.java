package com.deckerth.thomas.mybooksscanner.model;

import java.io.File;

public interface ExportFile {
    File getFile();
    void setFile(File file);
    String getName();
    Boolean getSelected();
    void setSelected(Boolean selected);
}
