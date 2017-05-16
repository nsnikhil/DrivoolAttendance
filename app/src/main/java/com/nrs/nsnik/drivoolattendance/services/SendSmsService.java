package com.nrs.nsnik.drivoolattendance.services;


import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.network.AsyncNetwork;

import java.net.MalformedURLException;
import java.net.URL;

public class SendSmsService extends IntentService{


    public SendSmsService() {
        super("SmsService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new AsyncNetwork().execute(createUrl(intent.getExtras().getString(getResources().getString(R.string.intentKeyMessage)),
                intent.getExtras().getString(getResources().getString(R.string.intentkeyPhoneNo))));
        return super.onStartCommand(intent, flags, startId);
    }

    private URL createUrl(String message,String phoneNo){
        String baseUrl = getResources().getString(R.string.uriBaseUrl);
        URL url = null;
        try {
            url = new  URL(Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("msgtxt",message)
                    .appendQueryParameter("receipientno",phoneNo)
                    .build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

}
