package com.example.caspe.kilonotes.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.activities.LoginActivity;
import com.example.caspe.kilonotes.activities.MainActivity;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFragment extends Fragment {

    TextView drivenDistance;
    EditText startDistance;
    EditText endDistance;
    Button saveBtn;
    ProgressBar progressBar;
    FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        declareLayoutElements(view);

        startDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                drivenDistance.setText("0 KM");
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.equals("")) {
                    setDrivenKm();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        endDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                drivenDistance.setText("0 KM");
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.equals("")) {
                    setDrivenKm();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if (checkFieldsFilled() && getDrivenDistance() > 0) {
                    builder.setTitle(R.string.alert_title_ask_save)
                            .setMessage(R.string.alert_message_ask_save)
                            .setIcon(R.drawable.kilo_note_logo_transparent)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    saveAction();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {

                    builder.setTitle(R.string.alert_title_cant_save)
                            .setMessage(R.string.alert_message_no_km)
                            .setIcon(R.drawable.kilo_note_logo_red);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        return view;
    }

    public void declareLayoutElements(View view) {
        drivenDistance = (TextView) view.findViewById(R.id.driven_km_txt);
        startDistance = (EditText) view.findViewById(R.id.edit_start_dist);
        endDistance = (EditText) view.findViewById(R.id.edit_end_dist);
        saveBtn = (Button) view.findViewById(R.id.save_btn);
        progressBar = (ProgressBar) view.findViewById(R.id.save_progress);
    }

    public void setDrivenKm() {
        if (checkFieldsFilled()) {
            int startKm = Integer.valueOf(startDistance.getText().toString());
            int endKm = Integer.valueOf(endDistance.getText().toString());

            int drivenKm = endKm - startKm;
            if (drivenKm > 0) {
                drivenDistance.setText(Integer.toString(drivenKm) + " KM");
            }
        }
    }

    public int getDrivenDistance() {
        if (checkFieldsFilled()) {
            return Integer.parseInt(endDistance.getText().toString()) - Integer.parseInt(startDistance.getText().toString());
        } else {
            return 0;
        }
    }

    public boolean checkFieldsFilled() {
        if (!(endDistance.getText().toString().matches("")) && !(startDistance.getText().toString().matches(""))) {
            return true;
        } else {
            return false;
        }
    }

    public void saveAction() {
        // TODO: also write data offline
        Ride newRide = new Ride();
        newRide.userName = getCurrentUser();
        newRide.startDistance = Integer.parseInt(startDistance.getText().toString());
        newRide.endDistance = Integer.parseInt(endDistance.getText().toString());
        newRide.date = dateFormat.format(new Date());

        DatabaseReference ref = fbDatabase.getReference("Rides");
//        DatabaseReference rideRef = ref.child("Ride: " + newRide.date);

        ref.push().setValue(newRide, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                AlertDialog.Builder saveResultDialogBuilder = new AlertDialog.Builder(getActivity());
                if (error == null) {
                    saveResultDialogBuilder.setTitle(R.string.alert_success)
                            .setMessage(R.string.alert_message_ride_saved)
                            .setIcon(R.drawable.kilo_note_logo_green);

                } else {
                    saveResultDialogBuilder.setTitle(R.string.alert_title_fail)
                            .setMessage(R.string.alert_message_fail)
                            .setIcon(R.drawable.kilo_note_logo_red);
                    Log.e("SaveRide: ", "Error: " + error);
                }

                AlertDialog dialog = saveResultDialogBuilder.create();
                progressBar.setVisibility(View.INVISIBLE);
                dialog.show();
            }
        });
    }

    public String getCurrentUser() {
        String testUser = "testUser";
        return testUser;
    }
}
