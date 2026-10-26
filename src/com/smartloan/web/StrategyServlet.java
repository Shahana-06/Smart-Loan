package com.smartloan.web;

import com.smartloan.dao.LoanDAO;
import com.smartloan.dao.UserDAO;
import com.smartloan.engine.FinancialIntelligenceService;
import com.smartloan.model.Loan;
import com.smartloan.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class StrategyServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private LoanDAO loanDAO = new LoanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");
        User user = userDAO.getUserById(sessionUser.getId());
        List<Loan> loans = loanDAO.getLoansByUser(user.getId());

        double defaultSurplus = Math.max(0, user.getIncome() - user.getExpenses() - FinancialIntelligenceService.getTotalMonthlyEMI(loans));
        FinancialIntelligenceService.PrepaymentOptimizationPlan plan = FinancialIntelligenceService.evaluatePrepayment(user, loans, defaultSurplus);

        request.setAttribute("user", user);
        request.setAttribute("loans", loans);
        request.setAttribute("plan", plan);
        request.getRequestDispatcher("/strategy.jsp").forward(request, response);
    }
}
