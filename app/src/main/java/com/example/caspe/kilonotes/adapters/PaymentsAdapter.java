package com.example.caspe.kilonotes.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
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
            RelativeLayout containerIsPayed = (RelativeLayout) convertView.findViewById(R.id.container_ispayed);
            final CheckBox cbIsPayed = (CheckBox) convertView.findViewById(R.id.item_check_payed);

            txtMonthYear.setText(convertMonthYearToDateString(payment.year, payment.month));
            txtTotalDistance.setText(Long.toString(payment.totalDistance) + " Km");
            txtPrice.setText("€" + Ride.priceFormat.format(payment.price));

            if(payment.isPayed){
                cbIsPayed.setChecked(true);
                cbIsPayed.setEnabled(false);
            }

            containerIsPayed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleCheckbox(cbIsPayed);
                }
            });

            cbIsPayed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.alert_title_confirm_make_payment)
                            .setMessage(R.string.alert_message_confirm_make_payment)
                            .setIcon(R.drawable.kilo_note_logo)
                            .show();
                    //TODO: set positive and negative button and action
                }
            });

        }
        return convertView;
    }

    private String convertMonthYearToDateString(int year, int month) {
        Calendar monthYear = new GregorianCalendar(year, month - 1, 1);
        return sdf.format(monthYear.getTime());
    }

    private void toggleCheckbox(CheckBox checkBox){
        if(!checkBox.isChecked()){
            checkBox.setChecked(true);
        }else if(checkBox.isEnabled()){
            checkBox.setChecked(false);
        }
    }
}
