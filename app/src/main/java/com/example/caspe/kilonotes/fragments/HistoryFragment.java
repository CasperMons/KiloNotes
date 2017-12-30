package com.example.caspe.kilonotes.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.activities.MainActivity;
import com.example.caspe.kilonotes.adapters.RidesAdapter;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class HistoryFragment extends Fragment {

    ListView historyList;
    ArrayList<Ride> lstRides;
    SwipeRefreshLayout swipeRefreshHistory;
    TextView displayDate;
    DatePickerDialog.OnDateSetListener setDateListener;


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
        declareLayoutElements(view);
        setHistoryRides();
        swipeRefreshHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), R.string.toast_refresh_history, Toast.LENGTH_SHORT).show();
                setHistoryRides();
            }
        });

        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        R.style.AppTheme,
                        setDateListener,
                        year, month, day);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });

        setDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                displayDate.setText(day + "-" + month + "-" + year);
            }
        };
        return view;
    }

    public void declareLayoutElements(View view) {
        historyList = (ListView) view.findViewById(R.id.history_list);
        swipeRefreshHistory = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        displayDate = (TextView) view.findViewById(R.id.display_date);
    }

    public void setHistoryRides() {
        DatabaseReference ref = fbDatabase.getReference("Rides");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Ride> lstRidesFromDb = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Ride newRide = ds.getValue(Ride.class);
                    lstRidesFromDb.add(newRide);
                }
                lstRides = lstRidesFromDb;
                swipeRefreshHistory.setRefreshing(false);
                fillHistory();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBERROR", databaseError.getMessage());
            }
        });
    }

    public void fillHistory() {
        if (lstRides != null) {
            Collections.sort(lstRides, Collections.reverseOrder());
            RidesAdapter ridesAdapter = new RidesAdapter(getContext(), lstRides);
            historyList.setAdapter(ridesAdapter);
        }
    }
}