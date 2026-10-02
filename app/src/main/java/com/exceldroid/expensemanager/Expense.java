package com.exceldroid.expensemanager;

public class Expense {

    private String id;
    private String title;
    private String category;
    private double amount;
    private String date;

    // Empty constructor — Firebase needs this to work, don't delete it
    public Expense() { }

    public Expense(String title, String category, double amount, String date) {
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
