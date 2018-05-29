package com.example.caspe.kilonotes.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.model.CustomUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    FirebaseUser currentUser;

    EditText editEmail;
    EditText editPassword;
    Button loginBtn;
    Button newUsrBtn;
    ProgressBar loginProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        declareLayoutElements();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgress.setVisibility(View.VISIBLE);
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
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        loginProgress = (ProgressBar) findViewById(R.id.login_progress);
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
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        if (!email.equals("") && !password.equals("")) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.e("FIREBASEAUTH", "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, R.string.toast_auth_success, Toast.LENGTH_LONG).show();
                                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                checkCustomUser();
                            } else {
                                Log.e("FIREBASEAUTH", "signInWithEmail:failure");
                                loginProgress.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, R.string.toast_auth_fail, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void checkCustomUser() {
        final CustomUser newCustomUser = new CustomUser();
        newCustomUser.userName = currentUser.getDisplayName();
        newCustomUser.id = currentUser.getUid();

        DatabaseReference ref = fbDatabase.getReference("Users");
        Query query = ref.orderByChild("userName").equalTo(newCustomUser.userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CustomUser customUserFromFb = null;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    customUserFromFb = ds.getValue(CustomUser.class);
                }
                if (customUserFromFb == null) {
                    createNewCustomUser(newCustomUser);
                } else {
                    loginProgress.setVisibility(View.INVISIBLE);
                    startMainActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loginProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void createNewCustomUser(CustomUser newCustomUser) {
        DatabaseReference ref = fbDatabase.getReference("Users");
        ref.push().setValue(newCustomUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference databaseReference) {
                loginProgress.setVisibility(View.INVISIBLE);
                if (error == null) {
                    startMainActivity();
                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setIcon(R.drawable.kilo_note_logo_red)
                            .setTitle(R.string.alert_title_general_error)
                            .setMessage(R.string.alert_message_fail).show();
                }
            }
        });

    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog saveResultDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.alert_title_ask_leave)
                .setMessage(R.string.alert_message_ask_leave)
                .setIcon(R.drawable.kilo_note_logo)
                .setPositiveButton(R.string.alert_confirm_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.alert_confirm_stay, null).show();
    }
}