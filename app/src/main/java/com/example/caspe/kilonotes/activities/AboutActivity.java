package com.example.caspe.kilonotes.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caspe.kilonotes.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutActivity extends AppCompatActivity {

    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    TextView txtVersion;
    TextView txtCheckUpdate;
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        declareLayoutElements();

        initData();

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateLogo();
            }
        });
    }

    private void declareLayoutElements() {
        txtVersion = (TextView) findViewById(R.id.txt_version);
        txtCheckUpdate = (TextView) findViewById(R.id.txt_check_updates);
        imgLogo = (ImageView)findViewById(R.id.img_logo);
    }

    private void initData() {
        setVersion();
    }

    private void rotateLogo(){
        // Rotate the logo on click
        float currentRotation = imgLogo.getRotation();
        final RotateAnimation rotateAnim = new RotateAnimation(
                currentRotation, (currentRotation + 360), imgLogo.getWidth()/2, imgLogo.getHeight()/2);
        rotateAnim.setDuration(1000); // Use 0 ms to rotate instantly
        rotateAnim.setFillAfter(true); // Must be true or the animation will reset
        imgLogo.startAnimation(rotateAnim);
    }

    private void setVersion() {
        DatabaseReference ref = fbDatabase.getReference("/About");

        ref.child("Version").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String versionText = getResources().getString(R.string.txt_version);
                versionText += snapshot.getValue().toString();

                txtVersion.setText(versionText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
