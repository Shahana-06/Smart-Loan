package com.smartloan.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.smartloan.exception.InvalidLoanException;
import com.smartloan.model.Loan;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LoanCalculatorTest {
    @Test
    void calculatesEmiForZeroInterest() throws InvalidLoanException {
        Loan loan = new Loan(0, 1, "Personal", 1200, 0, 12);

        assertEquals(100, LoanCalculator.calculateEMI(loan), 0.000001);
    }

    @Test
    void calculatesEmiForInterestBearingLoan() throws InvalidLoanException {
        Loan loan = new Loan(0, 1, "Home", 100000, 12, 12);

        assertEquals(8884.8789, LoanCalculator.calculateEMI(loan), 0.01);
    }

    @Test
    void rejectsInvalidLoanTerms() {
        Loan loan = new Loan(0, 1, "Personal", 1000, 5, 0);

        assertThrows(InvalidLoanException.class, () -> LoanCalculator.calculateEMI(loan));
    }

    @Test
    void amortizationSchedulePaysOffLoan() throws InvalidLoanException {
        Loan loan = new Loan(0, 1, "Personal", 1200, 0, 12);

        List<Map<String, Double>> schedule = LoanCalculator.generateAmortizationSchedule(loan);

        assertEquals(12, schedule.size());
        assertEquals(0, schedule.get(schedule.size() - 1).get("balance"), 0.000001);
        assertEquals(100, schedule.get(0).get("principal"), 0.000001);
    }
}
