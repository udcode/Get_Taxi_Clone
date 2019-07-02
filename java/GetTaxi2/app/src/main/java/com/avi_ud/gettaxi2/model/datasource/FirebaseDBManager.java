package com.avi_ud.gettaxi2.model.datasource;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.avi_ud.gettaxi2.model.backend.BackEnd;
import com.avi_ud.gettaxi2.model.backend.IAction;
import com.avi_ud.gettaxi2.model.entities.Driver;
import com.avi_ud.gettaxi2.model.entities.Ride;
import com.avi_ud.gettaxi2.model.entities.RideStatus;
import com.avi_ud.gettaxi2.utils.Globals;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FirebaseDBManager implements BackEnd {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference mImages = FirebaseStorage.getInstance().getReference().child("images");
    DatabaseReference drivers = db.getReference("Drivers");
    DatabaseReference rides = db.getReference("Rides");

    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    public void addDriver(Driver driver, final IAction<Void> action) {
        action.onPreExecute();
        //take care of the image
        if (driver.getImageLocalUri() != null) {
            mImages.child(driver.getKey() + ".jpg").
                    putFile(driver.getImageLocalUri()).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
        drivers.child(String.valueOf(driver.getKey())).setValue(driver)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        action.onSuccess(aVoid);
                        action.onPostExecute();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        action.onFailure(e);
                        action.onPostExecute();
                    }
                });
    }

    public void isDriverExists(final Driver driver, final IAction<Boolean> action) {
        action.onPreExecute();
        drivers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                action.onSuccess(snapshot.hasChild(String.valueOf(driver.getKey())));
                action.onPostExecute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
                action.onPostExecute();
            }
        });
    }

    @Override
    public void signInUser(String email, String password, final IAction<FirebaseUser> action) {
        action.onPreExecute();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            action.onSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            action.onFailure(task.getException());
                        }
                        action.onPostExecute();
                    }
                });
    }

    @Override
    public void SignInWithGoogle(final GoogleSignInAccount acct, final IAction<FirebaseUser> action) {
        action.onPreExecute();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            action.onSuccess(user);
                        } else {
                            action.onFailure(task.getException());
                        }
                        action.onPostExecute();
                    }
                });

    }

    @Override
    public void createUser(String email, String password, final IAction<FirebaseUser> action) {
        action.onPreExecute();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            action.onSuccess(user);
                        } else {
                            action.onFailure(task.getException());
                        }
                        action.onPostExecute();
                    }
                });
    }

    @Override
    public void getDriver(final String uid, final IAction<Driver> action) {
        action.onPreExecute();
        drivers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Driver d = dataSnapshot.getValue(Driver.class);
                if(d == null) {
                    action.onSuccess(null);
                    action.onPostExecute();
                    return;
                }
                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    mImages.child(d.getUid()+ ".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Local temp file has been created
                            d.setImageLocalUri(Uri.parse(localFile.getAbsolutePath()));
                            action.onSuccess(d);
                            action.onPostExecute();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            action.onSuccess(d);
                            action.onPostExecute();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
                action.onPostExecute();
            }
        });
    }

    @Override
    public void signOut() {
        mAuth.signOut();
    }

    @Override
    public void resetPassword(String email, final IAction<Void> action) {
        action.onPreExecute();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            action.onSuccess(null);
                        } else {
                            action.onFailure(task.getException());
                        }
                        action.onPostExecute();
                    }
                });
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return user;
    }

    @Override
    public void isBackEndAvailable(final IAction<Boolean> action) {
        action.onPreExecute();
        DatabaseReference connectedRef = db.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    action.onSuccess(true);
                } else {
                    action.onSuccess(false);
                }
                action.onPostExecute();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                action.onFailure(error.toException());
            }
        });
    }

    @Override
    public void openRides(final IAction<ArrayList<Ride>> action) {
        action.onPreExecute();
        Query query = rides.orderByChild("status").equalTo(RideStatus.OPEN.toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Ride> results = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    results.add(item.getValue(Ride.class));
                }
                action.onSuccess(results);
                action.onPostExecute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
                action.onPostExecute();
            }
        });
    }

    @Override
    public void driverRides(final String driverUid, final IAction<ArrayList<Ride>> action) {
        action.onPreExecute();
        Query query = rides.orderByChild("driver/uid").equalTo(driverUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Ride> results = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    results.add(item.getValue(Ride.class));
                }
                action.onSuccess(results);
                action.onPostExecute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
                action.onPostExecute();
            }
        });
    }

    @Override
    public void takeRide(final String key, final IAction<Boolean> action) {
        action.onPreExecute();
        rides.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(key)) {
                    Ride ride = snapshot.child(key).getValue(Ride.class);
                    ride.setDriver(Globals.driver);
                    ride.setStatus(RideStatus.IN_PROGRESS);
                    rides.child(key).setValue(ride);
                    action.onSuccess(true);
                } else {
                    action.onFailure(new Exception("Cant take your ride"));
                }
                action.onPostExecute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
                action.onPostExecute();
            }
        });
    }

    @Override
    public void finishRide(final String key, final IAction<Boolean> action) {
        action.onPreExecute();
        rides.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(key)) {
                    Ride ride = snapshot.child(key).getValue(Ride.class);
                    ride.setStatus(RideStatus.DONE);
                    rides.child(key).setValue(ride);
                    action.onSuccess(true);
                } else {
                    action.onSuccess(false);
                }
                action.onPostExecute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
                action.onPostExecute();
            }
        });
    }

    @Override
    public void cancelRide(final String key, final IAction<Boolean> action) {
        action.onPreExecute();
        rides.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(key)) {
                    Ride ride = snapshot.child(key).getValue(Ride.class);
                    ride.setDriver(null);
                    ride.setStatus(RideStatus.OPEN);
                    rides.child(key).setValue(ride);
                    action.onSuccess(true);
                } else {
                    action.onSuccess(false);
                }
                action.onPostExecute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
                action.onPostExecute();
            }
        });
    }
}
