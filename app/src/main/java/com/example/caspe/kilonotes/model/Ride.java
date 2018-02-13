package com.example.caspe.kilonotes.model;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;

/**
 * Created by caspe on 1-12-2017.
 */

public class Ride implements Comparable<Ride> {
    public static final double RIDE_PRICE = 0.15;
    public static DecimalFormat priceFormat = new DecimalFormat("##00.00");


    public String userName;
    public String userId;
    public long startDistance;
    public long endDistance;
    public long timestamp;

    @Override
    public int compareTo(@NonNull Ride another) {
        long compareValue = another.timestamp;
        return (this.timestamp < compareValue ? -1 : (compareValue == this.timestamp ? 0 : 1));
    }
}
