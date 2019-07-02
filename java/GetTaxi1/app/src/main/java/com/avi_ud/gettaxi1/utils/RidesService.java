package com.avi_ud.gettaxi1.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.avi_ud.gettaxi1.model.entities.Ride;

public class RidesService extends Service {
    public RidesService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(5000);
                        synchronized (Global.locker) {
                            if (Global.orderSubmited) {
                                Ride ride;
                                while ((ride = Global.ridesQueue.poll())!=null){
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction(Global.ACTION_NEW_RIDE);
                                    CharSequence ridePrevDetails = (ride.getPassenger().getFirstName() + " From: " + ride.getStartLocation().getAddress());
                                    broadcastIntent.putExtra(Global.BrodcastExtraKey, ridePrevDetails);
                                    sendBroadcast(broadcastIntent);
                                }
                                Global.orderSubmited = false;

                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
