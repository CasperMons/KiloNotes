package com.example.caspe.kilonotes.activities;

import android.app.AlertDialog;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PaymentsActivity extends AppCompatActivity {
    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    TextView txtTitle;
    ListView lvPayments;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<Payment> lstPayments;
    PaymentsAdapter paymentsAdapter;
    SwipeRefreshLayout swipeRefreshPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        declareLayoutElements();
        initData();

        getPaymentsFromFb();

        swipeRefreshPayments.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshPayments.setRefreshing(true);
                getPaymentsFromFb();
            }
        });
    }

    private void declareLayoutElements() {
        txtTitle = (TextView) findViewById(R.id.txt_title_payments);
        lvPayments = (ListView) findViewById(R.id.list_payments);
        swipeRefreshPayments = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
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

                // TODO: order lstPayments by year -> month descending
                orderPaymentsList();

                swipeRefreshPayments.setRefreshing(false);

                updatePaymentsListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBERROR", databaseError.getMessage());
                swipeRefreshPayments.setRefreshing(false);
            }
        });
    }

    private void updatePaymentsListView(){
        paymentsAdapter.notifyDataSetChanged();
    }

    private void orderPaymentsList(){
        orderListYear();
        orderListMonth();
    }

    private void orderListYear(){

        Comparator<Payment> comparableYear = new Comparator<Payment>() {
            @Override
            public int compare(Payment first, Payment other) {
                return (first.year - other.year);
            }
        };

        Collections.sort(lstPayments, Collections.<Payment>reverseOrder(comparableYear));
    }
    private void orderListMonth(){

        Comparator<Payment> comparableMonth = new Comparator<Payment>() {
            @Override
            public int compare(Payment first, Payment other) {
                return (first.month - other.month);
            }
        };

        Collections.sort(lstPayments, Collections.<Payment>reverseOrder(comparableMonth));
    }
}