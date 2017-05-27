package com.nrs.nsnik.drivoolattendance.services;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.interfaces.RetroFitApiCalls;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendSmsService extends IntentService {

    private Retrofit mRetrofit;
    private static final String TAG = SendSmsService.class.getSimpleName();

    public SendSmsService() {
        super("SmsService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendSms(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private Map<String, String> getParams(Intent intent) {
        Map<String, String> params = new HashMap<>();
        params.put("msgtxt", intent.getExtras().getString(getResources().getString(R.string.intentKeyMessage)));
        params.put("receipientno", intent.getExtras().getString(getResources().getString(R.string.intentkeyPhoneNo)));
        return params;
    }

    private Retrofit getSmsClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(getResources().getString(R.string.smsBaseUrl))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    private void sendSms(Intent inetnt){
        RetroFitApiCalls apiCalls = getSmsClient().create(RetroFitApiCalls.class);
        apiCalls.sendSms(getParams(inetnt)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, "Send");
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

}
