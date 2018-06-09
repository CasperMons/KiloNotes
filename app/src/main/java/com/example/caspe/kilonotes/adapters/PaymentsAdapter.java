package com.example.caspe.kilonotes.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.model.Payment;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

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

        final Payment payment = getItem(position);

        if (payment != null) {
            TextView txtMonthYear = (TextView) convertView.findViewById(R.id.item_month_year);
            TextView txtTotalDistance = (TextView) convertView.findViewById(R.id.item_total_km);
            TextView txtPrice = (TextView) convertView.findViewById(R.id.item_price);
            RelativeLayout containerIsPayed = (RelativeLayout) convertView.findViewById(R.id.container_ispayed);
            final CheckBox cbIsPayed = (CheckBox) convertView.findViewById(R.id.item_check_payed);

            txtMonthYear.setText(convertMonthYearToDateString(payment.year, payment.month));
            txtTotalDistance.setText(Long.toString(payment.totalDistance) + " Km");
            txtPrice.setText("€" + Ride.priceFormat.format(payment.price));

            if (payment.isPayed) {
                cbIsPayed.setChecked(true);
                cbIsPayed.setEnabled(false);
            }else{
                // Solve Adapter bug. Layout settings for checkbox are stuck after going through the if statement once.
                cbIsPayed.setChecked(false);
                cbIsPayed.setEnabled(true);
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
                    if (cbIsPayed.isChecked()) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.alert_title_confirm_make_payment)
                                .setMessage(R.string.alert_message_confirm_make_payment)
                                .setIcon(R.drawable.kilo_note_logo)
                                .setPositiveButton(R.string.alert_confirm_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        getUpdatablePayment(payment);

                                        cbIsPayed.setEnabled(false);
                                    }
                                }).setNegativeButton(R.string.alert_confirm_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cbIsPayed.setChecked(false);
                            }
                        }).show();
                    }
                }
            });

        }
        return convertView;
    }

    private String convertMonthYearToDateString(int year, int month) {
        Calendar monthYear = new GregorianCalendar(year, month - 1, 1);
        return sdf.format(monthYear.getTime());
    }

    private void toggleCheckbox(CheckBox checkBox) {
        if (!checkBox.isChecked()) {
            checkBox.setChecked(true);
        } else if (checkBox.isEnabled()) {
            checkBox.setChecked(false);
        }
    }

    private void getUpdatablePayment(final Payment payment) {

        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = fbDatabase.getReference("Payments");
        Query query = ref.orderByChild("userId").equalTo(payment.userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Payment paymentFromFb = snap.getValue(Payment.class);
                    if ((paymentFromFb.year == payment.year) && (paymentFromFb.month == payment.month)) {
                        String key = snap.getKey();
                        if (!key.equals("")) {
                            updatePayment(key, payment);
                        } else {
                            Toast.makeText(getContext(), R.string.toast_could_not_update, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.toast_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePayment(String key, Payment payment) {
        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = fbDatabase.getReference("Payments");
        // Update payment with isPayed true
        payment.isPayed = true;
        // Prepare a hashmap of key and payment to do update with
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, payment);

        ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (databaseError == null) {
                    builder.setTitle(R.string.alert_title_finish_payment)
                            .setMessage(R.string.alert_message_finish_payment_success)
                            .setIcon(R.drawable.kilo_note_logo_green).show();
                } else {
                    builder.setTitle(R.string.alert_title_finish_payment)
                            .setMessage(R.string.alert_message_finish_payment_fail)
                            .setIcon(R.drawable.kilo_note_logo_red).show();
                }
            }
        });


    }
}
