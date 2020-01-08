package com.example.android.zuruapp;

import java.util.Arrays;

/**
 * Created by user on 7/24/2019.
 */

public class Book {
    String title;
    String authors;

    public  Book(String title, String[] authors){
        this.title = title;
        this.authors = Arrays.toString(authors);
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setAuthors(String[] authors){
        this.authors = authors.toString();
    }

    public String getTitle(){
        return title;
    }

    public String getAuthors(){
        return authors;
    }
}
