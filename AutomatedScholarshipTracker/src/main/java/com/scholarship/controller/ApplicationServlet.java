package com.scholarship.controller;

import com.scholarship.dao.ApplicationDAO;
import com.scholarship.model.Application;
import com.scholarship.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * "My Applications" page: every scholarship the logged-in student has
 * applied to, with current status and a track/details link for each.
 */
@WebServlet("/student/applications")
public class ApplicationServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            List<Application> applications = applicationDAO.findByStudent(user.getUserId());
            req.setAttribute("applications", applications);
            req.getRequestDispatcher("/student/applications.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load your applications: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
