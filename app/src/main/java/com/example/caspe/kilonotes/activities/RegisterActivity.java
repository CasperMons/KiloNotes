package com.example.caspe.kilonotes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText editEmail;
    EditText editNickname;
    EditText editPassword;
    EditText editConfirmPassword;
    Button registerUserBtn;
    ProgressBar regProgress;
    Button goLoginBtn;

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
        goLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void declareLayoutElements() {
        editEmail = (EditText) findViewById(R.id.edit_new_email);
        editPassword = (EditText) findViewById(R.id.edit_new_pass);
        editConfirmPassword = (EditText) findViewById(R.id.edit_confirm_pass);
        registerUserBtn = (Button) findViewById(R.id.reg_btn);
        regProgress = (ProgressBar) findViewById(R.id.register_progress);
        editNickname = (EditText) findViewById(R.id.edit_nick_name);
        goLoginBtn = (Button)findViewById(R.id.btn_go_to_login);
    }

    private void saveNewUser() {
        regProgress.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        String nickname = editNickname.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPass = editConfirmPassword.getText().toString();
        if (checkCredentials(nickname, email, password, confirmPass)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (task.isSuccessful()) {
                        setNickname();
                    }
                }
            });
        }
    }

    private boolean checkCredentials(String nickname, String email, String password, String confirmPass) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if (password.length() < 8) {
            alertDialog.setTitle(R.string.alert_title_pass_too_short)
                    .setMessage(R.string.alert_message_pass_too_short)
                    .setIcon(R.drawable.kilo_note_logo_red);
        } else if (!password.equals(confirmPass)) {
            alertDialog.setTitle(R.string.alert_title_pass_false_match)
                    .setMessage(R.string.alert_message_pass_false_match)
                    .setIcon(R.drawable.kilo_note_logo_red);
        } else if (email.equals("")) {
            alertDialog.setTitle(R.string.alert_title_email_empty)
                    .setMessage(R.string.alert_message_email_empty)
                    .setIcon(R.drawable.kilo_note_logo_red);
        } else if (nickname.equals("")) {
            alertDialog.setTitle(R.string.alert_title_nickname_empty)
                    .setMessage(R.string.alert_message_nickname_empty)
                    .setIcon(R.drawable.kilo_note_logo_red);
        } else {
            return true;
        }
        regProgress.setVisibility(View.INVISIBLE);
        alertDialog.show();
        return false;
    }

    private void setNickname() {
        String nickname = editNickname.getText().toString();
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname).build();
        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        registerFeedback(task.isSuccessful());
                    }
                });

    }

    private void registerFeedback(Boolean success) {
        regProgress.setVisibility(View.INVISIBLE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if (success) {
            alertDialog.setTitle("Welkom " + currentUser.getDisplayName())
                    .setMessage(R.string.alert_message_reg_success)
                    .setIcon(R.drawable.kilo_note_logo_green)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startMainActivity();
                            //TODO: add email verification
                        }
                    });

        } else {
            alertDialog.setTitle(R.string.alert_title_reg_fail)
                    .setMessage(R.string.alert_message_reg_fail)
                    .setIcon(R.drawable.kilo_note_logo_red);
        }
        alertDialog.show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
