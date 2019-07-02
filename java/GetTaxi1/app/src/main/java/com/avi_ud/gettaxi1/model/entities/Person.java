package com.avi_ud.gettaxi1.model.entities;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

//This is an abstract class for the persons that we have
public abstract class Person implements Serializable {
    private String lastName;
    private String firstName;
    private String id;
    private String phoneNumber;
    private String email;

    //constructor
    public Person(){}

    public Person(String lastName, String firstName, String id, String phoneNumber, String email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }


    //Getters and Setters
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getKey(){
        return firstName + "_" + lastName + "_" + id;
    }
}
