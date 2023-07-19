package com.uta.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PieChartActivity extends AppCompatActivity {
    PieChart pieChart;
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference expenseRef;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
         pieChart = findViewById(R.id.pieChart);

        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        int currMonth = calendar.get(Calendar.MONTH) +1 ;
        System.out.println("mont  and yr:" + currMonth + currYear);

        getSupportActionBar().setTitle( new DateFormatSymbols().getMonths()[currMonth - 1] + " expenses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        expenseRef = database.getReference("users/" + userID + "/expenses");
        displayPieChart(calendar,calendar);

    }

    private void displayPieChart(Calendar calendar1,Calendar calendar2){
        System.out.println(calendar1.get(Calendar.MONTH) +1);
        System.out.println(calendar2.get(Calendar.MONTH) +1) ;
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //DecimalFormat df = new DecimalFormat("0.00");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                HashMap<String, Float> Category = new HashMap<String, Float>();

                // Iterate through the DataSnapshot objects to extract the expenses
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                        Expense expense = expenseSnapshot.getValue(Expense.class);
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

                        if((calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)) && (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR))){
                            if ((expenseYear == calendar1.get(Calendar.YEAR)) && (expenseMonth == ( calendar1.get(Calendar.MONTH)) +1)) {
                                if (Category.containsKey(expense.category)) {
                                    Float amt = Category.get(expense.category);
                                    Category.put(expense.category, new Float(expense.amount + amt));
                                } else {
                                    Category.put(expense.category, new Float(expense.amount));
                                }
                                System.out.println(Category);
                            }
                        } else{
                            if ((expenseCal.compareTo(calendar1) >= 0 ) && (expenseCal.compareTo(calendar2) <=0)) {
                                if (Category.containsKey(expense.category)) {
                                    Float amt = Category.get(expense.category);
                                    Category.put(expense.category, new Float(expense.amount + amt));
                                } else {
                                    Category.put(expense.category, new Float(expense.amount));
                                }
                                System.out.println(Category);
                            }
                        }

//                        if ((expenseYear == calendar1.get(Calendar.YEAR)) && (expenseMonth == ( calendar1.get(Calendar.MONTH)) +1)) {
//                            if (Category.containsKey(expense.category)) {
//                                Float amt = Category.get(expense.category);
//                                Category.put(expense.category, new Float(expense.amount + amt));
//                            } else {
//                                Category.put(expense.category, new Float(expense.amount));
//                            }
//                            System.out.println(Category);
//                        }
                    }
                    if (!Category.containsKey("Food")) {
                        Category.put("Food", 0f);
                    }
                    if (!Category.containsKey("Rent")) {
                        Category.put("Rent", 0f);
                    }
                    if (!Category.containsKey("Grocery")) {
                        Category.put("Grocery", 0f);
                    }
                    if (!Category.containsKey("MISC")) {
                        Category.put("MISC", 0f);
                    }


                    ArrayList<PieEntry> entries = new ArrayList<>();
                    entries.add(new PieEntry(Category.get("Food"), "Food"));
                    entries.add(new PieEntry(Category.get("Rent"), "Rent"));
                    entries.add(new PieEntry(Category.get("Grocery"), "Grocery"));
                    entries.add(new PieEntry(Category.get("MISC"), "MISC"));


                    PieDataSet pieDataSet = new PieDataSet(entries, "Expenses by Category");
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    pieDataSet.setValueTextSize(20f);
                    PieData data = new PieData(pieDataSet);

                    pieChart.setData(data);
                    pieChart.getDescription().setEnabled(false);

                    pieChart.setCenterText("Categories");
                    pieChart.setCenterTextSize(22f);
                    pieChart.animateY(2000, Easing.EaseInOutQuad);
                    pieChart.invalidate();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", error.getMessage());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_daterange, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.daterange:
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Select a date range");

                CalendarConstraints.Builder constraint = new CalendarConstraints.Builder();
                constraint.setValidator(DateValidatorPointBackward.now());
                builder.setCalendarConstraints(constraint.build());
                final MaterialDatePicker materialDatePicker = builder.build();

                materialDatePicker.show(getSupportFragmentManager(),"Date_range_picker");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Long startDate = selection.first;
                        Long endDate = selection.second;
                        Calendar startCal = Calendar.getInstance();
                        startCal.setTimeInMillis(startDate);

                        Calendar endCal = Calendar.getInstance();
                        endCal.setTimeInMillis(endDate);
                      displayPieChart(startCal,endCal);
                    }
                });


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}