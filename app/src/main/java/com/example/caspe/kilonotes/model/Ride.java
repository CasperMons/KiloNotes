package com.example.caspe.kilonotes.model;

import android.support.annotation.NonNull;

/**
 * Created by caspe on 1-12-2017.
 */

public class Ride implements Comparable<Ride> {
    public String userName; // Todo: check if firebase auth uses string or int id's
    public long startDistance;
    public long endDistance;
    public long timestamp;

    @Override
    public int compareTo(@NonNull Ride another) {
        long compareValue = another.timestamp;
        return (this.timestamp < compareValue ? -1 : (compareValue == this.timestamp ? 0 : 1));
    }
}
