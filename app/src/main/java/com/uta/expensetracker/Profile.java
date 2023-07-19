package com.uta.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.view.ViewCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.Menu;

public class Profile extends AppCompatActivity {

    TextView name;
    TextView emailid;
    Button edit;

    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userReference;
   String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.textView4);
        emailid= findViewById(R.id.textView15);
        edit = findViewById(R.id.editbutton);

       setProfileDetails();

        edit.setOnClickListener(view -> goToEditProfile());


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
        case R.id.logout:
            //add the function to perform here
            userLogout();
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }

   public void userLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                dialogInterface.dismiss();
                Toast.makeText(Profile.this, "Logout Successfull", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Profile.this, LoginActivity.class));
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
                Toast.makeText(Profile.this, "logout cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
   }


    public void setProfileDetails(){
        userID = mAuth.getCurrentUser().getUid();

        userReference = database.getReference("users/"+userID);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uname = snapshot.child("name").getValue(String.class);
                String uemail = snapshot.child("email").getValue(String.class);
                System.out.println("name"+ uname+ uemail);
                name.setText(uname);
                emailid.setText(uemail);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("No user in the database found");
            }
        });
    }

    public void goToEditProfile(){
        userID = mAuth.getCurrentUser().getUid();
        userReference = database.getReference("users/"+userID);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uname = snapshot.child("name").getValue(String.class);
                String uemail = snapshot.child("email").getValue(String.class);
                System.out.println("name"+ uname+ uemail);
                Intent intent = new Intent(Profile.this,EditProfile.class);
                intent.putExtra("name",uname);
                intent.putExtra("email",uemail);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("No user in the database found");
            }
        });
    }

}