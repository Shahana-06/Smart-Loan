package com.smartloan.web;
import com.smartloan.engine.LoanCalculator;
import com.smartloan.exception.InvalidLoanException;
import com.smartloan.model.Loan;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

public class CalculatorServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Loan loan = new Loan();
            loan.setPrincipal(Double.parseDouble(request.getParameter("principal")));
            loan.setInterestRate(Double.parseDouble(request.getParameter("interestRate")));
            loan.setTenureMonths(Integer.parseInt(request.getParameter("tenureMonths")));
            
            double emi = LoanCalculator.calculateEMI(loan);
            request.setAttribute("emiResult", String.format("%.2f", emi));
        } catch (NumberFormatException | InvalidLoanException e) {
            request.setAttribute("error", e.getMessage());
        }
        request.getRequestDispatcher("/calculator.jsp").forward(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/calculator.jsp").forward(request, response);
    }
}
