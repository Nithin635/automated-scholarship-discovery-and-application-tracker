package com.scholarship.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Extended academic/eligibility information for a STUDENT user.
 * One row per student, linked 1:1 to users.user_id.
 */
public class StudentProfile {

    private long profileId;
    private long userId;
    private Date dateOfBirth;
    private String gender;              // MALE, FEMALE, OTHER
    private String state;
    private String category;            // GENERAL, OBC, SC, ST, EWS
    private String course;
    private String collegeName;
    private Integer yearOfStudy;
    private BigDecimal cgpa;
    private BigDecimal percentage;
    private BigDecimal annualFamilyIncome;
    private boolean disabilityStatus;
    private boolean minorityStatus;

    public StudentProfile() {
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public BigDecimal getCgpa() {
        return cgpa;
    }

    public void setCgpa(BigDecimal cgpa) {
        this.cgpa = cgpa;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getAnnualFamilyIncome() {
        return annualFamilyIncome;
    }

    public void setAnnualFamilyIncome(BigDecimal annualFamilyIncome) {
        this.annualFamilyIncome = annualFamilyIncome;
    }

    public boolean isDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(boolean disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public boolean isMinorityStatus() {
        return minorityStatus;
    }

    public void setMinorityStatus(boolean minorityStatus) {
        this.minorityStatus = minorityStatus;
    }

    /**
     * Very simple, transparent completeness estimate used on the
     * profile page's progress bar. Counts how many of the 11 key
     * eligibility fields are filled in.
     */
    public int getCompletionPercentage() {
        int total = 11;
        int filled = 0;
        if (dateOfBirth != null) filled++;
        if (gender != null && !gender.isEmpty()) filled++;
        if (state != null && !state.isEmpty()) filled++;
        if (category != null && !category.isEmpty()) filled++;
        if (course != null && !course.isEmpty()) filled++;
        if (collegeName != null && !collegeName.isEmpty()) filled++;
        if (yearOfStudy != null) filled++;
        if (cgpa != null) filled++;
        if (percentage != null) filled++;
        if (annualFamilyIncome != null) filled++;
        // disability/minority are boolean flags with a default, so we
        // count them as "filled" together as the 11th slot once the
        // rest of the profile has been saved at least once.
        if (profileId > 0) filled++;
        return (int) Math.round((filled * 100.0) / total);
    }

    @Override
    public String toString() {
        return "StudentProfile{userId=" + userId + ", course='" + course + "', cgpa=" + cgpa + "}";
    }
}
