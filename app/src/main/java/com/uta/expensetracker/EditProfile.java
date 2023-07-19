package com.uta.expensetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {
EditText ename;
TextView eemail;
Button save;

FirebaseDatabase database = FirebaseDatabase.getInstance();
FirebaseAuth mAuth;
DatabaseReference editReference;
String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ename = findViewById(R.id.editTextTextPersonName);
        eemail = findViewById(R.id.textView21);
        save = findViewById(R.id.button3);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        ename.setText(name);
        eemail.setText(email);
        mAuth = FirebaseAuth.getInstance();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editName = ename.getText().toString();

                userID = mAuth.getCurrentUser().getUid();
                editReference = database.getReference("users/"+userID);
                HashMap<String,Object> editProfile = new HashMap<>();
                editProfile.put("name",editName);

                editReference.updateChildren(editProfile, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error == null){
                            Toast.makeText(EditProfile.this, "Successfully changed to name to" + editName, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this,Profile.class));
                        } else{
                            Toast.makeText(EditProfile.this, "Sorry, Cannot change the name to" + editName, Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });


    }
}