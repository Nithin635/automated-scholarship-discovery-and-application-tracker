package com.scholarship.controller;

import com.scholarship.model.Scholarship;
import com.scholarship.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;

/**
 * Small shared helper so AddScholarshipServlet and UpdateScholarshipServlet
 * don't duplicate ~20 lines of "read every form field" code. Not a full
 * service class since it does no I/O — just request-to-model binding and
 * validation for the admin scholarship form.
 */
final class ScholarshipFormBinder {

    private ScholarshipFormBinder() {
    }

    static Scholarship bindFromRequest(HttpServletRequest req, Scholarship s) {
        s.setName(req.getParameter("name"));
        s.setProvider(req.getParameter("provider"));
        s.setDescription(req.getParameter("description"));
        s.setScholarshipType(req.getParameter("scholarshipType"));
        s.setAmount(req.getParameter("amount"));
        s.setEligibilityDescription(req.getParameter("eligibilityDescription"));
        s.setMinimumCgpa(ValidationUtil.parseDecimalOrNull(req.getParameter("minimumCgpa")));
        s.setMinimumPercentage(ValidationUtil.parseDecimalOrNull(req.getParameter("minimumPercentage")));
        s.setMaximumFamilyIncome(ValidationUtil.parseDecimalOrNull(req.getParameter("maximumFamilyIncome")));
        s.setEligibleCategories(nullToAll(req.getParameter("eligibleCategories")));
        s.setEligibleCourses(nullToAll(req.getParameter("eligibleCourses")));
        s.setEligibleStates(nullToAll(req.getParameter("eligibleStates")));
        s.setMinimumYear(ValidationUtil.parseIntOrNull(req.getParameter("minimumYear")));
        s.setMaximumYear(ValidationUtil.parseIntOrNull(req.getParameter("maximumYear")));
        String gender = req.getParameter("genderEligibility");
        s.setGenderEligibility(ValidationUtil.isNotEmpty(gender) ? gender : "ALL");
        s.setDisabilityRequired("on".equals(req.getParameter("disabilityRequired")));
        s.setMinorityRequired("on".equals(req.getParameter("minorityRequired")));

        String startRaw = req.getParameter("applicationStartDate");
        if (ValidationUtil.isNotEmpty(startRaw)) {
            try {
                s.setApplicationStartDate(Date.valueOf(startRaw));
            } catch (IllegalArgumentException ignored) {
            }
        }
        String deadlineRaw = req.getParameter("applicationDeadline");
        if (ValidationUtil.isNotEmpty(deadlineRaw)) {
            try {
                s.setApplicationDeadline(Date.valueOf(deadlineRaw));
            } catch (IllegalArgumentException ignored) {
            }
        }
        s.setApplicationUrl(req.getParameter("applicationUrl"));
        s.setTrackingUrl(req.getParameter("trackingUrl"));
        String status = req.getParameter("status");
        s.setStatus(ValidationUtil.isNotEmpty(status) ? status : "ACTIVE");
        return s;
    }

    private static String nullToAll(String value) {
        return ValidationUtil.isNotEmpty(value) ? value.trim() : "ALL";
    }

    /** Returns an error message string, or null if the scholarship is valid. */
    static String validate(Scholarship s) {
        StringBuilder errors = new StringBuilder();
        if (!ValidationUtil.isNotEmpty(s.getName())) errors.append("Scholarship name is required. ");
        if (!ValidationUtil.isNotEmpty(s.getProvider())) errors.append("Provider is required. ");
        if (!ValidationUtil.isNotEmpty(s.getApplicationUrl())) errors.append("Official application URL is required. ");
        if (s.getApplicationDeadline() == null) errors.append("Application deadline is required. ");
        if (!ValidationUtil.isValidCgpa(s.getMinimumCgpa())) errors.append("Minimum CGPA must be between 0 and 10. ");
        if (!ValidationUtil.isValidPercentage(s.getMinimumPercentage())) errors.append("Minimum percentage must be between 0 and 100. ");
        if (!ValidationUtil.isNonNegative(s.getMaximumFamilyIncome())) errors.append("Maximum family income cannot be negative. ");
        return errors.length() > 0 ? errors.toString().trim() : null;
    }
}
