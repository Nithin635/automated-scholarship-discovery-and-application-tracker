package com.scholarship.controller;

import com.scholarship.dao.ApplicationDAO;
import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("totalScholarships", scholarshipDAO.countAll());
            req.setAttribute("activeScholarships", scholarshipDAO.countActive());
            req.setAttribute("expiredScholarships", scholarshipDAO.countExpired());
            req.setAttribute("totalStudents", userDAO.countStudents());
            req.setAttribute("totalApplications", applicationDAO.countAll());
            req.setAttribute("pendingApplications", applicationDAO.countPending());
            req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load admin dashboard: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
