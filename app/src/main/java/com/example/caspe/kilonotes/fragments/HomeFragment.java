package com.example.caspe.kilonotes.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.activities.LoginActivity;
import com.example.caspe.kilonotes.activities.MainActivity;


public class HomeFragment extends Fragment {

    TextView drivenDistance;
    EditText startDistance;
    EditText endDistance;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        declareLayoutElements(view);

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

        return view;
    }

    public void declareLayoutElements(View view){
        drivenDistance = (TextView)view.findViewById(R.id.driven_km_txt);
        startDistance = (EditText)view.findViewById(R.id.edit_start_dist);
        endDistance = (EditText)view.findViewById(R.id.edit_end_dist);
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
}
