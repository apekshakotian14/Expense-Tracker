package com.uta.expensetracker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Expense implements Serializable, Comparable<Expense> {
    public String name;
    public String category;
    public Double amount;

    public String description;

    //public Date date;
   // SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

  public String date;

  public String id;


  public Expense(){
    id ="";
  }

    public Expense(String name,Double amount,String description,Date date, String category ){
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date);
        this.category = category;
        this.id = "";

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(Expense expense) {
        //return this.date.compareTo(expense.getDate());
        return expense.getDate().compareTo(this.date);

    }
}
