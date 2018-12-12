package com.mmjang.ankihelper.data.book;

import java.util.ArrayList;
import java.util.List;

public class DefaultBook {
    public static List<Book> getDefaultBook(){
        Book book1 = new Book(
            System.currentTimeMillis(),
            System.currentTimeMillis(),
                "The Graveyard Book",
                "Neil Gaiman",
                "file:///android_asset/book/grave.epub",
                ""
        );

        Book book2 = new Book(
          System.currentTimeMillis(),
          System.currentTimeMillis(),
          "Moon over Manifest",
                "Clare Vanderpool",
                "file:///android_asset/book/moon.epub",
                ""
        );
        List<Book> books = new ArrayList<>();
        books.add(book1);
        //books.add(book2);
        return books;
    }
}
