package com.smartloan.model;

public class Loan {
    private int id;
    private int userId;
    private String loanType;
    private double principal;
    private double interestRate;
    private int tenureMonths;

    public Loan() {}

    public Loan(int id, int userId, String loanType, double principal, double interestRate, int tenureMonths) {
        this.id = id;
        this.userId = userId;
        this.loanType = loanType;
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public double getPrincipal() { return principal; }
    public void setPrincipal(double principal) { this.principal = principal; }
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public double calculateEMI() {
        if (principal <= 0 || tenureMonths <= 0) return 0;
        if (interestRate <= 0) return principal / tenureMonths;
        double r = interestRate / 1200.0;
        return (principal * r * Math.pow(1 + r, tenureMonths)) / (Math.pow(1 + r, tenureMonths) - 1);
    }

    public double calculateTotalInterest() {
        double emi = calculateEMI();
        return (emi * tenureMonths) - principal;
    }
}
