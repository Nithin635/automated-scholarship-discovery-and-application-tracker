package com.scholarship.controller;

import com.scholarship.dao.StudentProfileDAO;
import com.scholarship.model.StudentProfile;
import com.scholarship.model.User;
import com.scholarship.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;

/**
 * View + edit the logged-in student's profile (GET shows it, POST saves
 * updates). Combines "ProfileServlet" and "UpdateProfileServlet" from the
 * spec into a single, easier-to-follow controller since they operate on
 * the exact same resource.
 */
@WebServlet("/student/profile")
public class ProfileServlet extends HttpServlet {

    private final StudentProfileDAO profileDAO = new StudentProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            StudentProfile profile = profileDAO.findByUserId(user.getUserId());
            req.setAttribute("profile", profile);
            req.getRequestDispatcher("/student/profile.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load profile: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String dobRaw = req.getParameter("dob");
        String gender = req.getParameter("gender");
        String state = req.getParameter("state");
        String category = req.getParameter("category");
        String course = req.getParameter("course");
        String college = req.getParameter("college");
        Integer yearOfStudy = ValidationUtil.parseIntOrNull(req.getParameter("yearOfStudy"));
        BigDecimal cgpa = ValidationUtil.parseDecimalOrNull(req.getParameter("cgpa"));
        BigDecimal percentage = ValidationUtil.parseDecimalOrNull(req.getParameter("percentage"));
        BigDecimal income = ValidationUtil.parseDecimalOrNull(req.getParameter("income"));
        boolean disability = "on".equals(req.getParameter("disability"));
        boolean minority = "on".equals(req.getParameter("minority"));

        StringBuilder errors = new StringBuilder();
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
            req.getRequestDispatcher("/student/profile.jsp").forward(req, resp);
            return;
        }

        StudentProfile profile = new StudentProfile();
        profile.setUserId(user.getUserId());
        profile.setDateOfBirth(dob);
        profile.setGender(gender);
        profile.setState(state);
        profile.setCategory(category);
        profile.setCourse(course);
        profile.setCollegeName(college);
        profile.setYearOfStudy(yearOfStudy);
        profile.setCgpa(cgpa);
        profile.setPercentage(percentage);
        profile.setAnnualFamilyIncome(income);
        profile.setDisabilityStatus(disability);
        profile.setMinorityStatus(minority);

        try {
            profileDAO.save(profile);
            resp.sendRedirect(req.getContextPath() + "/student/profile?success=Profile+updated+successfully");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to save profile: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
