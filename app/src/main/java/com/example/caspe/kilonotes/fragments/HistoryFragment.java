package com.example.caspe.kilonotes.fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.adapters.RidesAdapter;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryFragment extends Fragment {

    ListView historyList;
    ArrayList<Ride> lstRides;
    Button getHistoryBtn;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHistRides();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        declareLayoutElements(view);

        getHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "GETTING HISTORY", Toast.LENGTH_LONG).show();
                if (lstRides != null) {
                    ArrayList<Ride> rideArrayList = lstRides;
                    RidesAdapter ridesAdapter = new RidesAdapter(getContext(), rideArrayList);
                    historyList.setAdapter(ridesAdapter);
                }
            }
        });
        return view;
    }

    public void declareLayoutElements(View view) {
        historyList = (ListView) view.findViewById(R.id.history_list);
        getHistoryBtn = (Button) view.findViewById(R.id.getHistoryButton);
        if (lstRides != null) {
            ArrayList<Ride> rideArrayList = lstRides;
            RidesAdapter ridesAdapter = new RidesAdapter(getContext(), rideArrayList);
            historyList.setAdapter(ridesAdapter);
        }
    }

    public void getHistRides() {
        DatabaseReference ref = fbDatabase.getReference("Rides");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Ride> lstRidesFromDb = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Ride newRide = ds.getValue(Ride.class);
                    lstRidesFromDb.add(newRide);
                }
                lstRides = lstRidesFromDb;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: make Exception handling
            }
        });
    }
}