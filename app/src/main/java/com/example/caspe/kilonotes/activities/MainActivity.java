package com.example.caspe.kilonotes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.caspe.kilonotes.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    TextView drivenDistance;
    EditText startDistance;
    EditText endDistance;

    private DatabaseReference dbReference;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        declareLayoutElements();

        startDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                drivenDistance.setText("0 KM");
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.equals(""))
                {
                    setDrivenKm();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        endDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                drivenDistance.setText("0 KM");
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.equals(""))
                {
                    setDrivenKm();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    public void declareLayoutElements(){
        drivenDistance = (TextView)findViewById(R.id.driven_km_txt);
        startDistance = (EditText)findViewById(R.id.edit_start_dist);
        endDistance = (EditText)findViewById(R.id.edit_end_dist);
    }

    public void setDrivenKm(){
        if(!(endDistance.getText().toString().matches(""))&& !(startDistance.getText().toString().matches(""))) {
            int startKm = Integer.valueOf(startDistance.getText().toString());
            int endKm = Integer.valueOf(endDistance.getText().toString());

            int drivenKm = endKm - startKm;
            if(drivenKm > 0){
                drivenDistance.setText(Integer.toString(drivenKm) + " KM");
            }
        }
    }

    public void saveAction(View view){

    }


    public void autoFillStartDistance(View view){

    }

    public void autoFillEndDistance(View view){

    }


}