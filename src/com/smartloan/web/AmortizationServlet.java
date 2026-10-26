package com.smartloan.web;

import com.smartloan.dao.LoanDAO;
import com.smartloan.engine.LoanCalculator;
import com.smartloan.exception.InvalidLoanException;
import com.smartloan.model.Loan;
import com.smartloan.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AmortizationServlet extends HttpServlet {
    private final LoanDAO loanDAO = new LoanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String loanIdValue = request.getParameter("loanId");
        if (loanIdValue != null && !loanIdValue.trim().isEmpty()) {
            try {
                int loanId = Integer.parseInt(loanIdValue);
                User user = (User) session.getAttribute("user");
                List<Loan> loans = loanDAO.getLoansByUser(user.getId());
                Loan selectedLoan = loans.stream()
                    .filter(loan -> loan.getId() == loanId)
                    .findFirst()
                    .orElse(null);
                if (selectedLoan == null) {
                    request.setAttribute("error", "That loan could not be found.");
                } else {
                    List<Map<String, Double>> schedule =
                        LoanCalculator.generateAmortizationSchedule(selectedLoan);
                    request.setAttribute("loan", selectedLoan);
                    request.setAttribute("schedule", schedule);
                }
            } catch (NumberFormatException | InvalidLoanException e) {
                request.setAttribute("error", e.getMessage());
            }
        }
        request.getRequestDispatcher("/amortization.jsp").forward(request, response);
    }
}
