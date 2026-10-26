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

public class SimulatorServlet extends HttpServlet {
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

        FinancialIntelligenceService.SimulationResult sim = FinancialIntelligenceService.runSimulation(user, loans, 20.0, 0, 0, 0);

        request.setAttribute("user", user);
        request.setAttribute("loans", loans);
        request.setAttribute("sim", sim);
        request.setAttribute("salaryCut", 20.0);
        request.setAttribute("rateHike", 0.0);
        request.setAttribute("emergencyExpense", 0.0);

        request.getRequestDispatcher("/simulator.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");
        User user = userDAO.getUserById(sessionUser.getId());
        List<Loan> loans = loanDAO.getLoansByUser(user.getId());

        double salaryCut = 0;
        double rateHike = 0;
        double emergencyExpense = 0;

        try { salaryCut = Double.parseDouble(request.getParameter("salaryCut")); } catch(Exception ignored){}
        try { rateHike = Double.parseDouble(request.getParameter("rateHike")); } catch(Exception ignored){}
        try { emergencyExpense = Double.parseDouble(request.getParameter("emergencyExpense")); } catch(Exception ignored){}

        FinancialIntelligenceService.SimulationResult sim = FinancialIntelligenceService.runSimulation(user, loans, salaryCut, 0, emergencyExpense, rateHike);

        request.setAttribute("user", user);
        request.setAttribute("loans", loans);
        request.setAttribute("sim", sim);
        request.setAttribute("salaryCut", salaryCut);
        request.setAttribute("rateHike", rateHike);
        request.setAttribute("emergencyExpense", emergencyExpense);

        request.getRequestDispatcher("/simulator.jsp").forward(request, response);
    }
}
