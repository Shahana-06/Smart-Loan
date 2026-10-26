package com.smartloan.web;

import com.smartloan.dao.UserDAO;
import com.smartloan.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class ProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");
        User user = userDAO.getUserById(sessionUser.getId());
        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User account was not found.");
            return;
        }
        request.setAttribute("income", user.getIncome());
        request.setAttribute("expenses", user.getExpenses());
        request.setAttribute("emergency_reserve", user.getEmergencyReserve());
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            User user = (User) session.getAttribute("user");
            double income = parseNonNegative(request.getParameter("income"), "Income");
            double expenses = parseNonNegative(request.getParameter("expenses"), "Expenses");
            double reserve = parseNonNegative(request.getParameter("reserve"), "Emergency reserve");
            if (!userDAO.updateProfile(user.getId(), income, expenses, reserve)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User account was not found.");
                return;
            }
            response.sendRedirect("profile");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ServletException("Could not save the profile.", e);
        }
    }

    private double parseNonNegative(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        double amount = Double.parseDouble(value);
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(field + " must be zero or greater.");
        }
        return amount;
    }
}
