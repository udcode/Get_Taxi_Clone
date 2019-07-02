package com.avi_ud.gettaxi1.utils;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

public class AsyncGeocoder extends AsyncTask<Object, Void, Address> {

    private OnAddressFound mCallback;
    private Geocoder mGeocoder;

    public enum From {STRING, LOCATION}

    public interface OnAddressFound {
        void onAddressFound(Address Address);

        From getFromType();
    }

    public AsyncGeocoder(Geocoder geocoder, OnAddressFound callback) {
        mCallback = callback;
        mGeocoder = geocoder;
    }

    @Override
    protected Address doInBackground(Object... locations) {
        List<Address> addressList = null;
        From fromType = mCallback.getFromType();

        try {
            if (fromType == From.LOCATION) {
                Location location = (Location) locations[0];
                addressList = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } else
                addressList = mGeocoder.getFromLocationName((String) locations[0], 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null && addressList.size() > 0)
            return addressList.get(0);
        return null;
    }

    @Override
    protected void onPostExecute(Address address) {
        mCallback.onAddressFound(address);
    }
}

