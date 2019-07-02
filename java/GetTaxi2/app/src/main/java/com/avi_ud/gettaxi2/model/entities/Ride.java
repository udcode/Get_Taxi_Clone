package com.avi_ud.gettaxi2.model.entities;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

//This is the class for a driver_ride
public class Ride implements Serializable {
    private RideStatus status = RideStatus.OPEN;
    private Location startLocation;
    private Location endLocation;
    private String dateTime;
    private Passenger passenger;
    private Driver driver;
    private String comments;

    //constructor
    public Ride(Location startLocation, Location endLocation, String dateTime, Passenger passenger, String comments) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.dateTime = dateTime;
        this.passenger = passenger;
        this.comments = comments;
    }
    public  Ride(){}
    //Getters and Setters
    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getTimeStart() {
        return dateTime;
    }

    public void setTimeStart(String dateTime) {
        this.dateTime = dateTime;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(@Nullable Driver driver) {
        this.driver = driver;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Exclude
    public String getKey(){
        return passenger.getKey() +
                dateTime.replace("/","_")
                        .replace(" ","_");
    }
}
