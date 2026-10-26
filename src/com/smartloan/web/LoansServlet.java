package com.smartloan.web;
import com.smartloan.dao.LoanDAO;
import com.smartloan.engine.LoanCalculator;
import com.smartloan.exception.InvalidLoanException;
import com.smartloan.model.Loan;
import com.smartloan.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class LoansServlet extends HttpServlet {
    private LoanDAO loanDAO = new LoanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        User u = (User) session.getAttribute("user");
        List<Loan> loans = loanDAO.getLoansByUser(u.getId());
        request.setAttribute("loans", loans);
        request.getRequestDispatcher("/loans.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        User u = (User) session.getAttribute("user");
        
        try {
            Loan loan = new Loan();
            loan.setUserId(u.getId());
            loan.setLoanType(request.getParameter("loanType"));
            loan.setPrincipal(Double.parseDouble(request.getParameter("principal")));
            loan.setInterestRate(Double.parseDouble(request.getParameter("interestRate")));
            loan.setTenureMonths(Integer.parseInt(request.getParameter("tenureMonths")));
            if (loan.getLoanType() == null || loan.getLoanType().trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Loan type is required.");
                return;
            }
            LoanCalculator.calculateEMI(loan);
            loanDAO.addLoan(loan);
            response.sendRedirect("loans");
        } catch (NumberFormatException | InvalidLoanException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ServletException("Could not save the loan.", e);
        }
    }
}
