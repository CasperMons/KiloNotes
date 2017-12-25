package com.example.caspe.kilonotes.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by caspe on 1-12-2017.
 */

public class Ride implements Comparable<Ride>{
    public String userName; // Todo: check if firebase auth uses string or int id's
    public long startDistance;
    public long endDistance;
    public Date date;


    @Override
    public int compareTo(@NonNull Ride another) {
        int compareValue = this.date.compareTo(another.date);
        if(compareValue == 0){
            return this.date.compareTo(another.date);
        }
        return compareValue;
    }
}
