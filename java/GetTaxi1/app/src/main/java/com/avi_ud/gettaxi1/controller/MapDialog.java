package com.avi_ud.gettaxi1.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avi_ud.gettaxi1.R;
import com.avi_ud.gettaxi1.utils.AsyncGeocoder;
import com.avi_ud.gettaxi1.utils.Connectivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;
import java.util.Objects;

public class MapDialog extends DialogFragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionGranted = false;
    private OnMapDialogSubmit callback;
    private EditText locationSearch;
    private TextView locationTxt;
    private LatLng mLatLng;
    private String mLocation;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private boolean startMoveCamera = false;
    private float zoomLevel = 18f;
    private MapView mapView;
    private SharedPreferences prefs;
    private CameraPosition savedCmeraPosition;

    //callback for getting the info from the Dialog
    public interface OnMapDialogSubmit {
        void onMapDialogSubmit(String location, LatLng latLng);
    }

    public static MapDialog newInstance(String location, OnMapDialogSubmit callback) {
        MapDialog frag = new MapDialog();
        frag.callback = callback;
        frag.mLocation = location;
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_maps, null);
        configureDialogView(view);

        dialog.setContentView(view);

        MapView mMapView = (MapView) dialog.findViewById(R.id.map);
        MapsInitializer.initialize(getActivity());
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();
        mMapView.getMapAsync(this);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Slide);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CameraPosition mMyCam = mMap.getCameraPosition();
        double longitude = mMyCam.target.longitude;
        double latitude = mMyCam.target.latitude;

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putFloat("longitude", (float) longitude).apply();
        prefs.edit().putFloat("latitude", (float) latitude).apply();

    }

    private void configureDialogView(View v) {
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        locationSearch = (EditText) v.findViewById(R.id.searchLocation);
        locationTxt = (TextView) v.findViewById(R.id.location);
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.setVisibility(View.INVISIBLE);

        final Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.map_menu);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(mLocation != null && mLatLng != null) {
                    callback.onMapDialogSubmit(mLocation, mLatLng);
                    dismiss();
                }
                else{
                    Toast.makeText(getContext(),"waiting for location",Toast.LENGTH_LONG);

                }
                return false;
            }
        });

        locationSearch.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (locationSearch.getRight() - locationSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        findAddress(locationSearch.getText().toString());

                        return true;
                    }
                }
                return false;
            }
        });

        locationSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    findAddress(textView.getText().toString());
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        enableMyLocation();

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        if (mLocation == null && !Connectivity.isGpsEnable(getContext())) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            float longitude = prefs.getFloat("longitude", 0);
            float latitude = prefs.getFloat("latitude", 0);
            if (longitude != 0 && latitude != 0) {
                changeLocation(new LatLng(latitude, longitude));
            }
        }else{

            if (mLocation == null)
                getDeviceLocation();
            else
                findAddress(mLocation);
        }

        mapView.setVisibility(View.VISIBLE);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                getDeviceLocation();
                return true;
            }
        });
        mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                changeLocation(location);
            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (i == REASON_GESTURE) {
                    startMoveCamera = true;
                    locationTxt.setText(R.string.searching);
                    locationTxt.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim));
                }
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (startMoveCamera) {
                    zoomLevel = mMap.getCameraPosition().zoom;
                    changeLocation(mMap.getCameraPosition().target);
                    startMoveCamera = false;
                }
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void enableMyLocation() {
        if (mMap == null) {
            return;
        }
        try {
            if (mPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    public void onPermissionsGranted() {
        mPermissionGranted = true;
        enableMyLocation();
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (mPermissionGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            changeLocation(currentLocation);
                        } else {
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }

    private void findAddress(String location) {
        (new AsyncGeocoder(geocoder,
                new AsyncGeocoder.OnAddressFound() {
                    @Override
                    public void onAddressFound(Address address) {
                        if (address != null) {
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                            mLatLng = latLng;
                            mLocation = addressToString(address);
                            locationTxt.setText(mLocation);
                            locationTxt.clearAnimation();
                        }
                    }

                    @Override
                    public AsyncGeocoder.From getFromType() {
                        return AsyncGeocoder.From.STRING;
                    }
                })).execute(location);
    }

    private void changeLocation(LatLng newLatLng) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(newLatLng.latitude);
        location.setLongitude(newLatLng.longitude);
        changeLocation(location);
    }

    private void changeLocation(Location newLocation) {
        if (newLocation == null)
            return;
        mLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, zoomLevel));
        (new AsyncGeocoder(geocoder,
                new AsyncGeocoder.OnAddressFound() {
                    @Override
                    public void onAddressFound(Address address) {
                        if (address != null) {
                            mLocation = addressToString(address);
                            locationTxt.setText(mLocation);
                            locationTxt.clearAnimation();
                        }
                    }

                    @Override
                    public AsyncGeocoder.From getFromType() {
                        return AsyncGeocoder.From.LOCATION;
                    }
                })).execute(newLocation);
    }

    private String addressToString(Address address) {
        String str = "";
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            str += address.getAddressLine(i);
        }
        return str;
    }
}

