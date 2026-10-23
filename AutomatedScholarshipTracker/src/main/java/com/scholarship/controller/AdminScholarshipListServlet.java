package com.scholarship.controller;

import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.model.Scholarship;
import com.scholarship.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Admin's scholarship management table, with an optional search box. */
@WebServlet("/admin/scholarships")
public class AdminScholarshipListServlet extends HttpServlet {

    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q");
        try {
            List<Scholarship> scholarships = ValidationUtil.isNotEmpty(q)
                    ? scholarshipDAO.searchAdmin(q.trim())
                    : scholarshipDAO.findAll();
            req.setAttribute("scholarships", scholarships);
            req.setAttribute("query", q);
            req.getRequestDispatcher("/admin/scholarships.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load scholarships: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
