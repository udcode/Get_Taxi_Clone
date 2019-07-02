package com.avi_ud.gettaxi2.controller.activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avi_ud.gettaxi2.NewRideBroadcastReceiver;
import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.controller.fragments.DriverRides;
import com.avi_ud.gettaxi2.controller.fragments.OpenRides;
import com.avi_ud.gettaxi2.controller.fragments.Profile;
import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.utils.Connectivity;
import com.avi_ud.gettaxi2.utils.Globals;

import java.util.Objects;

//the main activity
public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback, Profile.UpdateDriverListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    NewRideBroadcastReceiver broadcastReceiver;
    ImageView avatar;
    TextView navTitle;
    private boolean mPermissionGranted = false;
    FragmentType type = FragmentType.OPEN_RIDES;

    public enum FragmentType {
        OPEN_RIDES, PROFILE, DRIVER_RIDES
    }

    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View headerView = navigationView.getHeaderView(0);
            navTitle = (TextView) headerView.findViewById(R.id.nav_title);
            TextView navSubTitle = (TextView) headerView.findViewById(R.id.nav_subtitle);
            avatar = (ImageView) headerView.findViewById(R.id.imageView);

            onUpdateDriver();
            navSubTitle.setText(Globals.driver.getEmail());
            getLocationPermission();
            if (mPermissionGranted)
                updateLocation();
            navigationView.getMenu().getItem(0).setChecked(true);
            showFragment(FragmentType.OPEN_RIDES);
            //check the network status
            Connectivity.checkNetwork(new Connectivity.Handler() {
                @Override
                public void handleStatus(boolean status) {
                    if (!status) {
                        Snackbar.make(fragment.getView(), "There is no internet connectivity..", Snackbar.LENGTH_INDEFINITE)
                                .setAction("CLOSE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(Color.RED)
                                .show();
                    }
                }
            });

            String startActivityWithFragment = getIntent().getStringExtra(FragmentType.OPEN_RIDES.toString());
            if (startActivityWithFragment != null)
                showFragment(FragmentType.OPEN_RIDES);


            } catch(Exception e){
                e.printStackTrace();
            }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_open_rides) {
            showFragment(FragmentType.OPEN_RIDES);
        } else if (id == R.id.nav_rides_history) {
            showFragment(FragmentType.DRIVER_RIDES);
        } else if (id == R.id.nav_profile) {
            openProfile();
        } else if (id == R.id.nav_share) {
            share();
        } else if (id == R.id.nav_exit) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openProfile() {
        showFragment(FragmentType.PROFILE);
    }

    private void logout() {
        BackEndFactory.backEnd.signOut();
        Intent loginIntent = new Intent(Main.this, Login.class);
        startActivity(loginIntent);
        finish();
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "GetTaxi");
        intent.putExtra(Intent.EXTRA_TEXT, "Send from my GetTaxi2");
        startActivity(Intent.createChooser(intent, "Choose One"));
    }

    public void showFragment(FragmentType type) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (type) {
            case OPEN_RIDES:
                fragment = new OpenRides();
                break;
            case PROFILE:
                fragment = new Profile();
                Bundle bundle = new Bundle();
                bundle.putString(Globals.Strings.state, Globals.Strings.profile);
                fragment.setArguments(bundle);
                break;
            case DRIVER_RIDES:
                fragment = new DriverRides();
                break;
        }
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        this.type = type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true;
                    updateLocation();
                } else {
                    // permission denied, boo!
                }
                return;
            }
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this).getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void updateLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Connectivity.isGpsEnable(this))
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
            }
    }

    @Override
    public void onStart() {
        super.onStart();
        broadcastReceiver = new NewRideBroadcastReceiver();
        registerReceiver(broadcastReceiver,
                new IntentFilter(Globals.Strings.ACTION_NEW_RIDE));
        Bundle extr = getIntent().getExtras();
        String startActivityWithFragment = "";
        if (extr != null)
            startActivityWithFragment = extr.getString(FragmentType.OPEN_RIDES.toString());
        if (startActivityWithFragment.equals(FragmentType.OPEN_RIDES.toString())) {
            showFragment(FragmentType.OPEN_RIDES);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Globals.driverLocation = location;
        if (type == FragmentType.OPEN_RIDES)
            ((OpenRides) fragment).refresh();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onUpdateDriver() {
        if (Globals.driver.getImageLocalUri() != null)
            avatar.setImageURI(Globals.driver.getImageLocalUri());
        String title = Globals.driver.getFirstName() + " " + Globals.driver.getLastName();
        navTitle.setText(title);
    }
}
