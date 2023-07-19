package com.uta.expensetracker;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.*;


public class RegisterActivity extends AppCompatActivity {

    EditText etname;
    EditText etRegEmail;
    EditText etRegPassword;
    EditText etConfirmPassword;
    TextView tvLoginHere;
    Button btnRegister;
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etname = findViewById(R.id.etname);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvLoginHere = findViewById(R.id.tvLoginHere);

        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view -> createUser());

        tvLoginHere.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

    }

    private void createUser(){
        String email = Objects.requireNonNull(etRegEmail.getText()).toString();
        String password = Objects.requireNonNull(etRegPassword.getText()).toString();
        String name = Objects.requireNonNull(etname.getText()).toString();
        String cPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString();

        if (TextUtils.isEmpty(name)){
            etRegEmail.setError("Name cannot be empty");
            etRegEmail.requestFocus();
        }
        else if (TextUtils.isEmpty(email)){
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        }
        else if (!password.equals(cPassword)){
            etConfirmPassword.setError("Passwords do not match ");
            etConfirmPassword.requestFocus();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User registered successfully, Login again", Toast.LENGTH_SHORT).show();

                    userID = mAuth.getCurrentUser().getUid();
                    DatabaseReference myRef = database.getReference("users");
                    myRef.child(userID).setValue(new User(name, email));

//                    DatabaseReference expenseRef = myRef.child(userID).child("expenses");
//                    DatabaseReference newExpenseRef = expenseRef.push();
//                    newExpenseRef.setValue(new Expense("gracehop", "Food", 100.0f));


                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this, "Registration Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            });
        }
    }

}