package com.scholarship.controller;

import com.scholarship.model.User;
import com.scholarship.service.ApplicationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Lets a student update the status of their own application
 * (e.g. APPLIED -> UNDER_REVIEW). Ownership is enforced inside
 * ApplicationService so a student can never modify someone else's row,
 * even by guessing an application_id in the form.
 */
@WebServlet("/student/update-status")
public class UpdateApplicationStatusServlet extends HttpServlet {

    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        long applicationId;
        try {
            applicationId = Long.parseLong(req.getParameter("applicationId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/student/applications");
            return;
        }
        String newStatus = req.getParameter("status");

        boolean updated = applicationService.updateStatus(applicationId, user.getUserId(), newStatus, false);
        String message = updated ? "success=Status+updated" : "error=Unable+to+update+status";
        resp.sendRedirect(req.getContextPath() + "/student/applications?" + message);
    }
}
