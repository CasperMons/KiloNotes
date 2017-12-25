package com.example.caspe.kilonotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.model.Ride;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by caspe on 24-12-2017.
 */

public class RidesAdapter extends ArrayAdapter<Ride> {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm");
    private static DecimalFormat doubleFormat = new DecimalFormat("##00.00");
    private static double fee = 0.15;

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
        TextView ridePrice = (TextView) convertView.findViewById(R.id.list_price);

        // Populate the data into the template view using the data object
        date.setText(dateFormat.format(ride.date));
        startDist.setText(Long.toString(ride.startDistance) + "");
        endDist.setText(Long.toString(ride.endDistance) + " Km");
        rideUser.setText(ride.userName);
        totalDist.setText(Long.toString(ride.endDistance - ride.startDistance) + " Km");
        ridePrice.setText("€" + doubleFormat.format(calcPriceOfRide(ride)));

        // Return the completed view to render on screen
        return convertView;
    }

    public double calcPriceOfRide(Ride ride) {
        double price = 0.00;
        price = (ride.endDistance - ride.startDistance) * fee;
        return price;
    }

}
