package com.smartloan.web;
import com.smartloan.dao.UserDAO;
import com.smartloan.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDAO dao = new UserDAO();
        User u = dao.login(req.getParameter("username"), req.getParameter("password"));
        if (u != null) {
            req.getSession().setAttribute("user", u);
            resp.sendRedirect("dashboard");
        } else {
            resp.sendRedirect("login.jsp?error=1");
        }
    }
}
