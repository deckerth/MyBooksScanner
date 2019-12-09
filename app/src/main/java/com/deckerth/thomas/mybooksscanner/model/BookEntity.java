package com.deckerth.thomas.mybooksscanner.model;

/**
 * Created by Thomas on 20.07.2018.
 */

public class BookEntity implements Book {

    private String isbn;
    private String title;

    public BookEntity(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }

    public BookEntity(String isbn) {
        this.isbn = isbn;
        this.title = "";
    }

    @Override
    public String getISBN() {
        return isbn;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
