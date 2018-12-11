package com.mmjang.ankihelper.data.book;

import android.content.ContentValues;

import com.mmjang.ankihelper.data.database.DBContract;

public class Book {
    private long id;
    private long lastOpenTime;
    private String bookName;
    private String author;
    private String bookPath;
    private String readPosition; //json

    public Book(long id, long lastOpenTime, String bookName, String author, String bookPath, String readPosition) {
        this.id = id;
        this.lastOpenTime = lastOpenTime;
        this.bookName = bookName;
        this.author = author;
        this.bookPath = bookPath;
        this.readPosition = readPosition;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLastOpenTime() {
        return lastOpenTime;
    }

    public void setLastOpenTime(long lastOpenTime) {
        this.lastOpenTime = lastOpenTime;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public String getReadPosition() {
        return readPosition;
    }

    public void setReadPosition(String readPosition) {
        this.readPosition = readPosition;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getAuthor(){
        return this.author;
    }

    public ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Book.COLUMN_ID, getId());
        contentValues.put(DBContract.Book.COLUMN_LAST_OPEN_TIME, getLastOpenTime());
        contentValues.put(DBContract.Book.COLUMN_BOOK_NAME, getBookName());
        contentValues.put(DBContract.Book.COLUMN_AUTHOR, getAuthor());
        contentValues.put(DBContract.Book.COLUMN_BOOK_PATH, getBookPath());
        contentValues.put(DBContract.Book.COLUMN_READ_POSITION, getReadPosition());
        return contentValues;
    }
}
