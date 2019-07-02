package com.avi_ud.gettaxi1.utils;

import com.avi_ud.gettaxi1.model.entities.Ride;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Global {

    public static boolean orderSubmited;
    public static Object locker = new Object();
    public static Queue<Ride> ridesQueue= new LinkedBlockingQueue<Ride>();
    public static final String ACTION_NEW_RIDE = "com.avi_ud.gettaxi2.NewRide";
    public static final String BrodcastExtraKey= "BroadcastRideData";
}
