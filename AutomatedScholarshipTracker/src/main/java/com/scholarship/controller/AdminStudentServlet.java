package com.scholarship.controller;

import com.scholarship.dao.StudentProfileDAO;
import com.scholarship.dao.UserDAO;
import com.scholarship.model.StudentProfile;
import com.scholarship.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Admin: read-only list of all registered students and their profile summaries. */
@WebServlet("/admin/students")
public class AdminStudentServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final StudentProfileDAO profileDAO = new StudentProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<User> students = userDAO.findAllStudents();
            Map<Long, StudentProfile> profiles = new LinkedHashMap<>();
            for (User u : students) {
                profiles.put(u.getUserId(), profileDAO.findByUserId(u.getUserId()));
            }
            req.setAttribute("students", students);
            req.setAttribute("profiles", profiles);
            req.getRequestDispatcher("/admin/students.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load students: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
