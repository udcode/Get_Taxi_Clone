package com.avi_ud.gettaxi1.controller;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avi_ud.gettaxi1.R;
import com.avi_ud.gettaxi1.model.entities.Ride;


import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
    private List<Ride> dataSet;
    Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item,viewGroup,false);
        return new ViewHolder(view,mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Ride order = dataSet.get(i);
        String driverName = order.getDriver() != null ? order.getDriver().getFirstName()+ " - "+order.getDriver().getLastName():"";
        viewHolder.txtName.setText(driverName);
        viewHolder.txtSrc.setText(order.getStartLocation().getAddress());
        viewHolder.txtDest.setText(order.getEndLocation().getAddress());
        viewHolder.txtDate.setText(order.getTimeStart());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    // View lookup cache
    public  class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtSrc;
        TextView txtDest;
        TextView txtDate;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            mContext = ctx;
            txtName = (TextView) itemView.findViewById(R.id.driverNameTxt);
            txtSrc = (TextView) itemView.findViewById(R.id.fromTxt);
            txtDest = (TextView) itemView.findViewById(R.id.toTxt);
            txtDate = (TextView) itemView.findViewById(R.id.dateTxt);
        }
    }

    public CustomAdapter(List<Ride> data, Context context) {
        this.dataSet = data;
        this.mContext=context;

    }
}

