package com.example.caspe.kilonotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.model.Ride;

import java.util.ArrayList;

/**
 * Created by caspe on 24-12-2017.
 */

public class RidesAdapter extends ArrayAdapter<Ride> {
    public RidesAdapter(Context context, ArrayList<Ride> rides) {
        super(context, 0, rides);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Ride ride = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView date = (TextView) convertView.findViewById(R.id.list_ride_date);
        TextView startDist = (TextView) convertView.findViewById(R.id.list_start_dist);
        TextView endDist = (TextView) convertView.findViewById(R.id.list_end_dist);
        TextView rideUser = (TextView) convertView.findViewById(R.id.list_user);
        TextView totalDist = (TextView) convertView.findViewById(R.id.list_total_dist);

        // Populate the data into the template view using the data object
        date.setText(ride.date);
        startDist.setText(Long.toString(ride.startDistance));
        endDist.setText(Long.toString(ride.endDistance));
        rideUser.setText(ride.userName);
        totalDist.setText(Long.toString(ride.endDistance - ride.startDistance));

        // Return the completed view to render on screen
        return convertView;
    }

}
