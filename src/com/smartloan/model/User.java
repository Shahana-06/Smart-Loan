package com.smartloan.model;

public class User {
    private int id;
    private String username;
    private String password;
    private double income;
    private double expenses;
    private double emergencyReserve;

    public User() {}

    public User(int id, String username, String password, double income, double expenses, double emergencyReserve) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.income = income;
        this.expenses = expenses;
        this.emergencyReserve = emergencyReserve;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public double getIncome() { return income; }
    public void setIncome(double income) { this.income = income; }
    public double getExpenses() { return expenses; }
    public void setExpenses(double expenses) { this.expenses = expenses; }
    public double getEmergencyReserve() { return emergencyReserve; }
    public void setEmergencyReserve(double emergencyReserve) { this.emergencyReserve = emergencyReserve; }
}
