package com.example.caspe.kilonotes.operations;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.fragments.HistoryFragment;
import com.example.caspe.kilonotes.fragments.HomeFragment;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caspe on 13-12-2017.
 */

public class FirebaseHelper {
    private static final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    private static List<Ride> lstRides = new ArrayList<>();
    private static Ride lastRide;
}
