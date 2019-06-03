package com.example.thomas.mybooksscanner.model;

/**
 * Created by Thomas on 20.07.2018.
 */

public interface Book {
    String getISBN();
    String getTitle();

    void setISBN(String isbn);
}
