package com.smartloan.engine;

import com.smartloan.model.Loan;
import com.smartloan.model.User;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class FinancialIntelligenceService {

    public static double getTotalDebt(List<Loan> loans) {
        double total = 0;
        if (loans != null) {
            for (Loan l : loans) total += l.getPrincipal();
        }
        return total;
    }

    public static double getTotalMonthlyEMI(List<Loan> loans) {
        double total = 0;
        if (loans != null) {
            for (Loan l : loans) total += l.calculateEMI();
        }
        return total;
    }

    public static class AmortizationResult {
        private int monthsTaken;
        private double totalInterestPaid;
        private double totalAmountPaid;
        private double interestSaved;
        private int monthsSaved;

        public AmortizationResult(int monthsTaken, double totalInterestPaid, double totalAmountPaid, double interestSaved, int monthsSaved) {
            this.monthsTaken = monthsTaken;
            this.totalInterestPaid = totalInterestPaid;
            this.totalAmountPaid = totalAmountPaid;
            this.interestSaved = interestSaved;
            this.monthsSaved = monthsSaved;
        }

        public int getMonthsTaken() { return monthsTaken; }
        public double getTotalInterestPaid() { return totalInterestPaid; }
        public double getTotalAmountPaid() { return totalAmountPaid; }
        public double getInterestSaved() { return interestSaved; }
        public int getMonthsSaved() { return monthsSaved; }
    }

    public static AmortizationResult calculatePrepaymentBenefit(Loan loan, double extraMonthlyPayment) {
        double principal = loan.getPrincipal();
        double annualRate = loan.getInterestRate();
        int originalTenure = loan.getTenureMonths();
        double baseEMI = loan.calculateEMI();
        double baseTotalInterest = (baseEMI * originalTenure) - principal;

        if (extraMonthlyPayment <= 0) {
            return new AmortizationResult(originalTenure, baseTotalInterest, baseEMI * originalTenure, 0, 0);
        }

        double r = annualRate / 1200.0;
        double balance = principal;
        double totalInterest = 0;
        int months = 0;

        while (balance > 0.01 && months < 600) {
            months++;
            double interestForMonth = (r > 0) ? (balance * r) : 0;
            double regularPrincipal = (r > 0) ? (baseEMI - interestForMonth) : (principal / originalTenure);
            double totalPrincipalPaid = regularPrincipal + extraMonthlyPayment;

            if (totalPrincipalPaid > balance) {
                totalPrincipalPaid = balance;
                interestForMonth = (r > 0) ? (balance * r) : 0;
                totalInterest += interestForMonth;
                balance = 0;
                break;
            }

            totalInterest += interestForMonth;
            balance -= totalPrincipalPaid;
        }

        double totalPaid = principal + totalInterest;
        double interestSaved = Math.max(0, baseTotalInterest - totalInterest);
        int monthsSaved = Math.max(0, originalTenure - months);

        return new AmortizationResult(months, totalInterest, totalPaid, interestSaved, monthsSaved);
    }

    public static class PrepaymentOptimizationPlan {
        private Loan recommendedLoan;
        private double proposedAmount;
        private double safeSurplus;
        private double emergencyReserveRetained;
        private double reserveCoverageMonths;
        private double interestSavings;
        private int tenureMonthsSaved;
        private String priorityReason;
        private String whyThisLoan;
        private String alternativeComparison;
        private List<StrategyComparisonItem> strategyComparisons = new ArrayList<>();

        public Loan getRecommendedLoan() { return recommendedLoan; }
        public void setRecommendedLoan(Loan recommendedLoan) { this.recommendedLoan = recommendedLoan; }

        public double getProposedAmount() { return proposedAmount; }
        public void setProposedAmount(double proposedAmount) { this.proposedAmount = proposedAmount; }

        public double getSafeSurplus() { return safeSurplus; }
        public void setSafeSurplus(double safeSurplus) { this.safeSurplus = safeSurplus; }

        public double getEmergencyReserveRetained() { return emergencyReserveRetained; }
        public void setEmergencyReserveRetained(double emergencyReserveRetained) { this.emergencyReserveRetained = emergencyReserveRetained; }

        public double getReserveCoverageMonths() { return reserveCoverageMonths; }
        public void setReserveCoverageMonths(double reserveCoverageMonths) { this.reserveCoverageMonths = reserveCoverageMonths; }

        public double getInterestSavings() { return interestSavings; }
        public void setInterestSavings(double interestSavings) { this.interestSavings = interestSavings; }

        public int getTenureMonthsSaved() { return tenureMonthsSaved; }
        public void setTenureMonthsSaved(int tenureMonthsSaved) { this.tenureMonthsSaved = tenureMonthsSaved; }

        public String getPriorityReason() { return priorityReason; }
        public void setPriorityReason(String priorityReason) { this.priorityReason = priorityReason; }

        public String getWhyThisLoan() { return whyThisLoan; }
        public void setWhyThisLoan(String whyThisLoan) { this.whyThisLoan = whyThisLoan; }

        public String getAlternativeComparison() { return alternativeComparison; }
        public void setAlternativeComparison(String alternativeComparison) { this.alternativeComparison = alternativeComparison; }

        public List<StrategyComparisonItem> getStrategyComparisons() { return strategyComparisons; }
        public void setStrategyComparisons(List<StrategyComparisonItem> strategyComparisons) { this.strategyComparisons = strategyComparisons; }
    }

    public static class StrategyComparisonItem {
        private String strategyName;
        private String targetLoanName;
        private double interestSaved;
        private int monthsSaved;
        private double remainingTotalInterest;
        private String pros;
        private String cons;
        private boolean recommended;

        public StrategyComparisonItem(String strategyName, String targetLoanName, double interestSaved, int monthsSaved, double remainingTotalInterest, String pros, String cons, boolean recommended) {
            this.strategyName = strategyName;
            this.targetLoanName = targetLoanName;
            this.interestSaved = interestSaved;
            this.monthsSaved = monthsSaved;
            this.remainingTotalInterest = remainingTotalInterest;
            this.pros = pros;
            this.cons = cons;
            this.recommended = recommended;
        }

        public String getStrategyName() { return strategyName; }
        public String getTargetLoanName() { return targetLoanName; }
        public double getInterestSaved() { return interestSaved; }
        public int getMonthsSaved() { return monthsSaved; }
        public double getRemainingTotalInterest() { return remainingTotalInterest; }
        public String getPros() { return pros; }
        public String getCons() { return cons; }
        public boolean isRecommended() { return recommended; }
        public boolean getRecommended() { return recommended; }
        public boolean getIsRecommended() { return recommended; }
    }

    public static PrepaymentOptimizationPlan evaluatePrepayment(User user, List<Loan> loans, double requestedSurplus) {
        return evaluatePrepayment(user, loans, requestedSurplus, ForkJoinPool.commonPool());
    }

    public static PrepaymentOptimizationPlan evaluatePrepayment(
            User user, List<Loan> loans, double requestedSurplus, Executor strategyExecutor) {
        Objects.requireNonNull(strategyExecutor, "strategyExecutor");
        PrepaymentOptimizationPlan plan = new PrepaymentOptimizationPlan();

        double income = (user != null) ? user.getIncome() : 0;
        double expenses = (user != null) ? user.getExpenses() : 0;
        double totalEMI = getTotalMonthlyEMI(loans);
        double emergencyReserve = (user != null) ? user.getEmergencyReserve() : 0;

        double monthlyFreeCash = Math.max(0, income - expenses - totalEMI);
        double actualSurplusToUse = requestedSurplus > 0 ? requestedSurplus : monthlyFreeCash;
        plan.setSafeSurplus(actualSurplusToUse);
        plan.setEmergencyReserveRetained(emergencyReserve);
        plan.setReserveCoverageMonths(expenses > 0 ? (emergencyReserve / expenses) : 0);

        if (loans == null || loans.isEmpty()) {
            plan.setWhyThisLoan("You do not have any active loans recorded. Add your loans to generate an intelligent prepayment plan.");
            return plan;
        }

        List<Loan> avalancheList = new ArrayList<>(loans);
        avalancheList.sort((l1, l2) -> Double.compare(l2.getInterestRate(), l1.getInterestRate()));
        Loan highestInterestLoan = avalancheList.get(0);

        List<Loan> snowballList = new ArrayList<>(loans);
        snowballList.sort(Comparator.comparingDouble(Loan::getPrincipal));
        Loan smallestBalanceLoan = snowballList.get(0);

        CompletableFuture<StrategyComparisonItem> avalancheTask = CompletableFuture.supplyAsync(
            () -> calculateStrategyComparison(
                highestInterestLoan, actualSurplusToUse, "Debt Avalanche (Recommended)", true
            ),
            strategyExecutor
        );
        CompletableFuture<StrategyComparisonItem> snowballTask = CompletableFuture.supplyAsync(
            () -> calculateStrategyComparison(
                smallestBalanceLoan, actualSurplusToUse, "Debt Snowball", false
            ),
            strategyExecutor
        );
        StrategyComparisonItem avalancheResult = avalancheTask.join();
        StrategyComparisonItem snowballResult = snowballTask.join();

        plan.setRecommendedLoan(highestInterestLoan);
        plan.setProposedAmount(actualSurplusToUse);
        plan.setInterestSavings(avalancheResult.getInterestSaved());
        plan.setTenureMonthsSaved(avalancheResult.getMonthsSaved());

        StringBuilder why = new StringBuilder();
        why.append("We recommend allocating ₹").append(String.format("%,.2f", actualSurplusToUse))
           .append(" extra every month toward your '").append(highestInterestLoan.getLoanType())
           .append(" (ID: ").append(highestInterestLoan.getId()).append(")' because it carries your highest annual interest rate of ")
           .append(highestInterestLoan.getInterestRate()).append("%.\n\n")
           .append("• Why this loan: In lending mathematics, every extra Rupee applied to a higher-interest loan prevents interest from compounding at the fastest rate. ")
           .append("This saves you ₹").append(String.format("%,.2f", avalancheResult.getInterestSaved()))
           .append(" in total interest charges and clears this debt ").append(avalancheResult.getMonthsSaved()).append(" months earlier.\n")
           .append("• Impact on liquidity: Your emergency fund of ₹").append(String.format("%,.2f", emergencyReserve))
           .append(" is preserved intact, maintaining ").append(String.format("%.1f", plan.getReserveCoverageMonths()))
           .append(" months of essential expense coverage.\n");

        if (loans.size() > 1 && !highestInterestLoan.equals(smallestBalanceLoan)) {
            why.append("• Why not '").append(smallestBalanceLoan.getLoanType()).append(" (ID: ").append(smallestBalanceLoan.getId()).append(")': ")
               .append("Although it has the smallest balance (₹").append(String.format("%,.2f", smallestBalanceLoan.getPrincipal()))
               .append("), its interest rate is ").append(smallestBalanceLoan.getInterestRate()).append("% vs ").append(highestInterestLoan.getInterestRate())
               .append("%. Paying it first (Snowball method) would save ₹").append(String.format("%,.2f", snowballResult.getInterestSaved()))
               .append(" in interest—which is ₹").append(String.format("%,.2f", (avalancheResult.getInterestSaved() - snowballResult.getInterestSaved())))
               .append(" less total savings than Avalanche.");
        }

        plan.setWhyThisLoan(why.toString());

        List<StrategyComparisonItem> comparisons = new ArrayList<>();
        comparisons.add(avalancheResult);
        comparisons.add(snowballResult);

        comparisons.add(new StrategyComparisonItem(
            "Balanced Strategy (50% Prepay / 50% Emergency Reserve)",
            highestInterestLoan.getLoanType(),
            avalancheResult.getInterestSaved() * 0.55,
            (int)(avalancheResult.getMonthsSaved() * 0.6),
            avalancheResult.getRemainingTotalInterest() + (avalancheResult.getInterestSaved() * 0.45),
            "Builds emergency liquidity while still chipping away at expensive debt.",
            "Moderate interest savings and longer tenure than pure Avalanche.",
            false
        ));

        comparisons.add(new StrategyComparisonItem(
            "Minimum Payment Baseline",
            "None (Contractual EMIs only)",
            0.0,
            0,
            getTotalMonthlyEMI(loans) * 12 * 5,
            "Zero extra strain on immediate monthly cash flow.",
            "Maximum interest paid over time; maximum repayment tenure.",
            false
        ));

        plan.setStrategyComparisons(comparisons);
        return plan;
    }

    private static StrategyComparisonItem calculateStrategyComparison(
            Loan targetLoan, double extraMonthlyPayment, String strategyName, boolean recommended) {
        AmortizationResult result = calculatePrepaymentBenefit(targetLoan, extraMonthlyPayment);
        String targetName = recommended
            ? targetLoan.getLoanType() + " (" + targetLoan.getInterestRate() + "%)"
            : targetLoan.getLoanType() + " (₹" + String.format("%,.0f", targetLoan.getPrincipal()) + ")";
        return new StrategyComparisonItem(
            strategyName,
            targetName,
            result.getInterestSaved(),
            result.getMonthsSaved(),
            result.getTotalInterestPaid(),
            recommended
                ? "Maximizes interest savings by prioritizing the highest-rate loan."
                : "Builds momentum by paying off the smallest-balance loan first.",
            recommended
                ? "May take longer to clear a single loan if it has a large balance."
                : "May pay more interest when the smallest loan has a lower rate.",
            recommended
        );
    }

    public static class RiskEvaluation {
        private int score;
        private String category;
        private double dtiRatio;
        private double emiToIncomeRatio;
        private double reserveCoverageMonths;
        private double freeCashFlow;
        private List<String> protectiveFactors = new ArrayList<>();
        private List<String> riskFactors = new ArrayList<>();
        private List<String> recommendations = new ArrayList<>();
        private String plainEnglishSummary;

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public double getDtiRatio() { return dtiRatio; }
        public void setDtiRatio(double dtiRatio) { this.dtiRatio = dtiRatio; }

        public double getEmiToIncomeRatio() { return emiToIncomeRatio; }
        public void setEmiToIncomeRatio(double emiToIncomeRatio) { this.emiToIncomeRatio = emiToIncomeRatio; }

        public double getReserveCoverageMonths() { return reserveCoverageMonths; }
        public void setReserveCoverageMonths(double reserveCoverageMonths) { this.reserveCoverageMonths = reserveCoverageMonths; }

        public double getFreeCashFlow() { return freeCashFlow; }
        public void setFreeCashFlow(double freeCashFlow) { this.freeCashFlow = freeCashFlow; }

        public List<String> getProtectiveFactors() { return protectiveFactors; }
        public void setProtectiveFactors(List<String> protectiveFactors) { this.protectiveFactors = protectiveFactors; }

        public List<String> getRiskFactors() { return riskFactors; }
        public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }

        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

        public String getPlainEnglishSummary() { return plainEnglishSummary; }
        public void setPlainEnglishSummary(String plainEnglishSummary) { this.plainEnglishSummary = plainEnglishSummary; }
    }

    public static RiskEvaluation evaluateRisk(User user, List<Loan> loans) {
        RiskEvaluation eval = new RiskEvaluation();
        double income = (user != null) ? user.getIncome() : 0;
        double expenses = (user != null) ? user.getExpenses() : 0;
        double totalDebt = getTotalDebt(loans);
        double totalEMI = getTotalMonthlyEMI(loans);
        double emergencyReserve = (user != null) ? user.getEmergencyReserve() : 0;

        eval.setFreeCashFlow(income - expenses - totalEMI);
        eval.setEmiToIncomeRatio((income > 0) ? (totalEMI / income) * 100 : 0);
        eval.setDtiRatio((income > 0) ? (totalDebt / (income * 12)) * 100 : 0);
        eval.setReserveCoverageMonths((expenses > 0) ? (emergencyReserve / expenses) : 0);

        int score = 100;

        if (eval.getEmiToIncomeRatio() > 50) {
            score -= 35;
            eval.getRiskFactors().add("Critical EMI Burden: Your monthly EMIs consume " + String.format("%.1f", eval.getEmiToIncomeRatio()) + "% of your net income (recommended < 35%).");
        } else if (eval.getEmiToIncomeRatio() > 35) {
            score -= 20;
            eval.getRiskFactors().add("Elevated EMI Burden: Your monthly EMIs take " + String.format("%.1f", eval.getEmiToIncomeRatio()) + "% of your net income.");
        } else {
            eval.getProtectiveFactors().add("Safe EMI Ratio: Your monthly EMIs take only " + String.format("%.1f", eval.getEmiToIncomeRatio()) + "% of income.");
        }

        if (eval.getReserveCoverageMonths() < 1.0) {
            score -= 30;
            eval.getRiskFactors().add("Vulnerable Liquidity: Your emergency reserve covers only " + String.format("%.1f", eval.getReserveCoverageMonths()) + " months of living expenses (recommended: 3-6 months).");
        } else if (eval.getReserveCoverageMonths() < 3.0) {
            score -= 15;
            eval.getRiskFactors().add("Moderate Emergency Buffer: Emergency reserve covers " + String.format("%.1f", eval.getReserveCoverageMonths()) + " months.");
        } else {
            eval.getProtectiveFactors().add("Strong Safety Cushion: Emergency savings cover " + String.format("%.1f", eval.getReserveCoverageMonths()) + " months of essential expenses.");
        }

        if (eval.getFreeCashFlow() < 0) {
            score -= 35;
            eval.getRiskFactors().add("Monthly Cash Deficit: You are spending ₹" + String.format("%,.2f", Math.abs(eval.getFreeCashFlow())) + " more each month than you earn!");
        } else if (eval.getFreeCashFlow() < 5000) {
            score -= 10;
            eval.getRiskFactors().add("Tight Monthly Cushion: Free cash flow is only ₹" + String.format("%,.2f", eval.getFreeCashFlow()) + " per month.");
        } else {
            eval.getProtectiveFactors().add("Healthy Positive Cash Flow: You retain ₹" + String.format("%,.2f", eval.getFreeCashFlow()) + " surplus every month.");
        }

        if (score < 0) score = 0;
        eval.setScore(score);

        if (score >= 80) {
            eval.setCategory("Low Risk (Healthy & Resilient)");
        } else if (score >= 60) {
            eval.setCategory("Moderate Risk (Manageable with Care)");
        } else if (score >= 40) {
            eval.setCategory("High Risk (Vulnerable to Shocks)");
        } else {
            eval.setCategory("Critical Risk (Immediate Action Required)");
        }

        if (eval.getReserveCoverageMonths() < 3) {
            eval.getRecommendations().add("Build your emergency reserve to at least ₹" + String.format("%,.0f", expenses * 3) + " before initiating aggressive loan prepayments.");
        }
        if (eval.getFreeCashFlow() > 5000 && !loans.isEmpty()) {
            eval.getRecommendations().add("Direct a portion of your ₹" + String.format("%,.0f", eval.getFreeCashFlow()) + " monthly surplus toward the highest-interest loan using Debt Avalanche.");
        }
        if (eval.getEmiToIncomeRatio() > 40) {
            eval.getRecommendations().add("Avoid taking on any new loans or credit card debt until your EMI burden drops below 35%.");
        }
        if (eval.getRecommendations().isEmpty()) {
            eval.getRecommendations().add("Maintain your current repayment discipline and consider investing surplus cash flow.");
        }

        eval.setPlainEnglishSummary("Your financial health is currently rated at " + eval.getScore() + "/100 (" + eval.getCategory() + "). "
            + "Out of every ₹100 you take home, ₹" + String.format("%.0f", eval.getEmiToIncomeRatio()) + " is locked into loan EMIs and ₹"
            + String.format("%.0f", (expenses / (income > 0 ? income : 1)) * 100) + " goes toward essential expenses, leaving ₹"
            + String.format("%.0f", (eval.getFreeCashFlow() / (income > 0 ? income : 1)) * 100) + " as uncommitted surplus.");

        return eval;
    }

    public static class SimulationResult {
        private double originalIncome;
        private double simulatedIncome;
        private double originalCashFlow;
        private double simulatedCashFlow;
        private double simulatedDeficit;
        private double emergencyReserve;
        private double monthsUntilDepletion;
        private double simulatedTotalEMI;
        private double simulatedEMIBurdenPercent;
        private String narrativeAnalysis;
        private String actionRecommendation;

        public double getOriginalIncome() { return originalIncome; }
        public void setOriginalIncome(double originalIncome) { this.originalIncome = originalIncome; }

        public double getSimulatedIncome() { return simulatedIncome; }
        public void setSimulatedIncome(double simulatedIncome) { this.simulatedIncome = simulatedIncome; }

        public double getOriginalCashFlow() { return originalCashFlow; }
        public void setOriginalCashFlow(double originalCashFlow) { this.originalCashFlow = originalCashFlow; }

        public double getSimulatedCashFlow() { return simulatedCashFlow; }
        public void setSimulatedCashFlow(double simulatedCashFlow) { this.simulatedCashFlow = simulatedCashFlow; }

        public double getSimulatedDeficit() { return simulatedDeficit; }
        public void setSimulatedDeficit(double simulatedDeficit) { this.simulatedDeficit = simulatedDeficit; }

        public double getEmergencyReserve() { return emergencyReserve; }
        public void setEmergencyReserve(double emergencyReserve) { this.emergencyReserve = emergencyReserve; }

        public double getMonthsUntilDepletion() { return monthsUntilDepletion; }
        public void setMonthsUntilDepletion(double monthsUntilDepletion) { this.monthsUntilDepletion = monthsUntilDepletion; }

        public double getSimulatedTotalEMI() { return simulatedTotalEMI; }
        public void setSimulatedTotalEMI(double simulatedTotalEMI) { this.simulatedTotalEMI = simulatedTotalEMI; }

        public double getSimulatedEMIBurdenPercent() { return simulatedEMIBurdenPercent; }
        public void setSimulatedEMIBurdenPercent(double simulatedEMIBurdenPercent) { this.simulatedEMIBurdenPercent = simulatedEMIBurdenPercent; }

        public String getNarrativeAnalysis() { return narrativeAnalysis; }
        public void setNarrativeAnalysis(String narrativeAnalysis) { this.narrativeAnalysis = narrativeAnalysis; }

        public String getActionRecommendation() { return actionRecommendation; }
        public void setActionRecommendation(String actionRecommendation) { this.actionRecommendation = actionRecommendation; }
    }

    public static SimulationResult runSimulation(User user, List<Loan> loans, double salaryCutPercent, int jobLossMonths, double emergencyExpense, double rateHikePercent) {
        SimulationResult res = new SimulationResult();
        double baseIncome = (user != null) ? user.getIncome() : 0;
        double baseExpenses = (user != null) ? user.getExpenses() : 0;
        double baseReserve = (user != null) ? user.getEmergencyReserve() : 0;
        double totalEMI = getTotalMonthlyEMI(loans);

        res.setOriginalIncome(baseIncome);
        res.setOriginalCashFlow(baseIncome - baseExpenses - totalEMI);
        res.setEmergencyReserve(Math.max(0, baseReserve - emergencyExpense));

        double incomeAfterCut = baseIncome * (1.0 - (salaryCutPercent / 100.0));
        if (jobLossMonths > 0) {
            incomeAfterCut = 0;
        }
        res.setSimulatedIncome(incomeAfterCut);

        double simEMI = 0;
        if (loans != null) {
            for (Loan l : loans) {
                Loan simLoan = new Loan(l.getId(), l.getUserId(), l.getLoanType(), l.getPrincipal(), l.getInterestRate() + rateHikePercent, l.getTenureMonths());
                simEMI += simLoan.calculateEMI();
            }
        }
        res.setSimulatedTotalEMI(simEMI);
        res.setSimulatedCashFlow(res.getSimulatedIncome() - baseExpenses - simEMI);

        if (res.getSimulatedCashFlow() < 0) {
            res.setSimulatedDeficit(Math.abs(res.getSimulatedCashFlow()));
            res.setMonthsUntilDepletion((res.getSimulatedDeficit() > 0) ? (res.getEmergencyReserve() / res.getSimulatedDeficit()) : 0);
        } else {
            res.setSimulatedDeficit(0);
            res.setMonthsUntilDepletion(999);
        }

        res.setSimulatedEMIBurdenPercent((res.getSimulatedIncome() > 0) ? (simEMI / res.getSimulatedIncome()) * 100 : 100);

        StringBuilder sb = new StringBuilder();
        sb.append("Under this scenario:\n");
        if (salaryCutPercent > 0) {
            sb.append("• Salary drops by ").append(salaryCutPercent).append("%, reducing monthly income from ₹")
              .append(String.format("%,.2f", baseIncome)).append(" to ₹").append(String.format("%,.2f", res.getSimulatedIncome())).append(".\n");
        }
        if (rateHikePercent > 0) {
            sb.append("• Interest rate increases by ").append(rateHikePercent).append("%, raising total monthly EMI burden from ₹")
              .append(String.format("%,.2f", totalEMI)).append(" to ₹").append(String.format("%,.2f", simEMI)).append(".\n");
        }
        if (emergencyExpense > 0) {
            sb.append("• A one-time unexpected expense of ₹").append(String.format("%,.2f", emergencyExpense))
              .append(" reduces your emergency reserves to ₹").append(String.format("%,.2f", res.getEmergencyReserve())).append(".\n");
        }

        if (res.getSimulatedCashFlow() < 0) {
            sb.append("• Resulting Impact: Your cash flow turns negative with a monthly shortfall of ₹")
              .append(String.format("%,.2f", res.getSimulatedDeficit())).append(". ");
            if (res.getMonthsUntilDepletion() < 999) {
                sb.append("At this burn rate, your remaining emergency reserve will be completely depleted in approximately ")
                  .append(String.format("%.1f", res.getMonthsUntilDepletion())).append(" months.");
            }
            res.setActionRecommendation("Action Plan: Halt extra loan prepayments immediately. Trim discretionary spending and speak to your lender about tenure extensions if this stress lasts longer than " + String.format("%.0f", res.getMonthsUntilDepletion()) + " months.");
        } else {
            sb.append("• Resulting Impact: Your finances remain solvent with a positive monthly surplus of ₹")
              .append(String.format("%,.2f", res.getSimulatedCashFlow())).append(". Your emergency fund remains intact.");
            res.setActionRecommendation("Action Plan: Your financial buffer can comfortably absorb this shock without risking loan default.");
        }

        res.setNarrativeAnalysis(sb.toString());
        return res;
    }
}
