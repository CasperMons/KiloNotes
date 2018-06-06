package com.example.caspe.kilonotes.activities;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.adapters.PaymentsAdapter;
import com.example.caspe.kilonotes.model.Payment;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentsActivity extends AppCompatActivity {
    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    TextView txtTitle;
    ListView lvPayments;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<Payment> lstPayments;
    PaymentsAdapter paymentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        declareLayoutElements();
        initData();

        getPaymentsFromFb();

    }

    private void declareLayoutElements() {
        txtTitle = (TextView) findViewById(R.id.txt_title_payments);
        lvPayments = (ListView) findViewById(R.id.list_payments);
    }

    private void initData() {
        setTitle();
        lstPayments = new ArrayList<>();
        paymentsAdapter = new PaymentsAdapter(this, lstPayments);
        lvPayments.setAdapter(paymentsAdapter);
    }

    private void setTitle() {
        String title = getResources().getText(R.string.payments_for).toString();
        String userName = currentUser.getDisplayName();
        title += " " + userName;
        txtTitle.setText(title);
    }

    private void getPaymentsFromFb(){
        String currentUserName = currentUser.getDisplayName();
        DatabaseReference dbRef = fbDatabase.getReference("Payments");
        Query query = dbRef.orderByChild("userName").equalTo(currentUserName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lstPayments.clear();

                for (DataSnapshot paymentFromDb : dataSnapshot.getChildren()) {
                    lstPayments.add(paymentFromDb.getValue(Payment.class));
                }

                // TODO: stop refreshing

                updatePaymentsListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBERROR", databaseError.getMessage());
            }
        });
    }

    private void updatePaymentsListView(){
        paymentsAdapter.notifyDataSetChanged();
    }
}