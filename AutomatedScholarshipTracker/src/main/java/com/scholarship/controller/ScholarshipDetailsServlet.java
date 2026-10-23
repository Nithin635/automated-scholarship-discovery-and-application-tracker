package com.scholarship.controller;

import com.scholarship.dao.ApplicationDAO;
import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.dao.StudentProfileDAO;
import com.scholarship.model.*;
import com.scholarship.service.EligibilityService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Full detail page for a single scholarship, including a live
 * eligibility verdict for the logged-in student.
 */
@WebServlet("/student/scholarship-details")
public class ScholarshipDetailsServlet extends HttpServlet {

    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();
    private final StudentProfileDAO profileDAO = new StudentProfileDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final EligibilityService eligibilityService = new EligibilityService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String idParam = req.getParameter("id");

        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException | NullPointerException e) {
            resp.sendRedirect(req.getContextPath() + "/student/scholarships");
            return;
        }

        try {
            Scholarship scholarship = scholarshipDAO.findById(id);
            if (scholarship == null) {
                req.setAttribute("errorMessage", "That scholarship could not be found.");
                req.getRequestDispatcher("/error/404.jsp").forward(req, resp);
                return;
            }
            StudentProfile profile = profileDAO.findByUserId(user.getUserId());
            EligibilityResult eligibility = eligibilityService.evaluate(profile, scholarship);
            boolean applied = applicationDAO.hasApplied(user.getUserId(), scholarship.getScholarshipId());

            req.setAttribute("scholarship", scholarship);
            req.setAttribute("eligibility", eligibility);
            req.setAttribute("applied", applied);
            req.getRequestDispatcher("/student/scholarship-details.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load scholarship details: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
