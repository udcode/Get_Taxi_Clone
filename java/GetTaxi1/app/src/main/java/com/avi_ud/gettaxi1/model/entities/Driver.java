package com.avi_ud.gettaxi1.model.entities;

import com.google.firebase.database.Exclude;

//This is the Driver class
public class Driver extends Person {
    private String carType;
    private int rate;
    private String uid;

    //constructor
    public Driver(){}

    public Driver(String lastName, String firstName, String id, String phoneNUmber, String email, String carType, int rate) {
        super(lastName, firstName, id, phoneNUmber, email);
        this.carType = carType;
        this.rate = rate;
    }
    public Driver(String lastName, String firstName, String id, String phoneNUmber, String email, String carType) {
        super(lastName, firstName, id, phoneNUmber, email);
        this.carType = carType;
        this.rate = 0;
    }
    //Getters and Setters
    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Exclude
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
}
