package com.avi_ud.gettaxi2.utils;

import android.location.Location;

import com.avi_ud.gettaxi2.model.entities.Driver;

public final class Globals {
    public static Driver driver;
    public static Location driverLocation;
    public static int notificationId = 10;
    public static String dateFormat = "MM/dd/yy";
    public static String timeFormat = "HH:mm";

    public static class Strings{
        public static final String uid = "uid";
        public static final String email = "email";
        public static final String state = "state";
        public static final String profile = "profile";
        public static final String ACTION_NEW_RIDE = "com.avi_ud.gettaxi2.NewRide";
        public static final String BrodcastExtraKey= "BroadcastRideData";
        public static String GROUP_KEY_Rides = "com.avi_ud.gettaxi2.NewRide_group";
        public static final String firstRun = "first_run";
    }
}
