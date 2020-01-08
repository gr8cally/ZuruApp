package com.example.android.zuruapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 7/24/2019.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, ArrayList<Book> books){
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitem = convertView;
        if(listitem == null){
            listitem = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
        }

        Book currentBook = getItem(position);
        TextView titleView = (TextView) listitem.findViewById(R.id.title);
        titleView.setText(currentBook.getTitle());

        TextView authorView = (TextView) listitem.findViewById(R.id.authors);
        String authors = "Written by: " + currentBook.getAuthors().substring(1, currentBook.getAuthors().length()-1) + ".";
        authorView.setText( authors);

        return listitem;
    }
}
