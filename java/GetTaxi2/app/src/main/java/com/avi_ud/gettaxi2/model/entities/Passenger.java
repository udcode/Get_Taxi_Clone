package com.avi_ud.gettaxi2.model.entities;

//This is the Passenger class
public class Passenger extends Person {

    public Passenger(){}
    //constructor
    public Passenger(String lastName, String firstName, String id, String phoneNumber, String email) {
        super(lastName, firstName, id, phoneNumber, email);
    }
}
