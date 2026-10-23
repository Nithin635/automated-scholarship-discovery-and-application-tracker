package com.scholarship.controller;

import com.scholarship.service.AuthService;
import com.scholarship.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Handles student self-registration: validates every field server-side,
 * then delegates to AuthService to hash the password and create both
 * the user row and the initial student_profiles row.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");
        String dobRaw = req.getParameter("dob");
        String gender = req.getParameter("gender");
        String state = req.getParameter("state");
        String category = req.getParameter("category");
        String course = req.getParameter("course");
        String college = req.getParameter("college");
        String yearRaw = req.getParameter("yearOfStudy");
        String cgpaRaw = req.getParameter("cgpa");
        String percentageRaw = req.getParameter("percentage");
        String incomeRaw = req.getParameter("income");
        boolean disability = "on".equals(req.getParameter("disability"));
        boolean minority = "on".equals(req.getParameter("minority"));

        StringBuilder errors = new StringBuilder();

        if (!ValidationUtil.isNotEmpty(fullName)) errors.append("Full name is required. ");
        if (!ValidationUtil.isValidEmail(email)) errors.append("A valid email address is required. ");
        if (!ValidationUtil.isValidPassword(password)) errors.append("Password must be at least 6 characters. ");
        if (!ValidationUtil.isValidPhone(phone)) errors.append("A valid 10-digit phone number is required. ");

        BigDecimal cgpa = ValidationUtil.parseDecimalOrNull(cgpaRaw);
        BigDecimal percentage = ValidationUtil.parseDecimalOrNull(percentageRaw);
        BigDecimal income = ValidationUtil.parseDecimalOrNull(incomeRaw);
        Integer yearOfStudy = ValidationUtil.parseIntOrNull(yearRaw);

        if (!ValidationUtil.isValidCgpa(cgpa)) errors.append("CGPA must be between 0 and 10. ");
        if (!ValidationUtil.isValidPercentage(percentage)) errors.append("Percentage must be between 0 and 100. ");
        if (!ValidationUtil.isNonNegative(income)) errors.append("Family income cannot be negative. ");

        Date dob = null;
        if (ValidationUtil.isNotEmpty(dobRaw)) {
            try {
                dob = Date.valueOf(dobRaw);
            } catch (IllegalArgumentException e) {
                errors.append("Date of birth is invalid. ");
            }
        }

        if (errors.length() > 0) {
            req.setAttribute("error", errors.toString().trim());
            req.setAttribute("formValues", req.getParameterMap());
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        AuthService.RegistrationResult result = authService.registerStudent(
                fullName.trim(), email.trim().toLowerCase(), password, phone.trim(), dob, gender, state, category,
                course, college, yearOfStudy, cgpa, percentage, income, disability, minority);

        if (!result.success) {
            req.setAttribute("error", result.errorMessage);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/login.jsp?success=Registration+successful.+Please+log+in.");
    }
}
