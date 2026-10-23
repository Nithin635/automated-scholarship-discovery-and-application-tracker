package com.scholarship.controller;

import com.scholarship.model.User;
import com.scholarship.service.ApplicationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles "Mark as Applied": records that the student completed the
 * external application on the official website and is now returning to
 * track it here. Does NOT open the external site itself — that link is
 * a plain anchor tag in the JSP that opens in a new tab.
 */
@WebServlet("/student/apply")
public class ApplyScholarshipServlet extends HttpServlet {

    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        long scholarshipId;
        try {
            scholarshipId = Long.parseLong(req.getParameter("scholarshipId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/student/scholarships");
            return;
        }

        ApplicationService.MarkAppliedResult result = applicationService.markAsApplied(user.getUserId(), scholarshipId);
        String encodedMessage = URLEncoder.encode(result.message, StandardCharsets.UTF_8.toString());
        String param = result.success ? "success" : "error";

        resp.sendRedirect(req.getContextPath() + "/student/scholarship-details?id=" + scholarshipId
                + "&" + param + "=" + encodedMessage);
    }
}
