package com.scholarship.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Output of EligibilityService: whether a student is eligible for a given
 * scholarship, and — if not — the specific reasons why, so the UI can show
 * something more useful than a plain "Not Eligible" badge.
 */
public class EligibilityResult {

    private final long scholarshipId;
    private boolean eligible = true;
    private final List<String> reasons = new ArrayList<>();

    public EligibilityResult(long scholarshipId) {
        this.scholarshipId = scholarshipId;
    }

    public void addFailureReason(String reason) {
        this.eligible = false;
        this.reasons.add(reason);
    }

    public long getScholarshipId() {
        return scholarshipId;
    }

    public boolean isEligible() {
        return eligible;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public String getReasonSummary() {
        if (reasons.isEmpty()) return "";
        return String.join(" ", reasons);
    }
}
