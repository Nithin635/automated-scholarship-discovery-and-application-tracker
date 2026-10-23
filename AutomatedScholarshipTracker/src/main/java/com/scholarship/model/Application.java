package com.scholarship.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a row in the `applications` table — a student's record of
 * applying to a particular scholarship.
 */
public class Application {

    private long applicationId;
    private long studentId;
    private long scholarshipId;
    private Date appliedDate;
    private String status; // APPLIED, UNDER_REVIEW, APPROVED, REJECTED, PENDING
    private Timestamp lastUpdated;
    private String notes;

    // Convenience fields populated by joined DAO queries for display purposes.
    private String scholarshipName;
    private String scholarshipProvider;
    private String trackingUrl;
    private String studentName;

    public Application() {
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getScholarshipId() {
        return scholarshipId;
    }

    public void setScholarshipId(long scholarshipId) {
        this.scholarshipId = scholarshipId;
    }

    public Date getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Date appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getScholarshipName() {
        return scholarshipName;
    }

    public void setScholarshipName(String scholarshipName) {
        this.scholarshipName = scholarshipName;
    }

    public String getScholarshipProvider() {
        return scholarshipProvider;
    }

    public void setScholarshipProvider(String scholarshipProvider) {
        this.scholarshipProvider = scholarshipProvider;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public String toString() {
        return "Application{id=" + applicationId + ", studentId=" + studentId
                + ", scholarshipId=" + scholarshipId + ", status='" + status + "'}";
    }
}
