package com.scholarship.controller;

import com.scholarship.service.AuthService;
import com.scholarship.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Demo-safe password reset for a college project (no SMTP configured).
 * The student verifies their own email and sets a new password
 * directly. This is clearly documented in the README as a simplified
 * flow — a production system would email a signed, expiring reset link.
 */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String newPassword = req.getParameter("newPassword");

        if (!ValidationUtil.isValidEmail(email) || !ValidationUtil.isValidPassword(newPassword)) {
            req.setAttribute("error", "Enter a valid email and a new password of at least 6 characters.");
            req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
            return;
        }

        boolean ok = authService.resetPassword(email.trim().toLowerCase(), newPassword);
        if (!ok) {
            req.setAttribute("error", "No account found with that email address.");
            req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/login.jsp?success=Password+reset.+Please+log+in+with+your+new+password.");
    }
}
