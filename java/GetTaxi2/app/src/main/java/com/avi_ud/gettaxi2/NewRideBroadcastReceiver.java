package com.avi_ud.gettaxi2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.avi_ud.gettaxi2.controller.activities.Main;
import com.avi_ud.gettaxi2.controller.fragments.OpenRides;
import com.avi_ud.gettaxi2.model.entities.Ride;
import com.avi_ud.gettaxi2.utils.Globals;


public class NewRideBroadcastReceiver extends BroadcastReceiver {
    NotificationManagerCompat notificationManager;
    String chanel_id;
    Notification summery = null;
    Boolean firstSummery = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "New Ride", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals(Globals.Strings.ACTION_NEW_RIDE)) {
            Bundle extras = intent.getExtras();
            NotificationCompat.Builder notification_builder;
            CharSequence ridePrevDetails =  extras.getCharSequence(Globals.Strings.BrodcastExtraKey);
            NotificationManager notification_manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 chanel_id = "3000";
                CharSequence name = "Channel Name";
                String description = "Chanel Description";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.BLUE);
                notification_manager.createNotificationChannel(mChannel);
                notification_builder = new NotificationCompat.Builder(context, chanel_id);
            } else {
                notification_builder = new NotificationCompat.Builder(context);
            }
            Intent mainIntent = new Intent(context, Main.class);
            mainIntent.putExtra(Main.FragmentType.OPEN_RIDES.toString(),Main.FragmentType.OPEN_RIDES.toString());
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,mainIntent,0);
         notification_builder
                    .setSmallIcon(R.drawable.ic_baseline_navigation_24px)
                    .setContentTitle("A new Ride Was ordered")
                    .setContentText(ridePrevDetails)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setGroup(Globals.Strings.GROUP_KEY_Rides)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if(summery == null)
                summery = new NotificationCompat.Builder(context, chanel_id)
                    .setContentTitle("Rides")
                    //set content text to support devices running API level < 24
                    .setContentText("A new Ride was Added")
                    .setSmallIcon(R.drawable.ic_baseline_navigation_24px)
                    .setGroup(Globals.Strings.GROUP_KEY_Rides)
                    //set this notification as the summary for the group
                    .setGroupSummary(true)
                    .build();

            notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(Globals.notificationId++, notification_builder.build());

            notification_manager.notify(0, summery);
        }
    }
}
