package com.avi_ud.gettaxi2.model.entities;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

//This is the Driver class
public class Driver extends Person {
    private String carType;
    private String uid;
    private Uri imageLocalUri = null;

    //constructor
    public Driver(){}

    public Driver(String lastName, String firstName, String id, String phoneNumber, String email, String carType) {
        super(lastName, firstName, id, phoneNumber, email);
        this.carType = carType;
    }
    public Driver(String lastName, String firstName, String id, String phoneNumber, String email, String carType,@Nullable Uri imageLocalUri) {
        super(lastName, firstName, id, phoneNumber, email);
        this.carType = carType;
        this.imageLocalUri = imageLocalUri;
    }
    //Getters and Setters
    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    @Override
    public String getKey(){
        return uid;
    }

    @Exclude
    public Uri getImageLocalUri() {
        return imageLocalUri;
    }

    @Exclude
    public void setImageLocalUri(Uri imageLocalUri) {
        this.imageLocalUri = imageLocalUri;
    }
}
