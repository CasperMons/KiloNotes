package com.example.caspe.kilonotes.model;

import com.google.firebase.database.Exclude;

/**
 * Created by caspe on 1-12-2017.
 */

public class Ride {
    public String userName; // Todo: check if firebase auth uses string or int id's
    public long startDistance;
    public long endDistance;
    public String date;
}
