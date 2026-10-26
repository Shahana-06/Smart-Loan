package com.smartloan.engine;
public class RiskAnalyzer {
    public static String assessRisk(double debt, double income) {
        if (income == 0) return "HIGH RISK";
        double ratio = debt / income;
        if(ratio > 0.5) return "HIGH RISK";
        if(ratio > 0.3) return "MEDIUM RISK";
        return "LOW RISK (Estimate)";
    }
}
