package com.scholarship.controller;

import com.scholarship.dao.ApplicationDAO;
import com.scholarship.model.Application;
import com.scholarship.model.User;
import com.scholarship.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Redirects the student to the scholarship's official tracking website.
 * If no tracking URL is on file, sends them back to "My Applications"
 * with a clear message instead of fabricating a link.
 */
@WebServlet("/student/track")
public class TrackApplicationServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        long applicationId;
        try {
            applicationId = Long.parseLong(req.getParameter("applicationId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/student/applications");
            return;
        }

        try {
            Application app = applicationDAO.findById(applicationId);
            if (app == null || app.getStudentId() != user.getUserId()) {
                resp.sendRedirect(req.getContextPath() + "/student/applications?error=Application+not+found");
                return;
            }
            if (ValidationUtil.isNotEmpty(app.getTrackingUrl())) {
                resp.sendRedirect(app.getTrackingUrl());
            } else {
                resp.sendRedirect(req.getContextPath()
                        + "/student/applications?error=Official+tracking+information+is+not+available.+Please+visit+the+scholarship+provider%27s+website.");
            }
        } catch (SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/student/applications?error=Database+error");
        }
    }
}
