package com.scholarship.service;

import com.scholarship.dao.ApplicationDAO;
import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.model.Application;
import com.scholarship.model.Scholarship;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Business logic for the "Mark as Applied" flow and application status
 * updates. Encapsulates the rules described in the spec: applying does
 * NOT happen automatically just because the student clicked the
 * external "Apply Now" link, duplicates are prevented, and expired
 * scholarships cannot be newly applied to.
 */
public class ApplicationService {

    private static final List<String> VALID_STATUSES =
            Arrays.asList("APPLIED", "UNDER_REVIEW", "APPROVED", "REJECTED", "PENDING");

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final ScholarshipDAO scholarshipDAO = new ScholarshipDAO();

    public static class MarkAppliedResult {
        public boolean success;
        public String message;
    }

    public MarkAppliedResult markAsApplied(long studentId, long scholarshipId) {
        MarkAppliedResult result = new MarkAppliedResult();
        try {
            Scholarship scholarship = scholarshipDAO.findById(scholarshipId);
            if (scholarship == null) {
                result.success = false;
                result.message = "Scholarship not found.";
                return result;
            }
            if (scholarship.isExpired()) {
                result.success = false;
                result.message = "This scholarship's application deadline has passed and can no longer be applied to.";
                return result;
            }
            boolean inserted = applicationDAO.insertApplication(studentId, scholarshipId);
            if (!inserted) {
                result.success = false;
                result.message = "You have already applied to this scholarship.";
                return result;
            }
            result.success = true;
            result.message = "Application recorded successfully.";
            return result;
        } catch (SQLException e) {
            result.success = false;
            result.message = "A database error occurred: " + e.getMessage();
            return result;
        }
    }

    /**
     * Updates the status of an application, but only if it belongs to
     * the requesting student (or the caller is an admin, who may update
     * any application — enforced by the servlet before calling this
     * with adminOverride=true).
     */
    public boolean updateStatus(long applicationId, long requestingStudentId, String newStatus, boolean adminOverride) {
        if (!VALID_STATUSES.contains(newStatus)) return false;
        try {
            Application app = applicationDAO.findById(applicationId);
            if (app == null) return false;
            if (!adminOverride && app.getStudentId() != requestingStudentId) {
                // Prevent a student from modifying another student's application.
                return false;
            }
            applicationDAO.updateStatus(applicationId, newStatus);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isValidStatus(String status) {
        return VALID_STATUSES.contains(status);
    }
}
