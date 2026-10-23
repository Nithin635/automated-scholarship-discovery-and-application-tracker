package com.scholarship.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Represents a row in the `scholarships` table.
 * eligible_categories / eligible_courses / eligible_states are stored as
 * comma-separated strings (or "ALL" meaning unrestricted) to keep the
 * schema simple; EligibilityService knows how to parse them.
 */
public class Scholarship {

    private long scholarshipId;
    private String name;
    private String provider;
    private String description;
    private String scholarshipType; // GOVERNMENT, PRIVATE, INSTITUTIONAL, NGO
    private String amount;
    private String eligibilityDescription;
    private BigDecimal minimumCgpa;
    private BigDecimal minimumPercentage;
    private BigDecimal maximumFamilyIncome;
    private String eligibleCategories;
    private String eligibleCourses;
    private String eligibleStates;
    private Integer minimumYear;
    private Integer maximumYear;
    private String genderEligibility; // ALL, MALE, FEMALE
    private boolean disabilityRequired;
    private boolean minorityRequired;
    private Date applicationStartDate;
    private Date applicationDeadline;
    private String applicationUrl;
    private String trackingUrl;
    private String status; // ACTIVE, INACTIVE

    public Scholarship() {
    }

    public long getScholarshipId() {
        return scholarshipId;
    }

    public void setScholarshipId(long scholarshipId) {
        this.scholarshipId = scholarshipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScholarshipType() {
        return scholarshipType;
    }

    public void setScholarshipType(String scholarshipType) {
        this.scholarshipType = scholarshipType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEligibilityDescription() {
        return eligibilityDescription;
    }

    public void setEligibilityDescription(String eligibilityDescription) {
        this.eligibilityDescription = eligibilityDescription;
    }

    public BigDecimal getMinimumCgpa() {
        return minimumCgpa;
    }

    public void setMinimumCgpa(BigDecimal minimumCgpa) {
        this.minimumCgpa = minimumCgpa;
    }

    public BigDecimal getMinimumPercentage() {
        return minimumPercentage;
    }

    public void setMinimumPercentage(BigDecimal minimumPercentage) {
        this.minimumPercentage = minimumPercentage;
    }

    public BigDecimal getMaximumFamilyIncome() {
        return maximumFamilyIncome;
    }

    public void setMaximumFamilyIncome(BigDecimal maximumFamilyIncome) {
        this.maximumFamilyIncome = maximumFamilyIncome;
    }

    public String getEligibleCategories() {
        return eligibleCategories;
    }

    public void setEligibleCategories(String eligibleCategories) {
        this.eligibleCategories = eligibleCategories;
    }

    public String getEligibleCourses() {
        return eligibleCourses;
    }

    public void setEligibleCourses(String eligibleCourses) {
        this.eligibleCourses = eligibleCourses;
    }

    public String getEligibleStates() {
        return eligibleStates;
    }

    public void setEligibleStates(String eligibleStates) {
        this.eligibleStates = eligibleStates;
    }

    public Integer getMinimumYear() {
        return minimumYear;
    }

    public void setMinimumYear(Integer minimumYear) {
        this.minimumYear = minimumYear;
    }

    public Integer getMaximumYear() {
        return maximumYear;
    }

    public void setMaximumYear(Integer maximumYear) {
        this.maximumYear = maximumYear;
    }

    public String getGenderEligibility() {
        return genderEligibility;
    }

    public void setGenderEligibility(String genderEligibility) {
        this.genderEligibility = genderEligibility;
    }

    public boolean isDisabilityRequired() {
        return disabilityRequired;
    }

    public void setDisabilityRequired(boolean disabilityRequired) {
        this.disabilityRequired = disabilityRequired;
    }

    public boolean isMinorityRequired() {
        return minorityRequired;
    }

    public void setMinorityRequired(boolean minorityRequired) {
        this.minorityRequired = minorityRequired;
    }

    public Date getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(Date applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public Date getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(Date applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** True once the application deadline has passed. */
    public boolean isExpired() {
        return applicationDeadline != null && applicationDeadline.toLocalDate().isBefore(LocalDate.now());
    }

    /** Days left until the deadline (calendar days); negative once expired. */
    public long getDaysRemaining() {
        if (applicationDeadline == null) return 0;
        return applicationDeadline.toLocalDate().toEpochDay() - LocalDate.now().toEpochDay();
    }

    /** True when the scholarship is still open but the deadline is within 7 days. */
    public boolean isClosingSoon() {
        long remaining = getDaysRemaining();
        return !isExpired() && remaining >= 0 && remaining <= 7;
    }

    @Override
    public String toString() {
        return "Scholarship{id=" + scholarshipId + ", name='" + name + "'}";
    }
}
