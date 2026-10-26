package com.smartloan.engine;
import com.smartloan.model.Loan;
import java.util.*;

public interface RepaymentStrategy {
    void sortLoans(List<Loan> loans);
}

class HighestInterestFirst implements RepaymentStrategy {
    @Override
    public void sortLoans(List<Loan> loans) {
        loans.sort((l1, l2) -> Double.compare(l2.getInterestRate(), l1.getInterestRate()));
    }
}

class SmallestBalanceFirst implements RepaymentStrategy {
    @Override
    public void sortLoans(List<Loan> loans) {
        loans.sort(Comparator.comparingDouble(Loan::getPrincipal));
    }
}
