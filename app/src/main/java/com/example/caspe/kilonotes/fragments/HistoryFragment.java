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

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    ListView historyList;
    private List<Ride> lstRides;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        DatabaseReference ref = fbDatabase.getReference("Rides");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstRides = getAllRidesFromDb(dataSnapshot);
                fillList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: make Exception handling
            }
        });

        return view;
    }


    private List<Ride> getAllRidesFromDb(DataSnapshot dataSnapshot) {
        List<Ride> lstRides = new ArrayList<Ride>();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Ride newRide = ds.getValue(Ride.class);
            lstRides.add(newRide);
        }

        return lstRides;
    }

    public void declareLayoutElements(View view) {
        historyList = (ListView) view.findViewById(R.id.historyList);
    }

    public void fillList(){
        ArrayAdapter<Ride> adapter = new ArrayAdapter<Ride>(getActivity(), android.R.layout.simple_list_item_1, lstRides);
//        historyList.setAdapter(adapter);
    }

}