package com.uta.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddExpense extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Spinner spinner;
    private EditText name;
    private EditText amount;
    private EditText description;
    private Button add;


    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String userID;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        getSupportActionBar().setTitle("Add Expense");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDatepicker();
        dateButton = findViewById(R.id.datepickerButton);
        dateButton.setText(getTodaysDate());
        spinner = findViewById(R.id.spinner);
        name = findViewById(R.id.editTextTextPersonName);
        amount = findViewById(R.id.amount_edittext);
        description = findViewById(R.id.editTextTextPersonName2);
        add = findViewById(R.id.addbtn);


        mAuth = FirebaseAuth.getInstance();

        List<String> categories = new ArrayList<>();
        categories.add(0,"choose");
        categories.add("Food");
        categories.add("Grocery");
        categories.add("Rent");
        categories.add("MISC");


        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0,false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("choose")){

                }

                else{
                    String item = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(), "Selected:" +item , Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
      add.setOnClickListener(view -> addExpense() );


    }


    private void addExpense(){
        String uname = Objects.requireNonNull(name.getText().toString()) ;
        String uamount = Objects.requireNonNull(amount.getText().toString());
        double damount =000.00;
        if (TextUtils.isEmpty(uamount)) {
            amount.setError("Amount cannot be empty");
            amount.requestFocus();
        } else {
             damount = Double.valueOf(uamount);
        }
        String udescription =description.getText().toString() ;
        if (TextUtils.isEmpty(udescription)){
            udescription = "No description";
        }
        String category = Objects.requireNonNull(spinner.getSelectedItem().toString()) ;
        String sdate = Objects.requireNonNull(dateButton.getText().toString()) ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
        Date date = null;
        try {
            date = dateFormat.parse(sdate);
            System.out.println(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        System.out.println(uname+ uamount+ udescription + category + date );

        if (TextUtils.isEmpty(uname)){
            System.out.println("No name");
            name.setError("Name cannot be empty");
            name.requestFocus();

        } else if (TextUtils.isEmpty(sdate)){
            dateButton.setError("date cannot be empty");
            dateButton.requestFocus();
        }else if (category.equals("choose")){
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("Please select a category");
            spinner.requestFocus();
        } else{
            userID = mAuth.getCurrentUser().getUid();
            DatabaseReference expenseRef = database.getReference("users/" +userID+"/expenses" );
            Expense expense = new Expense(uname,damount,udescription,date,category);
            String expenseKey = expenseRef.push().getKey();
            expense.setId(expenseKey);

            expenseRef.child(expenseKey).setValue(expense).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Called Onsuccess");
                    Toast.makeText(AddExpense.this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddExpense.this,Overview.class));
                }
            });
        }

     }






    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month= month+ 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);

    }

    private void initDatepicker() {
        DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
       // int style = R.style.Theme_MaterialComponents_Light_Dialog_Alert;


        //int style = AlertDialog.THEME_HOLO_LIGHT;


        datePickerDialog = new DatePickerDialog(this,dateSetListener,year,month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month)+ " "+day+" "+year;
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
