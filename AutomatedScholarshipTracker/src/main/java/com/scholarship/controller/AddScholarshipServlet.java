package com.scholarship.controller;

import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.model.Scholarship;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/** Admin: Add Scholarship form (GET) and save (POST). */
@WebServlet("/admin/add-scholarship")
public class AddScholarshipServlet extends HttpServlet {

    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/admin/add-scholarship.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Scholarship s = ScholarshipFormBinder.bindFromRequest(req, new Scholarship());
        String error = ScholarshipFormBinder.validate(s);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("scholarship", s);
            req.getRequestDispatcher("/admin/add-scholarship.jsp").forward(req, resp);
            return;
        }
        try {
            scholarshipDAO.insert(s);
            resp.sendRedirect(req.getContextPath() + "/admin/scholarships?success=Scholarship+added+successfully");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to add scholarship: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
