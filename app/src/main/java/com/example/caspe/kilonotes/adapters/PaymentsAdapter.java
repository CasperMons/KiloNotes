package com.example.caspe.kilonotes.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.model.Payment;
import com.example.caspe.kilonotes.model.Ride;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by caspe on 7-6-2018.
 */

public class PaymentsAdapter extends ArrayAdapter<Payment> {

    public PaymentsAdapter(Context context, ArrayList<Payment> lstPayments) {
        super(context, 0, lstPayments);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_payment, parent, false);
        }

        Payment payment = getItem(position);
        if (payment != null) {
            TextView txtMonthYear = (TextView) convertView.findViewById(R.id.item_month_year);
            TextView txtTotalDistance = (TextView) convertView.findViewById(R.id.item_total_km);
            TextView txtPrice = (TextView) convertView.findViewById(R.id.item_price);
            CheckBox cbIsPayed = (CheckBox) convertView.findViewById(R.id.item_check_payed);

            txtMonthYear.setText(convertMonthYearToDateString(payment.year, payment.month));
            txtTotalDistance.setText(Long.toString(payment.totalDistance) + " Km");
            txtPrice.setText("€" + Ride.priceFormat.format(payment.price));

            if(payment.isPayed){
                cbIsPayed.setChecked(true);
                cbIsPayed.setEnabled(false);
            }

            cbIsPayed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // TODO: make checked action. If checked to true, confirmation and update record
                }
            });

        }
        return convertView;
    }

    private String convertMonthYearToDateString(int year, int month) {
        Calendar monthYear = new GregorianCalendar(year, month - 1, 1);
        return sdf.format(monthYear.getTime());
    }
}
