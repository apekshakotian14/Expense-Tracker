package com.uta.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends AppCompatActivity {
EditText email;
EditText password;
EditText confirmPassword;
Button reset;
FirebaseDatabase database = FirebaseDatabase.getInstance();
FirebaseAuth mAuth;
String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        email = findViewById(R.id.editTextTextEmailAddress4);
        password = findViewById(R.id.editTextTextPassword6);
        confirmPassword = findViewById(R.id.editTextTextPassword7);
        reset = findViewById(R.id.button4);
        mAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(view -> resetPassword());


    }

    private void resetPassword() {
        String email_str = email.getText().toString();
        String password_str = password.getText().toString();
        String confirmPassword_str = confirmPassword.getText().toString();



    }
}