package com.example.android.zuruapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Book>> {
    MediaPlayer song;
    String USGS_REQUEST_URL = null;
    BookAdapter bookAdapter;
    android.content.Loader<ArrayList<Book>> loader;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(getLoaderManager().getLoader(1) != null){
            getLoaderManager().initLoader(1, null, MainActivity.this);
        }



        if(info != null && info.isConnectedOrConnecting()){
            Button button = (Button) findViewById(R.id.searchButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    EditText editText = (EditText) findViewById(R.id.searchInput);
                    String search = editText.getText().toString();
                    String part = "https://www.googleapis.com/books/v1/volumes?q=" + search + "&maxResults=30";
                    USGS_REQUEST_URL = part;
                    Log.e("Tag", "Restart Loader");
                    if(getLoaderManager().getLoader(1) == null){
                        getLoaderManager().initLoader(1, null, MainActivity.this);
                    }
                    else {
                        getLoaderManager().restartLoader(1, null, MainActivity.this);
                    }
                }
            });

        }
        else{
            TextView textView = (TextView) findViewById(R.id.internettext);
            textView.setText("No internet connection!");
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setEmptyView(textView);
        }

    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {
        Log.e("Tag", "oCreateLoader");
        return new BooksLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> data) {
        if(data == null){
            TextView textView = (TextView) findViewById(R.id.internettext);
            textView.setText("No Result found!");
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setEmptyView(textView);
        }
        else{
            Log.e("Tag", "onLoadFinished");
            bookAdapter = new BookAdapter(MainActivity.this, data);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(bookAdapter);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {
        Log.e("Tag", "onLoaderReset");
        bookAdapter = new BookAdapter(MainActivity.this, new ArrayList<Book>());
    }
}
