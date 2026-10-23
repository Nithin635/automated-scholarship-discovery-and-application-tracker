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

/** Admin: Edit Scholarship form (GET, pre-filled) and save (POST). */
@WebServlet("/admin/edit-scholarship")
public class UpdateScholarshipServlet extends HttpServlet {

    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Scholarship s = scholarshipDAO.findById(id);
            if (s == null) {
                req.setAttribute("errorMessage", "Scholarship not found.");
                req.getRequestDispatcher("/error/404.jsp").forward(req, resp);
                return;
            }
            req.setAttribute("scholarship", s);
            req.getRequestDispatcher("/admin/edit-scholarship.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/scholarships");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load scholarship: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id;
        try {
            id = Long.parseLong(req.getParameter("scholarshipId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/scholarships");
            return;
        }
        Scholarship s = ScholarshipFormBinder.bindFromRequest(req, new Scholarship());
        s.setScholarshipId(id);
        String error = ScholarshipFormBinder.validate(s);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("scholarship", s);
            req.getRequestDispatcher("/admin/edit-scholarship.jsp").forward(req, resp);
            return;
        }
        try {
            scholarshipDAO.update(s);
            resp.sendRedirect(req.getContextPath() + "/admin/scholarships?success=Scholarship+updated+successfully");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to update scholarship: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
