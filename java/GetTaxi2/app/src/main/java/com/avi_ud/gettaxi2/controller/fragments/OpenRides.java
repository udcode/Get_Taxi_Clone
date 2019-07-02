package com.avi_ud.gettaxi2.controller.fragments;

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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.controller.adapters.OpenRideAdapter;
import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.model.backend.IAction;
import com.avi_ud.gettaxi2.model.entities.Ride;
import com.avi_ud.gettaxi2.utils.Alerter;

import java.util.ArrayList;

public class OpenRides extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView emptyList;
    private Switch aSwitch;

    protected ArrayList<Ride> rides = new ArrayList<>();
    BottomSheetDialog mBottomSheetDialog;
    Button filterButton;
    EditText distanceFilterTxt;
    CheckBox shortCheckBox;
    CheckBox longCheckBox;
    String filter = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.open_rides, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView = (RecyclerView) getView().findViewById(R.id.list_rides);
        emptyList = (TextView) getView().findViewById(R.id.empty_state);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new OpenRideAdapter(getActivity(), rides);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        //make empty state
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                ((OpenRideAdapter) mAdapter).getFilter().filter(filter);
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

        //get the open rides
        BackEndFactory.backEnd.openRides(new IAction<ArrayList<Ride>>() {
            @Override
            public void onSuccess(ArrayList<Ride> obj) {
                rides.clear();
                rides.addAll(obj);
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

        //pop the filter dialog
        if (id == R.id.button_filter) {
            mBottomSheetDialog = new BottomSheetDialog(getActivity());
            View sheetView = getActivity().getLayoutInflater().inflate(R.layout.filter_open_rides_dialog, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();
            distanceFilterTxt = (EditText) mBottomSheetDialog.findViewById(R.id.distance_filter);
            shortCheckBox = (CheckBox) mBottomSheetDialog.findViewById(R.id.short_checkBox);
            longCheckBox = (CheckBox) mBottomSheetDialog.findViewById(R.id.long_checkBox);
            aSwitch = (Switch) mBottomSheetDialog.findViewById(R.id.filter_switch);
            filterButton = (Button) mBottomSheetDialog.findViewById(R.id.filter_button);

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        distanceFilterTxt.setEnabled(false);
                        filterButton.setEnabled(false);
                        shortCheckBox.setEnabled(false);
                        longCheckBox.setEnabled(false);
                        emptyFilter();

                    } else {
                        distanceFilterTxt.setEnabled(true);
                        filterButton.setEnabled(true);
                        shortCheckBox.setEnabled(true);
                        longCheckBox.setEnabled(true);
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

    public void filter() {
        StringBuilder sb = new StringBuilder();
        sb.append(distanceFilterTxt.getText().toString());
        if (longCheckBox.isChecked() && !shortCheckBox.isChecked())
            sb.append(";long");
        else if (!longCheckBox.isChecked() && shortCheckBox.isChecked())
            sb.append(";short");
        filter = sb.toString();
        ((OpenRideAdapter) mAdapter).getFilter().filter(filter);
    }

    public void emptyFilter() {
        ((OpenRideAdapter) mAdapter).getFilter().filter("");
        filter = "";
    }

    public void refresh() {
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
