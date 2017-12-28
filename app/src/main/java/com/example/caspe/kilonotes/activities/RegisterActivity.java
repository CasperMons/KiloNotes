package com.example.caspe.kilonotes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.caspe.kilonotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText editUserName;
    EditText editPassword;
    EditText editConfirmPassword;
    Button registerUserBtn;
    ProgressBar regProgress;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reqister);

        declareLayoutElements();

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewUser();
            }
        });
    }

    private void declareLayoutElements() {
        editUserName = (EditText) findViewById(R.id.edit_new_username);
        editPassword = (EditText) findViewById(R.id.edit_new_pass);
        editConfirmPassword = (EditText) findViewById(R.id.edit_confirm_pass);
        registerUserBtn = (Button) findViewById(R.id.reg_btn);
        regProgress = (ProgressBar) findViewById(R.id.register_progress);
    }

    private void saveNewUser() {
        regProgress.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        String userName = editUserName.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPass = editConfirmPassword.getText().toString();
        if (checkCredentials(userName, password, confirmPass)) {
            firebaseAuth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    registerFeedback(task.isSuccessful());
                }
            });
        }
    }

    private boolean checkCredentials(String userName, String password, String confirmPass) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if (password.length() < 8) {
            alertDialog.setTitle(R.string.alert_title_pass_too_short)
                    .setMessage(R.string.alert_message_pass_too_short)
                    .setIcon(R.drawable.kilo_note_logo_red);
        } else if (!password.equals(confirmPass)) {
            alertDialog.setTitle(R.string.alert_title_pass_false_match)
                    .setMessage(R.string.alert_message_pass_false_match)
                    .setIcon(R.drawable.kilo_note_logo_red);
        } else if (userName.equals("")) {
            alertDialog.setTitle(R.string.alert_title_username_empty)
                    .setMessage(R.string.alert_message_username_empty)
                    .setIcon(R.drawable.kilo_note_logo_red);
        } else {
            return true;
        }
        regProgress.setVisibility(View.INVISIBLE);
        alertDialog.show();
        return false;
    }

    private void registerFeedback(Boolean success) {
        regProgress.setVisibility(View.INVISIBLE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if (success) {
            alertDialog.setTitle(R.string.alert_title_reg_success)
                    .setMessage(R.string.alert_message_reg_success)
                    .setIcon(R.drawable.kilo_note_logo_green)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startMainActivity();
                        }
                    });

        } else {
            alertDialog.setTitle(R.string.alert_title_reg_fail)
                    .setMessage(R.string.alert_message_reg_fail)
                    .setIcon(R.drawable.kilo_note_logo_red);
        }
        alertDialog.show();
    }

    private void startMainActivity(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
