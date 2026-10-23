package com.scholarship.controller;

import com.scholarship.dao.ApplicationDAO;
import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.dao.StudentProfileDAO;
import com.scholarship.model.*;
import com.scholarship.service.EligibilityService;
import com.scholarship.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * Scholarship List / Browse page. Handles plain listing, keyword search
 * (?q=...) and multi-criteria filtering (?category=...&course=...) in
 * one controller since they all render the same JSP with the same
 * "eligible/not eligible" annotation logic — this keeps
 * SearchScholarshipServlet and FilterScholarshipServlet from duplicating
 * that annotation code.
 */
@WebServlet("/student/scholarships")
public class ScholarshipListServlet extends HttpServlet {

    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();
    private final StudentProfileDAO profileDAO = new StudentProfileDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final EligibilityService eligibilityService = new EligibilityService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");

        String q = req.getParameter("q");
        String category = req.getParameter("category");
        String course = req.getParameter("course");
        String state = req.getParameter("state");
        String maxIncomeRaw = req.getParameter("maxIncome");
        String minCgpaRaw = req.getParameter("minCgpa");
        String type = req.getParameter("type");
        String eligibilityFilter = req.getParameter("eligibility"); // "ELIGIBLE" / "ALL"

        try {
            List<Scholarship> scholarships;
            boolean isFiltering = ValidationUtil.isNotEmpty(category) || ValidationUtil.isNotEmpty(course)
                    || ValidationUtil.isNotEmpty(state) || ValidationUtil.isNotEmpty(maxIncomeRaw)
                    || ValidationUtil.isNotEmpty(minCgpaRaw) || ValidationUtil.isNotEmpty(type);

            if (ValidationUtil.isNotEmpty(q)) {
                scholarships = scholarshipDAO.search(q.trim());
            } else if (isFiltering) {
                BigDecimal maxIncome = ValidationUtil.parseDecimalOrNull(maxIncomeRaw);
                BigDecimal minCgpa = ValidationUtil.parseDecimalOrNull(minCgpaRaw);
                scholarships = scholarshipDAO.filter(category, course, state, maxIncome, minCgpa, type);
            } else {
                scholarships = scholarshipDAO.findAllActive();
            }

            StudentProfile profile = profileDAO.findByUserId(user.getUserId());
            Map<Long, EligibilityResult> eligibilityMap = new LinkedHashMap<>();
            Map<Long, Boolean> appliedMap = new LinkedHashMap<>();
            for (Scholarship s : scholarships) {
                eligibilityMap.put(s.getScholarshipId(), eligibilityService.evaluate(profile, s));
                appliedMap.put(s.getScholarshipId(), applicationDAO.hasApplied(user.getUserId(), s.getScholarshipId()));
            }

            if ("ELIGIBLE".equalsIgnoreCase(eligibilityFilter)) {
                List<Scholarship> onlyEligible = new ArrayList<>();
                for (Scholarship s : scholarships) {
                    if (eligibilityMap.get(s.getScholarshipId()).isEligible()) onlyEligible.add(s);
                }
                scholarships = onlyEligible;
            }

            req.setAttribute("scholarships", scholarships);
            req.setAttribute("eligibilityMap", eligibilityMap);
            req.setAttribute("appliedMap", appliedMap);
            req.setAttribute("query", q);
            req.getRequestDispatcher("/student/scholarships.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Unable to load scholarships: " + e.getMessage());
            req.getRequestDispatcher("/error/500.jsp").forward(req, resp);
        }
    }
}
