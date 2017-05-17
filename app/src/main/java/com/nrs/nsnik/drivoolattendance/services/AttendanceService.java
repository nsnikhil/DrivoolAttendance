package com.nrs.nsnik.drivoolattendance.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.nrs.nsnik.drivoolattendance.MainActivity;
import com.nrs.nsnik.drivoolattendance.R;

/**
 * Created by Nikhil on 17-May-17.
 */

public class AttendanceService extends IntentService {

    private static final int NOTIFICATION_ID = 987;

    public AttendanceService() {
        super("attendance");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.trip))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.app_name))
                .build();
        startForeground(NOTIFICATION_ID, notification);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
