package com.example.caspe.kilonotes.activities;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class PaymentsActivity extends AppCompatActivity {
    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    Button btnTest;
    TextView txtTitle;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);


        declareLayoutElements();
        initData();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN", "Token: " + token);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = fbDatabase.getReference("TestRides");
                Ride newRide = new Ride();
                newRide.userId = currentUser.getUid();
                newRide.userName = "N/A";
                newRide.startDistance = 10;
                newRide.endDistance = 20;
                newRide.timestamp = new Date().getTime();
                ref.push().setValue(newRide, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            new AlertDialog.Builder(PaymentsActivity.this).setTitle("Success").setMessage("Test successfull").show();
                        } else {
                            new AlertDialog.Builder(PaymentsActivity.this).setTitle("Fail").setMessage(databaseError.getMessage().toString()).show();
                        }
                    }
                });
            }
        });
    }

    private void declareLayoutElements() {
        txtTitle = (TextView) findViewById(R.id.txt_title_payments);
        btnTest = (Button) findViewById(R.id.btn_add_test_ride);
    }

    private void initData() {
        txtTitle.setText(getResources().getText(R.string.payments_for) + " " + currentUser.getDisplayName());
    }
}