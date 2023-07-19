package com.uta.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText etLoginEmail;
    EditText etLoginPassword;
    TextView tvRegisterHere;
    TextView forgotPassword;
    Button btnLogin;
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    String userID;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPass);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        forgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view -> loginUser());
        tvRegisterHere.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        forgotPassword.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        final EditText emailField = new EditText(this);
        emailField.setHint("Enter your registered email address");
        emailField.setWidth(400);
        emailField.setTextSize(19);
        emailField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(emailField);

        builder.setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               String emailAddress = emailField.getText().toString();
               mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {

                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           dialogInterface.dismiss();
                           Toast.makeText(LoginActivity.this, "Link has to sent to the email address", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                       } else{
                           Toast.makeText(LoginActivity.this, "Could not send the link to the email address", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                       }
                   }
               });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //startActivity(new Intent(LoginActivity.this,LoginActivity.class));

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loginUser(){
        String email = Objects.requireNonNull(etLoginEmail.getText()).toString();
        String password = Objects.requireNonNull(etLoginPassword.getText()).toString();

        if (TextUtils.isEmpty(email)){
            etLoginEmail.setError("Email cannot be empty");

            etLoginEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etLoginPassword.setError("Password cannot be empty");
            etLoginPassword.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
//                    userID = mAuth.getCurrentUser().getUid();
//                    DatabaseReference myRef = database.getReference("users");
//                    DatabaseReference expenseRef = myRef.child(userID).child("expenses");
//                    DatabaseReference newExpenseRef = expenseRef.push();
//                    newExpenseRef.setValue(new Expense("appu", "Car", 200.0f));
                    startActivity(new Intent(LoginActivity.this, Overview.class));
                }else{
                    Toast.makeText(LoginActivity.this, "Log in Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                }
            });

        }
    }

}