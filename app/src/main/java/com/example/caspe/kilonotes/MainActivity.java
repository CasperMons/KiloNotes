package com.example.caspe.kilonotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    TextView drivenKm;
    EditText startDistance;
    EditText endDistance;

    private DatabaseReference dbReference;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void declareLayoutElements(){
        drivenKm = (TextView)findViewById(R.id.driven_km_txt);
        startDistance = (EditText)findViewById(R.id.start_text);
        endDistance = (EditText)findViewById(R.id.edit_end_dist);
    }

    public void setDrivenKm(int startDistance, int endDistance){
        int drivenDistance = endDistance - startDistance;
        drivenKm.setText(Integer.toString(drivenDistance));
        // Todo: set drivenKm when editText's change
    }

    public void saveAction(View view){

    }

    public void autoFillStartDistance(View view){

    }

    public void autoFillEndDistance(View view){

    }


}