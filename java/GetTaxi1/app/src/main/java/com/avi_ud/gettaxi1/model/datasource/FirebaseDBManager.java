package com.avi_ud.gettaxi1.model.datasource;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.avi_ud.gettaxi1.model.entities.RideStatus;
import com.google.gson.Gson;

import com.avi_ud.gettaxi1.model.backend.BackEnd;
import com.avi_ud.gettaxi1.model.backend.IAction;
import com.avi_ud.gettaxi1.model.entities.Passenger;
import com.avi_ud.gettaxi1.model.entities.Ride;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class FirebaseDBManager implements BackEnd {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference rides = db.getReference().child("Rides");
    DatabaseReference passengers = db.getReference("Passengers");
    ArrayList<Ride> rid = new ArrayList<>();

    @Override
    public  Object getRidesReference(){
        return rides;
    }

    @Override
    public void addRide(Ride ride, final IAction<Void> action) {
        rides.child(String.valueOf(ride.getKey())).setValue(ride)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        action.onSuccess(aVoid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        action.onFailure(e);
                    }
                });
    }



    @Override
    public void isExists(final Ride ride, final IAction<Boolean> action) {
        rides.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                action.onSuccess(snapshot.hasChild(String.valueOf(ride.getKey())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
            }
        });
    }

    public void getRidesOfPassenger(final Passenger p, final IAction<ArrayList<Ride>> action){
            rid = new ArrayList<>();
            Query query = rides.orderByChild("passenger/id").equalTo(p.getId());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String val = snapshot.child("status").getValue().toString();
                        if(val.equals(RideStatus.DONE.toString())) {
                            Object  ride = snapshot.getValue(Object.class);
                            String jsn = new Gson().toJson(ride);
                            Ride r = new Gson().fromJson(jsn,Ride.class);
                            r.setTimeStart(((HashMap<String,String>)ride).get("timeStart"));
                            if(!rid.contains(r))
                                rid.add(r);
                        }
                    }
                    action.onSuccess(rid);

                }
               @Override
               public void onCancelled(DatabaseError databaseError) {
                   System.out.println(databaseError.getMessage());
               }
    });

    }
    @Override
    public void addPassenger(Passenger passenger,final IAction<Void> action) {

        passengers.child(String.valueOf(passenger.getId())).setValue(passenger)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        action.onSuccess(aVoid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        action.onFailure(e);
                    }
                });
    }
    @Override
    public void isPassengerExists(final Passenger passenger, final IAction<Boolean> action) {
        rides.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                action.onSuccess(snapshot.hasChild(String.valueOf(passenger.getKey())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
            }
        });
    }
}
