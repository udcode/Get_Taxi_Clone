package com.avi_ud.gettaxi2.controller.adapters;

import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.controller.dialogs.TakenRideDialog;
import com.avi_ud.gettaxi2.model.entities.Ride;
import com.avi_ud.gettaxi2.utils.Globals;

import java.util.ArrayList;

public class OpenRideAdapter extends RecyclerView.Adapter<OpenRideAdapter.OpenRidesViewHolder> implements Filterable {
    private ArrayList<Ride> mOriginalData;
    private ArrayList<Ride> mData;
    private Activity activity;

    public static class OpenRidesViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView source;
        TextView target;
        TextView rideDistance;
        TextView rideLength;
        TextView dateTime;

        View mView;

        public OpenRidesViewHolder(View v) {
            super(v);
            mView = v;
            name = (TextView) v.findViewById(R.id.name);
            source = (TextView) v.findViewById(R.id.source);
            target = (TextView) v.findViewById(R.id.target);
            rideDistance = (TextView) v.findViewById(R.id.ride_distance);
            rideLength = (TextView) v.findViewById((R.id.ride_length));
            dateTime = (TextView) v.findViewById((R.id.datetime));
        }
    }

    public OpenRideAdapter(@NonNull Activity activity, @NonNull ArrayList<Ride> objects) {
        mData = objects;
        mOriginalData = objects;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            //filter by the string formatted
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                final ArrayList<Ride> list = mOriginalData;
                final ArrayList<Ride> tempList = new ArrayList<>();
                if (constraint != null && constraint.length() > 0) {
                    boolean shortRide = true;
                    boolean longRide = true;
                    float dis = Float.MAX_VALUE;
                    String[] s = constraint.toString().split(";");
                    for (int i = 0; i < s.length; i++) {
                        if (i == 0 && s[0].length() > 0)
                            dis = Float.parseFloat(s[0]);
                        else if (i == 1)
                            if (s[1].equals("short"))
                                longRide = false;
                            else if (s[1].equals("long"))
                                shortRide = false;
                    }
                    int count = list.size();
                    for (int i = 0; i < count; i++) {

                        Ride filterableRide = list.get(i);
                        if (Globals.driverLocation != null) {
                            Location a = new Location("A");
                            a.setLongitude(filterableRide.getStartLocation().getLng());
                            a.setLatitude(filterableRide.getStartLocation().getLat());

                            Location b = new Location("B");
                            b.setLongitude(filterableRide.getEndLocation().getLng());
                            b.setLatitude(filterableRide.getEndLocation().getLat());

                            float distTotal = a.distanceTo(b) / 1000;
                            float disto = a.distanceTo(Globals.driverLocation) / 1000;

                            if (disto >= dis)
                                continue;
                            if(!longRide && distTotal > 20)
                                continue;
                            if(!shortRide && distTotal <= 20)
                                continue;

                            tempList.add(filterableRide);

                        } else {
                            tempList.add(filterableRide);
                        }
                    }
                } else {
                    tempList.addAll(list);
                }

                results.values = tempList;
                results.count = tempList.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mData = (ArrayList<Ride>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public OpenRideAdapter.OpenRidesViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_ride, parent, false);

        OpenRidesViewHolder vh = new OpenRidesViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull OpenRidesViewHolder holder, int i) {
        final Ride ride = mData.get(i);
        holder.name.setText(ride.getPassenger().getFirstName());
        holder.source.setText(ride.getStartLocation().getAddress());
        holder.target.setText(ride.getEndLocation().getAddress());
        holder.dateTime.setText(ride.getTimeStart());
        Location source = new Location("A");
        source.setLongitude(ride.getStartLocation().getLng());
        source.setLatitude(ride.getStartLocation().getLat());

        Location target = new Location("B");
        target.setLongitude(ride.getEndLocation().getLng());
        target.setLatitude(ride.getEndLocation().getLat());
        final float rideLength = source.distanceTo(target) / 1000;
        holder.rideLength.setText(String.format("%.1f", rideLength) + " km");

        if (Globals.driverLocation != null) {
            float dis = source.distanceTo(Globals.driverLocation) / 1000;
            holder.rideDistance.setText(String.format("%.1f", dis) + " KM from you");
        } else {
            holder.rideDistance.setText("?? KM from you");
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakenRideDialog dialog = TakenRideDialog.newInstance(ride, rideLength);
                dialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "");
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
