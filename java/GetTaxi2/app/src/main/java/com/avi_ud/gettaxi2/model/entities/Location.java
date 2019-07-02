package com.avi_ud.gettaxi2.model.entities;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

//custom location class
public class Location {
    private double lat;
    private double lng;
    private String address;

    public Location(){}

    public Location(LatLng latLng, String address) {
        this.lat = latLng.latitude;
        this.lng = latLng.latitude;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Exclude
    public void setLatLng(LatLng latLng) {
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }

}
