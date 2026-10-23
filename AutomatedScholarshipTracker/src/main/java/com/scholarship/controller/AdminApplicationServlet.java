package com.scholarship.controller;

import com.scholarship.model.Application;
import com.scholarship.service.ApplicationService;
import com.scholarship.dao.ApplicationDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin view of every application in the system, with the ability to
 * update any application's status (adminOverride=true bypasses the
 * "must be your own application" rule that applies to students).
 */
@WebServlet("/admin/applications")
public class AdminApplicationServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Application> applications = applicationDAO.findAllForAdmin();
            req.setAttribute("applications", applications);
            req.getRequestDispatcher("/admin/applications.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load applications: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long applicationId = Long.parseLong(req.getParameter("applicationId"));
            String newStatus = req.getParameter("status");
            applicationService.updateStatus(applicationId, -1, newStatus, true);
            resp.sendRedirect(req.getContextPath() + "/admin/applications?success=Status+updated");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/applications");
        }
    }
}
