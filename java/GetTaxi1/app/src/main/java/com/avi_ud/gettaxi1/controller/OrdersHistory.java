package com.avi_ud.gettaxi1.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avi_ud.gettaxi1.R;
import com.avi_ud.gettaxi1.model.backend.BackEndFactory;
import com.avi_ud.gettaxi1.model.backend.IAction;
import com.avi_ud.gettaxi1.model.entities.Passenger;
import com.avi_ud.gettaxi1.model.entities.Ride;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrdersHistory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrdersHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersHistory extends Fragment {

    private Passenger passenger;
    private List<Ride> dataEntries = new ArrayList<>();
    private RecyclerView recyclerView;
    private static CustomAdapter adapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_orders_history,container,false);

        Bundle extras = getActivity().getIntent().getExtras();
        passenger = (Passenger)extras.getSerializable(Passenger.class.getName());
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CustomAdapter(dataEntries,getContext());
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void EnteringPage() {

            BackEndFactory.backEnd.getRidesOfPassenger(passenger,new IAction<ArrayList<Ride>>(){
                @Override
                public void onSuccess(ArrayList<Ride> list) {
                    dataEntries.clear();
                    dataEntries.addAll(list);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getContext(),"failed",Toast.LENGTH_LONG).show();
                }
            });


    }

//    @Override
//    public void onStart(){
//        super.onStart();
//        dataEntries = BackEndFactory.backEnd.getRidesOfPassenger(passenger,new IAction<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getContext(),"success",Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Exception exception) {
//                Toast.makeText(getContext(),"failed",Toast.LENGTH_LONG).show();
//            }
//        });
//
//        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(false);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        adapter = new CustomAdapter(dataEntries,getContext());
//        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//
//    }
//
    public void resetFragment() {
        dataEntries = new ArrayList<>();
        adapter = new CustomAdapter(dataEntries,getContext());
        recyclerView.setAdapter(adapter);
        passenger = null;
    }
}
