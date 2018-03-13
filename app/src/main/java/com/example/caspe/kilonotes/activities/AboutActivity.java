package com.example.caspe.kilonotes.activities;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.caspe.kilonotes.Data.DataHelper;
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
    ProgressBar progressCheckUpdates;

    double latestVersion;


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

        txtCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressCheckUpdates.setVisibility(View.VISIBLE);
                checkUpdates();
            }
        });
    }

    private void declareLayoutElements() {
        txtVersion = (TextView) findViewById(R.id.txt_version);
        txtCheckUpdate = (TextView) findViewById(R.id.txt_check_updates);
        imgLogo = (ImageView) findViewById(R.id.img_logo);
        progressCheckUpdates = (ProgressBar)findViewById(R.id.progress_update);
    }

    private void initData() {
        setCurrentVersion();
    }

    private void rotateLogo() {
        // Rotate the logo on click
        float currentRotation = imgLogo.getRotation();
        final RotateAnimation rotateAnim = new RotateAnimation(
                currentRotation, (currentRotation + 360), imgLogo.getWidth() / 2, imgLogo.getHeight() / 2
        );
        rotateAnim.setDuration(1000); // Use 0 ms to rotate instantly
        rotateAnim.setFillAfter(true); // Must be true or the animation will reset
        imgLogo.startAnimation(rotateAnim);
    }

    private void checkUpdates() {
        DatabaseReference ref = fbDatabase.getReference("/About");

        ref.child("LatestRelease").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                latestVersion = snapshot.getValue(Double.class);
                if(latestVersion != 0){
                    compareVersion(latestVersion);
                }else{
                    progressCheckUpdates.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(AboutActivity.this)
                            .setTitle(R.string.alert_title_general_error)
                            .setMessage(R.string.alert_message_failed_check_updates)
                            .setIcon(R.drawable.kilo_note_logo_red).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                new AlertDialog.Builder(AboutActivity.this)
                        .setTitle(R.string.alert_title_general_error)
                        .setMessage(R.string.alert_message_failed_check_updates)
                        .setIcon(R.drawable.kilo_note_logo_red).show();
            }
        });
    }

    private void compareVersion(double latestVersion){
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        if(latestVersion > DataHelper.CURRENT_VERSION){
                    builder.setTitle(R.string.alert_title_update_available)
                    .setMessage(R.string.alert_message_update_available)
                    .setIcon(R.drawable.kilo_note_logo_transparent);
        }else{
            builder.setTitle(R.string.alert_title_no_update)
                    .setMessage(R.string.alert_message_no_update)
                    .setIcon(R.drawable.kilo_note_logo_green);
        }
        progressCheckUpdates.setVisibility(View.INVISIBLE);
        builder.show();
    }

    private void setCurrentVersion() {
        String versionText = getResources().getString(R.string.txt_version);
        versionText += Double.toString(DataHelper.CURRENT_VERSION);
        txtVersion.setText(versionText);
    }
}
