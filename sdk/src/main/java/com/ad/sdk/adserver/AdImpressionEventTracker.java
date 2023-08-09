package com.ad.sdk.adserver;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdImpressionEventTracker extends AsyncTask<String, Void, String> {
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    static final int AdFetchTimeout = 30000;
    public static final int CONNECTION_TIMEOUT = AdFetchTimeout;

    @Override
    protected String doInBackground(String... params){
        AdRequestParam adRequestObj = new AdRequestParam();

        String result;
        String inputLine;

        try {
//Create a URL object holding our url
            URL myUrl = new URL(params[0]);
//Create a connection
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();

//Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

//Connect to our url
            connection.connect();

//Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());

//Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

//Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

//Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();

//Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        }
        catch(IOException e){
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }


}