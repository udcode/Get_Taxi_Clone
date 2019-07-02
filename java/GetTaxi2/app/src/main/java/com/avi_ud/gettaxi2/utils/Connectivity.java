package com.avi_ud.gettaxi2.utils;

import android.content.Context;
import android.location.LocationManager;

import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.model.backend.IAction;

public final class Connectivity {

    private Connectivity() {
    }

    public interface Handler {
        void handleStatus(boolean status);
    }

    public static void checkNetwork(final Handler handler) {
        BackEndFactory.backEnd.isBackEndAvailable(new IAction<Boolean>() {
            @Override
            public void onSuccess(Boolean obj) {
               handler.handleStatus(obj);
            }

            @Override
            public void onFailure(Exception exception) {

            }

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute() {

            }
        });
    }

    public static boolean isGpsEnable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
