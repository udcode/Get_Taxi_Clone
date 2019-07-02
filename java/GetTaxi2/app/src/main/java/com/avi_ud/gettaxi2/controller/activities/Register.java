package com.avi_ud.gettaxi2.controller.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.controller.fragments.Profile;
import com.avi_ud.gettaxi2.utils.Globals;
//Register activity
public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uid = extras.getString(Globals.Strings.uid);
            String email = extras.getString(Globals.Strings.email);

            //show the profile fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new Profile();
            Bundle bundle = new Bundle();
            bundle.putString(Globals.Strings.uid, uid);
            bundle.putString(Globals.Strings.email, email);
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
        }
    }
}
