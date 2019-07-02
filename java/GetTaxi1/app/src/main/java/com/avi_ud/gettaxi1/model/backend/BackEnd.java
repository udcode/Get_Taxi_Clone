package com.avi_ud.gettaxi1.model.backend;

import com.avi_ud.gettaxi1.model.entities.Passenger;
import com.avi_ud.gettaxi1.model.entities.Ride;

import java.util.ArrayList;

public interface BackEnd {
    void addRide(Ride ride, final IAction<Void> action);
    void isExists(final Ride ride, final IAction<Boolean> action);
    void addPassenger(Passenger passenger,final IAction<Void> action);
    void isPassengerExists(final Passenger passenger, final IAction<Boolean> action);
    void getRidesOfPassenger(Passenger p, final IAction<ArrayList<Ride>> action);
    Object getRidesReference();
}
