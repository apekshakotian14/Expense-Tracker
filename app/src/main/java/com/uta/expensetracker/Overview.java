package com.uta.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Overview extends AppCompatActivity {
    TextView totalAmount;
    TextView foodAmt;
    TextView rentAmt;
    TextView groceryAmt;
    TextView miscAmt;
    ImageButton addExpense;
    ImageButton dashboard;
    ImageButton charts;
    ImageButton history;
    ImageButton profile;
    CardView tot_exp;
    CardView food;
    CardView rent;
    CardView grocery;
    CardView misc;

    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference expReference;
    String userID;

private int lastMonth =-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        totalAmount = findViewById(R.id.textView8);
        foodAmt = findViewById(R.id.textView5);
        rentAmt = findViewById(R.id.textView10);
        groceryAmt = findViewById(R.id.textView12);
        miscAmt = findViewById(R.id.textView14);
        addExpense = findViewById(R.id.imageButton7);
        dashboard = findViewById(R.id.imageButton1);
        charts = findViewById(R.id.imageButton2);
        history = findViewById(R.id.imageButton3);
        profile = findViewById(R.id.imageButton4);
        tot_exp = findViewById(R.id.cardView1);
        food = findViewById(R.id.cardView2);
        rent = findViewById(R.id.cardView3);
        grocery =  findViewById(R.id.cardView4);
        misc = findViewById(R.id.cardView5);


        addExpense.setTooltipText("Click to Add Expense");

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        expReference = database.getReference("users/"+userID+"/expenses");

        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        int currMonth = calendar.get(Calendar.MONTH) +1 ;
        System.out.println("mont  and yr:" + currMonth + currYear);

        displayTotalAmount(currMonth,currYear);


        addExpense.setOnClickListener(view -> goToAddExpense() );
        charts.setOnClickListener(view -> goToCharts());
        history.setOnClickListener(view -> goToHistory());
        profile.setOnClickListener(view -> goToProfile());
        dashboard.setOnClickListener(view -> goToDashboard());




        food.setOnClickListener(view -> displayExpense("Food",currMonth,currYear));
        rent.setOnClickListener(view -> displayExpense("Rent",currMonth,currYear));
        grocery.setOnClickListener(view -> displayExpense("Grocery",currMonth,currYear));
        misc.setOnClickListener(view -> displayExpense("MISC",currMonth,currYear));
        tot_exp.setOnClickListener(view -> displayExpense(" ",currMonth,currYear));



    }


    public void displayTotalAmount(int currMonth, int currYear){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        expReference.addValueEventListener(new ValueEventListener() {
            double totalExpenses = 0.0;
            double totalFoodExpenses = 0.0;
            double totalRentExpenses = 0.0;
            double totalGroceryExpenses = 0.0;
            double totalMiscExpenses = 0.0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Calendar cal = Calendar.getInstance();
//                int lastMonth = (cal.get(Calendar.MONTH)+1) -1;

                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense != null){
                        Date expDate = null;
                        try {
                           expDate = dateFormat.parse(expense.getDate());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        Calendar expenseCal = Calendar.getInstance();
                        expenseCal.setTime(expDate);
                        int expenseYear = expenseCal.get(Calendar.YEAR);
                        int expenseMonth = expenseCal.get(Calendar.MONTH) + 1;
                        if((expenseYear == currYear) && (expenseMonth == currMonth)){
                            System.out.println("currExp:" + expense);
                            totalExpenses += expense.getAmount();
                            double expenseAmount = expense.getAmount();
                            String getCategory = expense.getCategory();
                            switch (getCategory){
                                case "Food":
                                    totalFoodExpenses += expenseAmount;
                                    break;
                                case "Rent":
                                    totalRentExpenses += expenseAmount;
                                    break;
                                case "Grocery":
                                    totalGroceryExpenses += expenseAmount;
                                    break;
                                case "MISC":
                                    totalMiscExpenses += expenseAmount;
                                    break;
                                case "default":
                                    break;

                            }
                        }

                    }
                }
               totalAmount.setText("$ "+String.format("%.2f", totalExpenses));
                foodAmt.setText("$ "+ String.format("%.2f", totalFoodExpenses));
                rentAmt.setText("$ "+ String.format("%.2f",totalRentExpenses));
                groceryAmt.setText("$ "+ String.format("%.2f",totalGroceryExpenses));
                miscAmt.setText("$ "+ String.format("%.2f",totalMiscExpenses));

                if (lastMonth != currMonth) {
                    Toast.makeText(getApplicationContext(), new DateFormatSymbols().getMonths()[currMonth - 1] + " month expenses", Toast.LENGTH_LONG).show();
                    lastMonth = currMonth;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("No amount to show, some error occurred");
            }
        });

    }

    private void displayExpense(String category,int currMonth, int currYear) {
        System.out.println("Category" + category);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        expReference.addValueEventListener(new ValueEventListener() {
            ArrayList<Expense> listOfExpenses = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChildren()){
                    for(DataSnapshot expSnapshot: snapshot.getChildren()) {
                        Expense expense = expSnapshot.getValue(Expense.class);
                        Date expDate = null;
                        try {
                            expDate = dateFormat.parse(expense.getDate());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        Calendar expenseCal = Calendar.getInstance();
                        expenseCal.setTime(expDate);
                        int expenseYear = expenseCal.get(Calendar.YEAR);
                        int expenseMonth = expenseCal.get(Calendar.MONTH) + 1;

                          if ((expenseYear == currYear) && (expenseMonth == currMonth)) {
                              if (category.equals(" ")) {
                                  listOfExpenses.add(expense);
                              } else if (expense.getCategory().equals(category)) {
                                listOfExpenses.add(expense);
                                }
                            }
                        }
                    if (listOfExpenses == null || listOfExpenses.isEmpty()){
                        Intent intent = new Intent(Overview.this,History.class);
                        intent.putExtra("flag",true);
                        startActivity(intent);
                        //startActivity(new Intent(Overview.this,History.class));
                    } else{
                        Intent intent = new Intent(Overview.this,History.class);
                        intent.putExtra("expenses",listOfExpenses);
                        startActivity(intent);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Overview.this, "No relevant data found", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void goToDashboard(){
        startActivity(new Intent(Overview.this,Overview.class));
    }

    public void goToAddExpense(){
        startActivity(new Intent(Overview.this,AddExpense.class));
    }

    public void goToCharts(){
        startActivity(new Intent(Overview.this,Charts.class));
    }

    public void goToHistory(){
        startActivity(new Intent(Overview.this,History.class));
    }
    public void goToProfile(){
        startActivity(new Intent(Overview.this,Profile.class));
    }

}