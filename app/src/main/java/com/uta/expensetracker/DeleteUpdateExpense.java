package com.uta.expensetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DeleteUpdateExpense extends AppCompatActivity {


    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    private EditText upd_name;
    private EditText upd_amount;
    private EditText upd_description;
    private Button update;

    private Spinner spinner;

    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference expReference;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_update_expense);
        getSupportActionBar().setTitle("Update/Delete");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Expense expense = (Expense) getIntent().getSerializableExtra("expense");
        initDatepicker();
        mAuth = FirebaseAuth.getInstance();

        upd_name = findViewById(R.id.editTextTextPersonName);
        upd_amount = findViewById(R.id.amount_edittext);
        upd_description = findViewById(R.id.editTextTextPersonName2);
        dateButton = findViewById(R.id.datepickerButton);
        dateButton.setText(getTodaysDate());
        spinner = findViewById(R.id.spinner);
        update = findViewById(R.id.button2);

        List<String> categories = new ArrayList<>();
        categories.add(0, "choose");
        categories.add("Food");
        categories.add("Grocery");
        categories.add("Rent");
        categories.add("MISC");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0,false);

        upd_name.setText(expense.getName());
        double amount = expense.getAmount();
        String amountStr = String.valueOf(amount);
        upd_amount.setText(amountStr);
        upd_description.setText(expense.getDescription());

        int categoryIndex = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(expense.getCategory());
        System.out.println("index" + categoryIndex);
        spinner.setSelection(categoryIndex);

        String sdate = expense.getDate();
        String dateAlone[] = sdate.split("T");
        String dateSplit[] = dateAlone[0].split("-");
        int year = Integer.valueOf(dateSplit[0]);
        int month = Integer.valueOf(dateSplit[1]);
        int day = Integer.valueOf(dateSplit[2]);
        dateButton.setText(makeDateString(day,month,year));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("choose")) {
                } else {
                    String item = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(), "Selected:" + item, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = Objects.requireNonNull(upd_name.getText().toString()) ;
               // Double amount = Objects.requireNonNull(Double.valueOf(upd_amount.getText().toString())) ;
                String uamount = Objects.requireNonNull(upd_amount.getText().toString());
                double amount =000.00;
                if (TextUtils.isEmpty(uamount)) {
                    upd_amount.setError("Amount cannot be empty");
                    upd_amount.requestFocus();
                } else {
                    amount = Double.valueOf(uamount);
                }

                String description = upd_description.getText().toString();
                if (TextUtils.isEmpty(description)){
                    description = "No description";
                }
                String category = Objects.requireNonNull(spinner.getSelectedItem().toString()) ;
                String dateStr = Objects.requireNonNull(dateButton.getText().toString());
                System.out.println("datestr" + dateStr);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
                Date date = null;
                try {
                    date = dateFormat.parse(dateStr);

                    System.out.println(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String newdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date);
                System.out.println("newdate" + newdate);

                if (TextUtils.isEmpty(name)){
                    System.out.println("No name");
                    upd_name.setError("Name cannot be empty");
                    upd_name.requestFocus();

                } else if (TextUtils.isEmpty(dateStr)){
                    dateButton.setError("date cannot be empty");
                    dateButton.requestFocus();
                }else if (category.equals("choose")){
                    TextView errorText = (TextView) spinner.getSelectedView();
                    errorText.setError("Please select a category");
                    spinner.requestFocus();
                } else {

                    userID = mAuth.getCurrentUser().getUid();
                    expReference = database.getReference("users/" + userID + "/expenses/" + expense.getId());


                    HashMap<String, Object> updatedExpense = new HashMap<>();
                    updatedExpense.put("name", name);
                    updatedExpense.put("amount", amount);
                    updatedExpense.put("description", description);
                    updatedExpense.put("date", newdate);
                    updatedExpense.put("category", category);

                    expReference.updateChildren(updatedExpense, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                Toast.makeText(DeleteUpdateExpense.this, "Your expense" + expense.getName() + " got updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DeleteUpdateExpense.this, History.class));
                            } else {
                                Toast.makeText(DeleteUpdateExpense.this, "Could not update the expense " + expense.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.delete:
                //add the function to perform here
                Expense expense = (Expense) getIntent().getSerializableExtra("expense");
                AlertDialog.Builder builder = new AlertDialog.Builder(DeleteUpdateExpense.this);
                builder.setMessage("Are you sure you want to delete?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userID = mAuth.getCurrentUser().getUid();
                        expReference = database.getReference("users/"+userID+"/expenses/"+expense.getId());
                        expReference.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if( error == null){
                                    Toast.makeText(DeleteUpdateExpense.this, "Deleted expense " +  expense.getName() + " successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DeleteUpdateExpense.this, Overview.class));
                                } else{
                                    Toast.makeText(DeleteUpdateExpense.this, "Could not delete " +  expense.getName(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DeleteUpdateExpense.this, History.class));
                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(DeleteUpdateExpense.this, "Delete cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return(true);

        }
        return(super.onOptionsItemSelected(item));
    }





    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);

    }

    private void initDatepicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);



        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";

        if (month == 2)
            return "FEB";

        if (month == 3)
            return "MAR";

        if (month == 4)
            return "APR";

        if (month == 5)
            return "MAY";

        if (month == 6)
            return "JUN";

        if (month == 7)
            return "JUL";

        if (month == 8)
            return "AUG";

        if (month == 9)
            return "SEP";

        if (month == 10)
            return "OCT";

        if (month == 11)
            return "NOV";

        if (month == 12)
            return "DEC";

        return "JAN";

    }


    public void openDatepicker(View view) {
        datePickerDialog.show();
    }
}