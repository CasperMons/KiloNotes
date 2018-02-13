package com.example.caspe.kilonotes.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.caspe.kilonotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentsActivity extends AppCompatActivity {
    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    Button testBtn;
    TextView txtTitle;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        declareLayoutElements();
        initData();
    }

    private void declareLayoutElements(){
        txtTitle = (TextView)findViewById(R.id.txt_title_payments);
    }

    private void initData(){
        txtTitle.setText(getResources().getText(R.string.payments_for) + " " + currentUser.getDisplayName());
    }
}
