package com.example.zavrsniapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BroadcastReminder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder bulider = new NotificationCompat.Builder(context, "notifyUser")
                .setSmallIcon(R.drawable.ic_logo_final)
                .setContentTitle("Nadolazeći trošak")
                .setContentText("Prema vašim kreiranim planovima postoje troškovi kojima je rok dospijeća danas")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, bulider.build());
    }

    public void getData() {
    }
}
