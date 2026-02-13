package com.example.model;

public class Expense {
    private int userId;
    private String description;
    private double amount;

    public Expense() {}

    public Expense(int userId, String description, double amount) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
    }

    public Expense(String description, double amount) {
        this(0, description, amount);
    }

        //Getters/Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    @Override
    public String toString() {
    return "Expense [userId=" + userId + ", description=" + description + ", amount=" + amount + "]";
    }
}