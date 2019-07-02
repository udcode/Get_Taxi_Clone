package com.avi_ud.gettaxi2.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public final class Communication {

    public static void call(String tel, Activity activity){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+tel));
        activity.startActivity(intent);
    }

    public static void sendSms(String tel, Activity activity){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"+tel));
        activity.startActivity(intent);
    }

    public static void sendEmail(String email, Activity activity){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+email));
        activity.startActivity(intent);
    }

    public static void openAddressOnMap(String address, Activity activity){
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        activity.startActivity(mapIntent);
    }
}
