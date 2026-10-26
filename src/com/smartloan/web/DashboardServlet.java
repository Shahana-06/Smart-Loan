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

public class DashboardServlet extends HttpServlet {
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

        double totalDebt = FinancialIntelligenceService.getTotalDebt(loans);
        double totalEMI = FinancialIntelligenceService.getTotalMonthlyEMI(loans);
        double freeCashFlow = Math.max(0, user.getIncome() - user.getExpenses() - totalEMI);
        FinancialIntelligenceService.RiskEvaluation risk = FinancialIntelligenceService.evaluateRisk(user, loans);

        request.setAttribute("user", user);
        request.setAttribute("loans", loans);
        request.setAttribute("totalDebt", totalDebt);
        request.setAttribute("totalEMI", totalEMI);
        request.setAttribute("freeCashFlow", freeCashFlow);
        request.setAttribute("risk", risk);

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
