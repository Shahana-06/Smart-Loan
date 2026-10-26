package com.smartloan.engine;

import com.smartloan.exception.InvalidLoanException;
import com.smartloan.model.Loan;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoanCalculator {
    public static double calculateEMI(Loan loan) throws InvalidLoanException {
        if (loan == null) {
            throw new InvalidLoanException("Loan details are required.");
        }
        if (!Double.isFinite(loan.getPrincipal()) || loan.getPrincipal() <= 0) {
            throw new InvalidLoanException("Principal must be greater than zero.");
        }
        if (!Double.isFinite(loan.getInterestRate()) || loan.getInterestRate() < 0) {
            throw new InvalidLoanException("Interest rate must be zero or greater.");
        }
        if (loan.getTenureMonths() <= 0) {
            throw new InvalidLoanException("Tenure must be greater than zero months.");
        }

        double principal = loan.getPrincipal();
        double monthlyRate = loan.getInterestRate() / 1200.0;
        int tenure = loan.getTenureMonths();
        if (monthlyRate == 0) {
            return principal / tenure;
        }
        double factor = Math.pow(1 + monthlyRate, tenure);
        return (principal * monthlyRate * factor) / (factor - 1);
    }

    public static List<Map<String, Double>> generateAmortizationSchedule(Loan loan)
            throws InvalidLoanException {
        List<Map<String, Double>> schedule = new ArrayList<>();
        double emi = calculateEMI(loan);
        double monthlyRate = loan.getInterestRate() / 1200.0;
        double balance = loan.getPrincipal();

        for (int month = 1; month <= loan.getTenureMonths() && balance > 0; month++) {
            double interest = balance * monthlyRate;
            double principalPaid = Math.min(balance, emi - interest);
            balance = Math.max(0, balance - principalPaid);

            Map<String, Double> row = new LinkedHashMap<>();
            row.put("month", (double) month);
            row.put("emi", principalPaid + interest);
            row.put("interest", interest);
            row.put("principal", principalPaid);
            row.put("balance", balance);
            schedule.add(row);
        }
        return schedule;
    }
}
