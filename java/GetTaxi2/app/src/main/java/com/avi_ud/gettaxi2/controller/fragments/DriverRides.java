package com.avi_ud.gettaxi2.controller.fragments;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.controller.adapters.DriverRidesAdapter;
import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.model.backend.IAction;
import com.avi_ud.gettaxi2.model.entities.Ride;
import com.avi_ud.gettaxi2.model.entities.RideStatus;
import com.avi_ud.gettaxi2.utils.Alerter;
import com.avi_ud.gettaxi2.utils.Globals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import static com.avi_ud.gettaxi2.utils.Globals.dateFormat;

public class DriverRides extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    protected ArrayList<Ride> rides = new ArrayList<>();
    private TextView emptyList;
    BottomSheetDialog mBottomSheetDialog;
    Button filterButton;
    EditText lengthFilterTxt;
    CheckBox progressCheckBox;
    CheckBox doneCheckBox;
    EditText dateFilterTxt;
    private final Calendar orderCalendar = Calendar.getInstance();

    private Switch aSwitch;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.driver_rides, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView = (RecyclerView) getView().findViewById(R.id.driver_rides_recycler);
        emptyList = (TextView) getView().findViewById(R.id.empty);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DriverRidesAdapter(rides,getActivity());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            void checkEmpty() {
                emptyList.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });

        BackEndFactory.backEnd.driverRides(Globals.driver.getUid() ,new IAction<ArrayList<Ride>>() {
            @Override
            public void onSuccess(ArrayList<Ride> obj) {
                rides.clear();
                rides.addAll(obj);
                //sort the list, the open rides on top
                Collections.sort(rides, new Comparator<Ride>() {
                    @Override
                    public int compare(Ride lhs, Ride rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        if(lhs.getStatus() == rhs.getStatus())
                            return 0;
                        else if(lhs.getStatus() == RideStatus.IN_PROGRESS)
                            return -1;
                        return 1;
                    }
                });
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {
                Alerter.showErrorDialog(getActivity(), exception.getMessage());
            }

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute() {

            }
        });

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.button_filter) {
            mBottomSheetDialog = new BottomSheetDialog(getActivity());
            View sheetView = getActivity().getLayoutInflater().inflate(R.layout.filter_driver_rides_dialog, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();
            lengthFilterTxt = (EditText) mBottomSheetDialog.findViewById(R.id.length_filter);
            progressCheckBox = (CheckBox)mBottomSheetDialog.findViewById(R.id.inprogress_checkBox);
            doneCheckBox = (CheckBox)mBottomSheetDialog.findViewById(R.id.done_checkBox);
            dateFilterTxt = (EditText) mBottomSheetDialog.findViewById(R.id.rideDate);
            aSwitch = (Switch)mBottomSheetDialog.findViewById(R.id.filter_switch);
            filterButton = (Button) mBottomSheetDialog.findViewById(R.id.filter_button);
            dateFilterTxt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getContext(), date, orderCalendar
                            .get(Calendar.YEAR), orderCalendar.get(Calendar.MONTH),
                            orderCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        lengthFilterTxt.setEnabled(false);
                        filterButton.setEnabled(false);
                        progressCheckBox.setEnabled(false);
                        doneCheckBox.setEnabled(false);
                        dateFilterTxt.setEnabled(false);
                        emptyFilter();

                    }else{
                        lengthFilterTxt.setEnabled(true);
                        filterButton.setEnabled(true);
                        progressCheckBox.setEnabled(true);
                        doneCheckBox.setEnabled(true);
                        dateFilterTxt.setEnabled(true);
                        filter();
                    }
                }
            });
            filterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filter();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void filter(){
        StringBuilder sb = new StringBuilder();
        sb.append(lengthFilterTxt.getText().toString());
        if(progressCheckBox.isChecked() && !doneCheckBox.isChecked())
            sb.append(";ongoing");
        else if(!progressCheckBox.isChecked() && doneCheckBox.isChecked())
            sb.append(";done");
        else
            sb.append(";dummy");
        if(dateFilterTxt.getText() != null)
            sb.append(";" + dateFilterTxt.getText().toString());
        ((DriverRidesAdapter)mAdapter).getFilter().filter(sb.toString());

    }

    public void emptyFilter(){
        ((DriverRidesAdapter)mAdapter).getFilter().filter("");
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        dateFilterTxt.setText(sdf.format(orderCalendar.getTime()));
    }
}
