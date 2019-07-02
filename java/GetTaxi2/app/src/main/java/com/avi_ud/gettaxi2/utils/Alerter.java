package com.avi_ud.gettaxi2.utils;

import android.app.AlertDialog;
import android.content.Context;

public class Alerter {
    public static void showErrorDialog(Context context, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("An error occurred!")
                .setMessage(msg)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
