package com.avi_ud.gettaxi2.model.backend;

import com.avi_ud.gettaxi2.model.entities.Driver;
import com.avi_ud.gettaxi2.model.entities.Ride;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public interface BackEnd {
    //user actions
    void signInUser(String email, String password, final IAction<FirebaseUser> action);
    void createUser(String email, String password, final IAction<FirebaseUser> action);
    void signOut();
    void SignInWithGoogle(GoogleSignInAccount acct, IAction<FirebaseUser> action);
    void resetPassword(String email, final IAction<Void> action);
    FirebaseUser getCurrentUser();

    //driver actions
    void addDriver(Driver driver, final IAction<Void> action);
    void getDriver(String uid, final IAction<Driver> action);

    //ride actions
    void openRides(IAction<ArrayList<Ride>> action);
    void driverRides(final String driverUid, final IAction<ArrayList<Ride>> action);
    void takeRide(String key, final IAction<Boolean> action);
    void finishRide(String key, final IAction<Boolean> action);
    void cancelRide(String key, final IAction<Boolean> action);

    //general actions
    void isBackEndAvailable(final IAction<Boolean> action);
}
