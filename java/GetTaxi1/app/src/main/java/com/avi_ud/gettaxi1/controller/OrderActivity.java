package com.avi_ud.gettaxi1.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.avi_ud.gettaxi1.R;
import com.avi_ud.gettaxi1.model.backend.BackEndFactory;
import com.avi_ud.gettaxi1.model.backend.IAction;
import com.avi_ud.gettaxi1.model.entities.Location;
import com.avi_ud.gettaxi1.model.entities.Passenger;
import com.avi_ud.gettaxi1.model.entities.Ride;
import com.avi_ud.gettaxi1.utils.Alerter;
import com.avi_ud.gettaxi1.utils.Connectivity;
import com.avi_ud.gettaxi1.utils.Global;
import com.avi_ud.gettaxi1.utils.RidesService;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import static android.support.v4.app.DialogFragment.STYLE_NO_INPUT;

public class OrderActivity extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final Calendar orderCalendar = Calendar.getInstance();
    private String dateFormat = "MM/dd/yy";
    private String timeFormat = "HH:mm";
    private LinkedList<Ride> rides;
    IntentFilter mIntentFilter;
    private EditText rideDateEditText;
    private EditText rideTimeEditText;
    private EditText startLocationEditText;
    private EditText endLocationEditText;
    private Button submitButton;
    private ProgressBottomDialog bottomSheetFragment;

    private Passenger passenger;
    private Location startLocation = new Location();
    private Location endLocation = new Location();

    private MapDialog mapDialog;

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            orderCalendar.set(Calendar.YEAR, year);
            orderCalendar.set(Calendar.MONTH, monthOfYear);
            orderCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };

    private TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            orderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            orderCalendar.set(Calendar.MINUTE, minute);
            updateTime();
        }
    };

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.activity_order,container,false);
        Bundle extras = getActivity().getIntent().getExtras();
        passenger = (Passenger)extras.getSerializable(Passenger.class.getName());
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Global.ACTION_NEW_RIDE);
        Intent serviceIntent = new Intent(getContext(), RidesService.class);
        this.getActivity().startService(serviceIntent);

        rideDateEditText= (EditText) view.findViewById(R.id.rideDate);
        rideDateEditText.setText("Today");
        rideDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dPicker  = new DatePickerDialog(getContext(), date, orderCalendar
                        .get(Calendar.YEAR), orderCalendar.get(Calendar.MONTH),
                        orderCalendar.get(Calendar.DAY_OF_MONTH));
                dPicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dPicker.show();
            }
        });
        rideTimeEditText= (EditText) view.findViewById(R.id.rideTime);
        rideTimeEditText.setText("Now");
        rideTimeEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog tPicker = new TimePickerDialog(getContext(), time, orderCalendar
                        .get(Calendar.HOUR_OF_DAY), orderCalendar.get(Calendar.MINUTE),
                        true);
                tPicker.show();
            }
        });

        startLocationEditText = (EditText)view.findViewById(R.id.startLocation);
        startLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                String location = startLocationEditText.getText().toString().isEmpty() ? null : startLocationEditText.getText().toString();
                mapDialog = MapDialog.newInstance(location, new MapDialog.OnMapDialogSubmit() {
                    @Override
                    public void onMapDialogSubmit(String location, LatLng latLng) {
                        startLocationEditText.setText(location);
                        startLocation.setLatLng(latLng);
                        startLocation.setAddress(location);
                    }
                });
                mapDialog.show(fm, "fragment_map");
            }
        });

        endLocationEditText = (EditText)view.findViewById(R.id.endLocation);
        endLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                String location = endLocationEditText.getText().toString().isEmpty() ? null : endLocationEditText.getText().toString();
                mapDialog = MapDialog.newInstance(location, new MapDialog.OnMapDialogSubmit() {
                    @Override
                    public void onMapDialogSubmit(String location, LatLng latLng) {
                        endLocationEditText.setText(location);
                        endLocation.setLatLng(latLng);
                        endLocation.setAddress(location);
                    }
                });
                mapDialog.show(fm, "fragment_map");
            }
        });

        submitButton = (Button)view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startLocationEditText.getText().toString().length() < 1)
                    return;
                if(endLocationEditText.getText().toString().length() < 1)
                    return;

                bottomSheetFragment = new ProgressBottomDialog();
                bottomSheetFragment.setStyle(STYLE_NO_INPUT,0);
                bottomSheetFragment.setCancelable(false);
                bottomSheetFragment.show(getFragmentManager(), "progress_bar");

                submitButton.setClickable(true);

                final Ride ride = makeRideDetails(view);
                BackEndFactory.backEnd.isExists(ride, new IAction<Boolean>() {
                    @Override
                    public void onSuccess(Boolean obj) {
                        if (obj) {
                            Alerter.showErrorDialog(getContext(),"This Ride already exists");
                            dismissProgressDialog();

                        }
                        else {
                            BackEndFactory.backEnd.addRide(ride, new IAction<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    synchronized (Global.locker) {
                                        Global.ridesQueue.add(ride);
                                        Global.orderSubmited = true;
                                    }
                                    bottomSheetFragment.sendOk();
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    Alerter.showErrorDialog(getContext(),exception.getMessage());
                                    dismissProgressDialog();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Alerter.showErrorDialog(getContext(),e.getMessage());
                        dismissProgressDialog();
                    }
                });
            }
        });

        checkNetwork(view);
        return view;
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        rideDateEditText.setText(sdf.format(orderCalendar.getTime()));
    }

    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        rideTimeEditText.setText(sdf.format(orderCalendar.getTime()));
    }

    private Ride makeRideDetails(View view){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat + " " + timeFormat , Locale.getDefault());
            String datetime;
            String date  = rideDateEditText.getText().toString();
            String time  = rideTimeEditText.getText().toString();
            if(date.equals("Today") && time.equals("Now"))
                datetime = sdf.format(new Date());
            else
                datetime = date + " " + time;

        String comments = (( EditText) view.findViewById(R.id.comments)).getText().toString();
        return new Ride(startLocation, endLocation,datetime,passenger,comments);
    }

    private void  dismissProgressDialog(){
        submitButton.setClickable(true);
        bottomSheetFragment.dismiss();
    }



    private void checkNetwork(View view){
        if (!Connectivity.isNetworkAvailable(this.getActivity())) {
            Snackbar.make(view.findViewById(R.id.mainLayout), "There is no internet connectivity..", Snackbar.LENGTH_INDEFINITE)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    mapDialog.onPermissionsGranted();
                } else {
                    // permission denied, boo!
                }
                return;
            }
        }
    }
}
