package com.avi_ud.gettaxi2.controller.adapters;

import android.app.Activity;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.model.backend.IAction;
import com.avi_ud.gettaxi2.model.entities.Ride;
import com.avi_ud.gettaxi2.model.entities.RideStatus;
import com.avi_ud.gettaxi2.utils.Alerter;
import com.avi_ud.gettaxi2.utils.Communication;
import com.avi_ud.gettaxi2.utils.Globals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import static com.avi_ud.gettaxi2.utils.Globals.dateFormat;
import static com.avi_ud.gettaxi2.utils.Globals.timeFormat;

public class DriverRidesAdapter extends RecyclerView.Adapter<DriverRidesAdapter.RidesViewHolder> implements Filterable{
    private static final float RATE = 7.5f;
    private ArrayList<Ride> mOriginalData;
    private ArrayList<Ride> mDataset;
    private Activity mActivity;
    private boolean isExpended = false;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RidesViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView phone;
        public TextView source;
        public TextView target;
        public TextView datetime;
        public ImageButton closeBottom;
        public ImageButton navBottom;
        public ImageButton callBottom;
        public TextView rideLength;
        public TextView rideMoney;
        public MaterialCardView cardView;
        View mView;

        public RidesViewHolder(View v) {
            super(v);
            mView = v;
            name = (TextView) v.findViewById(R.id.name);
            phone = (TextView) v.findViewById(R.id.phone);
            source = (TextView) v.findViewById(R.id.from);
            target = (TextView) v.findViewById(R.id.to);
            datetime = (TextView) v.findViewById(R.id.datetime);
            closeBottom = (ImageButton) v.findViewById(R.id.close_button);
            navBottom = (ImageButton) v.findViewById(R.id.nav_button);
            callBottom = (ImageButton) v.findViewById(R.id.phone_button);
            rideLength = (TextView) v.findViewById(R.id.ride_length);
            rideMoney = (TextView) v.findViewById(R.id.money);
            cardView = (MaterialCardView) v.findViewById(R.id.card);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DriverRidesAdapter(ArrayList<Ride> myDataset, Activity activity) {
        mDataset = myDataset;
        mOriginalData = myDataset;
        mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public DriverRidesAdapter.RidesViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_ride, parent, false);
        RidesViewHolder vh = new RidesViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RidesViewHolder holder, int position) {
        final Ride ride = mDataset.get(position);
        if (ride.getStatus() == RideStatus.IN_PROGRESS) {
            holder.cardView.setBackgroundColor(holder.mView.getResources().getColor(R.color.GreenBackground));
            holder.rideLength.setBackgroundColor(holder.mView.getResources().getColor(R.color.GreenBackground));
        }else  if(ride.getStatus() == RideStatus.DONE) {
            holder.cardView.setBackgroundColor(holder.mView.getResources().getColor(R.color.OrangeBackground));
            holder.rideLength.setBackgroundColor(holder.mView.getResources().getColor(R.color.OrangeBackground));
        }
        holder.name.setText(String.format("%s %s", ride.getPassenger().getFirstName(),
                ride.getPassenger().getLastName()));
        holder.phone.setText(ride.getPassenger().getPhoneNumber());
        holder.source.setText(ride.getStartLocation().getAddress());
        holder.target.setText(ride.getEndLocation().getAddress());
        holder.datetime.setText(ride.getTimeStart());

        Location source = new Location("A");
        source.setLongitude(ride.getStartLocation().getLng());
        source.setLatitude(ride.getStartLocation().getLat());

        Location target = new Location("B");
        target.setLongitude(ride.getEndLocation().getLng());
        target.setLatitude(ride.getEndLocation().getLat());
        float dis = source.distanceTo(target) / 1000;
        holder.rideLength.setText(new StringBuilder()
                .append(String.format("%.1f", dis))
                .append(" KM").toString());
        holder.rideMoney.setText(new StringBuilder()
                .append(String.format("%.1f", dis * RATE))
                .append(" NIS").toString());
        holder.closeBottom.setTag(ride);
        holder.closeBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackEndFactory.backEnd.finishRide(((Ride) view.getTag()).getKey(),
                        new IAction<Boolean>() {
                            @Override
                            public void onSuccess(Boolean obj) {
                                holder.closeBottom.setVisibility(View.GONE);
                                holder.navBottom.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Alerter.showErrorDialog(holder.mView.getContext(),exception.getMessage());
                            }

                            @Override
                            public void onPreExecute() {

                            }

                            @Override
                            public void onPostExecute() {

                            }
                        });
            }
        });

        holder.callBottom.setTag(ride);
        holder.callBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Communication.call(((Ride) view.getTag()).getPassenger().getPhoneNumber(), mActivity);
            }
        });

        holder.navBottom.setTag(ride);
        holder.navBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Communication.openAddressOnMap(((Ride) view.getTag()).getStartLocation().getAddress(), mActivity);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpended) {
                    holder.closeBottom.setVisibility(View.GONE);
                    holder.navBottom.setVisibility(View.GONE);
                    holder.callBottom.setVisibility(View.GONE);
                    isExpended = false;
                } else {
                    if(!(ride.getStatus() == RideStatus.DONE)){
                        holder.closeBottom.setVisibility(View.VISIBLE);
                        holder.navBottom.setVisibility(View.VISIBLE);
                    }
                    holder.callBottom.setVisibility(View.VISIBLE);
                    isExpended = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                final ArrayList<Ride> list = mOriginalData;
                final ArrayList<Ride> tempList = new ArrayList<>();
                if (constraint != null && constraint.length() > 0) {
                    boolean ongoingRide = false;
                    boolean doneRide = false;
                    float dis = Float.MAX_VALUE;
                    Date fromDate = null;
                    String[] s = constraint.toString().split(";");
                    for (int i = 0; i < s.length; i++) {
                        if (i == 0 && s[0].length() > 0)
                            dis = Float.parseFloat(s[0]);
                        else if (i == 1) {
                            if (s[1].equals("ongoing"))
                                ongoingRide = true;
                            else if (s[1].equals("done"))
                                doneRide = true;
                        }
                        else if (i == 2) {
                            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                            try {
                                fromDate = sdf.parse(s[2]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    int count = list.size();
                    for (int i = 0; i < count; i++) {

                        Ride filterableRide = list.get(i);
                        Location a = new Location("A");
                        a.setLongitude(filterableRide.getStartLocation().getLng());
                        a.setLatitude(filterableRide.getStartLocation().getLat());

                        Location b = new Location("B");
                        b.setLongitude(filterableRide.getEndLocation().getLng());
                        b.setLatitude(filterableRide.getEndLocation().getLat());
                        float distTotal = a.distanceTo(b) / 1000;

                        if(ongoingRide && (filterableRide.getStatus() == RideStatus.DONE))
                            continue;
                        if(doneRide && (filterableRide.getStatus() == RideStatus.IN_PROGRESS))
                            continue;
                        if(fromDate != null) {
                            DateFormat f = new SimpleDateFormat(dateFormat + " " + timeFormat);
                            try {
                                Date d = f.parse(filterableRide.getTimeStart());
                                if( d.compareTo(fromDate) <= 0)
                                    continue;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        if (distTotal < dis){
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
                mDataset = (ArrayList<Ride>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
