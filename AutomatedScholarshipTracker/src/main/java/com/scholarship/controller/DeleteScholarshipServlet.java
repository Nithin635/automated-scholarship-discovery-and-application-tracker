package com.scholarship.controller;

import com.scholarship.dao.ScholarshipDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Admin: deletes a scholarship. The confirmation dialog itself lives in
 * the JSP/JavaScript ("Are you sure?"); by the time this POST arrives
 * the admin has already confirmed.
 */
@WebServlet("/admin/delete-scholarship")
public class DeleteScholarshipServlet extends HttpServlet {

    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long id = Long.parseLong(req.getParameter("scholarshipId"));
            scholarshipDAO.delete(id);
            resp.sendRedirect(req.getContextPath() + "/admin/scholarships?success=Scholarship+deleted");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/scholarships");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to delete scholarship: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
