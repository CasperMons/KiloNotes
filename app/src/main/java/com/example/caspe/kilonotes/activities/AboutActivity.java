package com.example.caspe.kilonotes.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.io.File;

public class AboutActivity extends AppCompatActivity {

    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    final int REQ_READ_WRITE_PERMISSION = 120;
    TextView txtVersion;
    TextView txtCheckUpdate;
    ImageView imgLogo;
    ProgressBar progressCheckUpdates;

    final DatabaseReference ref = fbDatabase.getReference("/About");

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
        progressCheckUpdates = (ProgressBar) findViewById(R.id.progress_update);
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


        ref.child("LatestRelease").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                latestVersion = snapshot.getValue(Double.class);
                if (latestVersion != 0) {
                    compareVersion(latestVersion);
                } else {
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

    private void compareVersion(double latestVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        if (latestVersion > DataHelper.CURRENT_VERSION) {
            builder.setTitle(R.string.alert_title_update_available)
                    .setMessage(R.string.alert_message_update_available)
                    .setIcon(R.drawable.kilo_note_logo_transparent)
                    .setPositiveButton(R.string.alert_confirm_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getDownloadUrl();
                        }
                    }).setNegativeButton(R.string.alert_confirm_cancel, null);
        } else {
            builder.setTitle(R.string.alert_title_no_update)
                    .setMessage(R.string.alert_message_no_update)
                    .setIcon(R.drawable.kilo_note_logo_green);
        }
        progressCheckUpdates.setVisibility(View.INVISIBLE);
        builder.show();
    }

    private void setCurrentVersion() {
        String versionText = getResources().getString(R.string.txt_version);
        versionText += " ";
        versionText += Double.toString(DataHelper.CURRENT_VERSION);
        txtVersion.setText(versionText);
    }

    private void getDownloadUrl() {
        // Retrieve download url from firebase
        ref.child("DownloadUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String downloadUrl = "";
                downloadUrl = snapshot.getValue(String.class);
                if (!downloadUrl.equals("")) {
                    downloadAndInstallApk(downloadUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: add error alert
            }
        });
    }

    private void downloadAndInstallApk(String url) {
        // Ask for permission on external storage if not already given
        if ((ContextCompat.checkSelfPermission(AboutActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(AboutActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AboutActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_READ_WRITE_PERMISSION);
        } else {
            // Determine URI to download directory
            final String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            final Uri uri = Uri.parse("file://" + destination + DataHelper.APP_APK_NAME);

            //Delete update file if exists
            File file = new File(destination);
            if (file.exists()) {
                file.delete(); // TODO: check if delete works
            }

            //Create a new Download request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(getResources().getString(R.string.download_update));
            request.setTitle(getResources().getString(R.string.app_name));

            //Set the destination of the file
            request.setDestinationUri(uri);

            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

            //Set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    try {
                        // TODO: fix permission to use URI to install the downloaded apk
                        File APK = new File(destination + DataHelper.APP_APK_NAME);
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setDataAndType(uri,"application/vnd.android.package-archive");
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(install);

                        unregisterReceiver(this);
                        finish();
                    } catch (Exception ex) {
                        Log.e("BROADCASTRECEIVER", ex.getMessage());
                    }
                }
            };
            //register receiver for when .apk download is compete
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }
}
