package com.nrs.nsnik.drivoolattendance.network;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncNetwork extends AsyncTask<URL,Void,InputStream>{

    //private static AsyncNetwork mAsyncNetworkInstance = new AsyncNetwork();
    //private static InputStream mInputStream;
    private static final String LOG_TAG = AsyncNetwork.class.getSimpleName();

    public AsyncNetwork(){

    }

    /*public static AsyncNetwork getAsyncNetwork(){
        return mAsyncNetworkInstance;
    }

    public  static InputStream getInputStream(){
        return mInputStream;
    }

    public static void cancelAsyncTask(){
        mAsyncNetworkInstance.cancel(true);
    }*/

    @Override
    protected InputStream doInBackground(URL... params) {
        return connectTONetwork(params[0]);
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        //mInputStream = inputStream;
        super.onPostExecute(inputStream);
    }

    private InputStream connectTONetwork(URL url){
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode()==200){
                inputStream = httpURLConnection.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpURLConnection!=null) {
                httpURLConnection.disconnect();
            }
        }
        return inputStream;
    }
}
