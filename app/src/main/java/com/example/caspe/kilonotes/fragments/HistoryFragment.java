package com.example.caspe.kilonotes.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;


public class HistoryFragment extends Fragment {
    Button findBtn;
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
        findBtn = (Button) view.findViewById(R.id.find_btn);

        findBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                find();
            }
        });

        return view;
    }

    // Finding the single last inserted record
    // Here for testing purposes. Later to be implemented in home fragment to get endDistance
    public void find() {

        final DatabaseReference ref = fbDatabase.getReference();
        Query finalRecord = ref.child("Rides").orderByKey().limitToLast(1);

        finalRecord.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ride lastRide = dataSnapshot.getValue(Ride.class);
                Log.d("TEST", dataSnapshot.getValue().toString());
                // TODO: map json object to Ride object properly
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: do Exception handeling
            }
        });
    }

}
