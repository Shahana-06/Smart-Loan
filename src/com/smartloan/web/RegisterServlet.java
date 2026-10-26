package com.smartloan.web;
import com.smartloan.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String u = request.getParameter("username");
        String p = request.getParameter("password");
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            ps.setString(1, u);
            ps.setString(2, p); // Note: In production, hash this password!
            ps.executeUpdate();
            response.sendRedirect("login.jsp?registered=1");
        } catch (Exception e) {
            response.sendRedirect("register.jsp?error=1");
        }
    }
}
