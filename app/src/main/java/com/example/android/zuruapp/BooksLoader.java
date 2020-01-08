package com.example.android.zuruapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by user on 7/24/2019.
 */

public class BooksLoader extends AsyncTaskLoader<ArrayList<Book>> {
    ArrayList<Book> mfinalBooks = null;
    String USGS_REQUEST_URL;

    public BooksLoader(Context context, String url){
        super(context);
        this.USGS_REQUEST_URL = url;
    }

    @Override
    protected void onStartLoading() {
        Log.e("Tag", "onStartLoading");
        if(mfinalBooks !=  null){
            deliverResult(mfinalBooks);
        }
        else{
            forceLoad();
        }
    }

    @Override
    public void deliverResult(ArrayList<Book> data) {
        mfinalBooks = data;
        super.deliverResult(data);
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        if(USGS_REQUEST_URL == null){
            return null;
        }
        Log.e("Tag", "Load in Background");
        URL url = createUrl(USGS_REQUEST_URL);
        String jsonResponse = "";
        try {
            jsonResponse = MakeHttp(url);
        } catch (IOException e) {
            Log.e("LOG_TAG", "Error in background making http ...", e);
        }

        return extractBooks(jsonResponse);
    }

    public URL createUrl(String link){
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            Log.e("LOG_TAG", "Error creating URL...", e);
        }
        return url;
    }

    public String readFromStream(InputStream inputStream){
        StringBuilder output = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line = bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            Log.e("Tag", "Problem reading from stream", e);
        }
        return output.toString();
    }

    public String MakeHttp(URL url) throws IOException  {

        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();


            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("Tag", Integer.toString(urlConnection.getResponseCode()));
            }
        }

        catch (IOException i){
            Log.e("Tag", "Problem retrieving the earthquake result from za internet ", i);
        }

        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public ArrayList<Book> extractBooks(String jsonResonse){
    ArrayList<Book> bookArrayList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResonse);
            JSONArray itemsArray = jsonObject.optJSONArray("items");
            if(itemsArray == null){
                return bookArrayList;
            }
            for(int i=0; i<itemsArray.length(); i++){
                JSONObject bookObject = itemsArray.getJSONObject(i);
                JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");
                String title = volumeObject.getString("title");
                JSONArray authorsJsonArray = volumeObject.getJSONArray("authors");
                String[] authorsArray = convertJsonArray(authorsJsonArray);
                Book currentBook = new Book(title, authorsArray);
                bookArrayList.add(currentBook);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return bookArrayList;
    }

    public String[] convertJsonArray(JSONArray array){
        String[] authorsArray = null;
        if(array!= null){
            authorsArray = new String[array.length()];
            for(int i=0; i<authorsArray.length; i++){
                authorsArray[i] = array.optString(i);
            }
        }
        return authorsArray;
    }

}
