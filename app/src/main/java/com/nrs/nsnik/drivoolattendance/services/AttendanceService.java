package com.nrs.nsnik.drivoolattendance.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.nrs.nsnik.drivoolattendance.MainActivity;
import com.nrs.nsnik.drivoolattendance.R;


public class AttendanceService extends Service {

    private static final int NOTIFICATION_ID = 987;

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
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
