package com.example.caspe.kilonotes.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    EditText editUserName;
    EditText editPassword;
    Button loginBtn;
    Button newUsrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        declareLayoutElements();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: progressbar
                login();
            }
        });

        newUsrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegisterActivity();
            }
        });

    }

    private void declareLayoutElements() {
        loginBtn = (Button) findViewById(R.id.login_btn);
        newUsrBtn = (Button) findViewById(R.id.btn_reg_new_user);
        editUserName = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void login() {
        String userName = editUserName.getText().toString();
        String password = editPassword.getText().toString();
        if (!userName.equals("") && !password.equals("")) {
            firebaseAuth.signInWithEmailAndPassword(userName, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.e("FIREBASEAUTH", "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, R.string.toast_auth_success, Toast.LENGTH_LONG).show();
                                startMainActivity();
                            } else {
                                Log.e("FIREBASEAUTH", "signInWithEmail:failure");
                                Toast.makeText(LoginActivity.this, R.string.toast_auth_fail, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}