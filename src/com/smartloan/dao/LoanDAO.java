package com.smartloan.dao;

import com.smartloan.model.Loan;
import com.smartloan.util.DatabaseUtil;
import java.sql.*;
import java.util.*;

public class LoanDAO {
    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (user_id, loan_type, principal, interest_rate, tenure_months) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loan.getUserId());
            ps.setString(2, loan.getLoanType());
            ps.setDouble(3, loan.getPrincipal());
            ps.setDouble(4, loan.getInterestRate());
            ps.setInt(5, loan.getTenureMonths());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save the loan.", e);
        }
    }

    public List<Loan> getLoansByUser(int userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT id, user_id, loan_type, principal, interest_rate, tenure_months FROM loans WHERE user_id = ? ORDER BY id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Loan loan = new Loan();
                    loan.setId(rs.getInt("id"));
                    loan.setUserId(rs.getInt("user_id"));
                    loan.setLoanType(rs.getString("loan_type"));
                    loan.setPrincipal(rs.getDouble("principal"));
                    loan.setInterestRate(rs.getDouble("interest_rate"));
                    loan.setTenureMonths(rs.getInt("tenure_months"));
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load loans for user " + userId + ".", e);
        }
        return loans;
    }
}
