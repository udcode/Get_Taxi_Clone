package com.avi_ud.gettaxi2.controller.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.controller.fragments.Profile;
import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.model.backend.IAction;
import com.avi_ud.gettaxi2.model.entities.Driver;
import com.avi_ud.gettaxi2.utils.Alerter;
import com.avi_ud.gettaxi2.utils.Connectivity;
import com.avi_ud.gettaxi2.utils.Globals;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;

//This is the first activity
public class Splash extends AppCompatActivity {

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = prefs.getBoolean(Globals.Strings.firstRun, true);

        if (firstRun) {
            loadLoginActivity();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Globals.Strings.firstRun, false);
            editor.apply(); // Very important to save the preference

        }

        //check if the user still connect to the app
        FirebaseUser currentUser = BackEndFactory.backEnd.getCurrentUser();
        if (currentUser != null) {
            loadMainActivity(currentUser);
        }else {
            loadLoginActivity();
        }
    }

    private void loadMainActivity(@NotNull FirebaseUser usr) {
        final String uid = usr.getUid();
        final String email = usr.getEmail();
        //pull the driver data and load the main activity
        BackEndFactory.backEnd.getDriver(uid, new IAction<Driver>() {
            @Override
            public void onSuccess(Driver driver) {
                if(driver == null){ //some misconfiguration
                    Intent profileIntent = new Intent(Splash.this, Register.class);
                    profileIntent.putExtra(Globals.Strings.uid, uid);
                    profileIntent.putExtra(Globals.Strings.email, email);
                    startActivity(profileIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(getApplicationContext(), Main.class);
                    driver.setUid(uid);
                    Globals.driver = driver;
                    startActivity(mainIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Alerter.showErrorDialog(Splash.this, exception.getMessage());
            }

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute() {

            }
        });
    }

    private void loadLoginActivity(){
        Intent loginIntent = new Intent(Splash.this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}
