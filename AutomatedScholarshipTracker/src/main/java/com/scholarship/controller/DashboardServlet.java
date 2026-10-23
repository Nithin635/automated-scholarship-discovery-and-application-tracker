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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the Student Dashboard: summary cards (eligible scholarships,
 * applications, pending/approved counts) and the "Upcoming Deadlines"
 * list, computed fresh on every visit.
 */
@WebServlet("/student/dashboard")
public class DashboardServlet extends HttpServlet {

    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();
    private final StudentProfileDAO profileDAO = new StudentProfileDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final EligibilityService eligibilityService = new EligibilityService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        try {
            StudentProfile profile = profileDAO.findByUserId(user.getUserId());
            List<Scholarship> active = scholarshipDAO.findAllActive();

            int eligibleCount = 0;
            List<Scholarship> upcomingDeadlines = new ArrayList<>();
            for (Scholarship s : active) {
                if (!s.isExpired() && eligibilityService.evaluate(profile, s).isEligible()) {
                    eligibleCount++;
                }
                if (!s.isExpired()) {
                    upcomingDeadlines.add(s);
                }
            }
            upcomingDeadlines.sort((a, b) -> a.getApplicationDeadline().compareTo(b.getApplicationDeadline()));
            if (upcomingDeadlines.size() > 5) {
                upcomingDeadlines = upcomingDeadlines.subList(0, 5);
            }

            int totalApplications = applicationDAO.countByStudent(user.getUserId());
            int pending = applicationDAO.countByStudentAndStatus(user.getUserId(), "APPLIED")
                    + applicationDAO.countByStudentAndStatus(user.getUserId(), "UNDER_REVIEW")
                    + applicationDAO.countByStudentAndStatus(user.getUserId(), "PENDING");
            int approved = applicationDAO.countByStudentAndStatus(user.getUserId(), "APPROVED");

            req.setAttribute("profile", profile);
            req.setAttribute("totalScholarships", active.size());
            req.setAttribute("eligibleCount", eligibleCount);
            req.setAttribute("totalApplications", totalApplications);
            req.setAttribute("pendingCount", pending);
            req.setAttribute("approvedCount", approved);
            req.setAttribute("upcomingDeadlines", upcomingDeadlines);

            req.getRequestDispatcher("/student/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load dashboard: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
