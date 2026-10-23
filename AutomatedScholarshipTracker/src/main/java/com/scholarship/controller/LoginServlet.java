package com.scholarship.controller;

import com.scholarship.model.User;
import com.scholarship.service.AuthService;
import com.scholarship.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Handles login and redirects to the correct dashboard based on role.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password)) {
            req.setAttribute("error", "Email and password are both required.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        AuthService.LoginResult result = authService.login(email.trim().toLowerCase(), password);
        if (!result.success) {
            req.setAttribute("error", result.errorMessage);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        User user = result.user;
        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("role", user.getRole());
        session.setMaxInactiveInterval(30 * 60); // 30 minutes

        if (user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard");
        }
    }
}
