package com.example.nustsocialcircle.HelpingClasses;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MainApplication extends Application {

    public static final String NOTIFICATION_CHANNEL_LOW = "LOW PRIORITY CHANNEL";
    public static final String NOTIFICATION_CHANNEL_MEDIUM = "MEDIUM PRIORITY CHANNEL";
    public static final String NOTIFICATION_CHANNEL_HIGH = "HIGH PRIORITY CHANNEL";


    @Override
    public void onCreate() {
        super.onCreate();

        initializeNotificationChannels();
    }

    private void initializeNotificationChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel_low = new NotificationChannel(
                    NOTIFICATION_CHANNEL_LOW,
                    "Low priority channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            channel_low.setDescription("This channel receives notification which are of low priority");

            NotificationChannel channel_medium = new NotificationChannel(
                    NOTIFICATION_CHANNEL_MEDIUM,
                    "medium priority channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel_medium.setDescription("This channel receives notification which are of medium priority");

            NotificationChannel channel_high = new NotificationChannel(
                    NOTIFICATION_CHANNEL_HIGH,
                    "high priority channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel_medium.setDescription("This channel receives notification which are of high priority");


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel_low);
            manager.createNotificationChannel(channel_medium);
            manager.createNotificationChannel(channel_high);
        }
    }


}
