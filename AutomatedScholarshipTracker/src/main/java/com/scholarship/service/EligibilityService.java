package com.scholarship.service;

import com.scholarship.model.EligibilityResult;
import com.scholarship.model.Scholarship;
import com.scholarship.model.StudentProfile;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Automatically compares a student's profile against a scholarship's
 * eligibility rules. This is the heart of the "automated discovery"
 * feature: instead of a plain scholarship listing, every card the
 * student sees is annotated ELIGIBLE / NOT ELIGIBLE with a reason.
 *
 * Rule: if a scholarship field is unrestricted ("ALL" or null), that
 * criterion is skipped entirely — it never counts against the student.
 */
public class EligibilityService {

    public EligibilityResult evaluate(StudentProfile profile, Scholarship s) {
        EligibilityResult result = new EligibilityResult(s.getScholarshipId());

        if (profile == null) {
            result.addFailureReason("Complete your student profile to check eligibility.");
            return result;
        }

        // 1. CGPA
        if (s.getMinimumCgpa() != null) {
            if (profile.getCgpa() == null) {
                result.addFailureReason("Add your CGPA to your profile to check this criterion.");
            } else if (profile.getCgpa().compareTo(s.getMinimumCgpa()) < 0) {
                result.addFailureReason("Your CGPA (" + profile.getCgpa() + ") is below the minimum required CGPA of "
                        + s.getMinimumCgpa() + ".");
            }
        }

        // 2. Percentage
        if (s.getMinimumPercentage() != null) {
            if (profile.getPercentage() == null) {
                result.addFailureReason("Add your percentage/marks to your profile to check this criterion.");
            } else if (profile.getPercentage().compareTo(s.getMinimumPercentage()) < 0) {
                result.addFailureReason("Your percentage (" + profile.getPercentage()
                        + "%) is below the minimum required " + s.getMinimumPercentage() + "%.");
            }
        }

        // 3. Family income (maximum allowed)
        if (s.getMaximumFamilyIncome() != null) {
            if (profile.getAnnualFamilyIncome() == null) {
                result.addFailureReason("Add your annual family income to your profile to check this criterion.");
            } else if (profile.getAnnualFamilyIncome().compareTo(s.getMaximumFamilyIncome()) > 0) {
                result.addFailureReason("Family income exceeds the maximum allowed income of ₹"
                        + formatMoney(s.getMaximumFamilyIncome()) + ".");
            }
        }

        // 4. Category
        if (!isUnrestricted(s.getEligibleCategories())) {
            if (isBlank(profile.getCategory()) || !containsIgnoreCase(s.getEligibleCategories(), profile.getCategory())) {
                result.addFailureReason("Your category (" + safe(profile.getCategory())
                        + ") does not match the eligible categories (" + s.getEligibleCategories() + ").");
            }
        }

        // 5. Course
        if (!isUnrestricted(s.getEligibleCourses())) {
            if (isBlank(profile.getCourse()) || !containsIgnoreCase(s.getEligibleCourses(), profile.getCourse())) {
                result.addFailureReason("Your course (" + safe(profile.getCourse())
                        + ") does not match the eligible courses (" + s.getEligibleCourses() + ").");
            }
        }

        // 6. State
        if (!isUnrestricted(s.getEligibleStates())) {
            if (isBlank(profile.getState()) || !containsIgnoreCase(s.getEligibleStates(), profile.getState())) {
                result.addFailureReason("This scholarship is restricted to students of " + s.getEligibleStates() + ".");
            }
        }

        // 7. Year of study range
        if (s.getMinimumYear() != null || s.getMaximumYear() != null) {
            if (profile.getYearOfStudy() == null) {
                result.addFailureReason("Add your current year of study to your profile to check this criterion.");
            } else {
                int year = profile.getYearOfStudy();
                if (s.getMinimumYear() != null && year < s.getMinimumYear()) {
                    result.addFailureReason("This scholarship requires year of study >= " + s.getMinimumYear() + ".");
                }
                if (s.getMaximumYear() != null && year > s.getMaximumYear()) {
                    result.addFailureReason("This scholarship requires year of study <= " + s.getMaximumYear() + ".");
                }
            }
        }

        // 8. Gender
        if (s.getGenderEligibility() != null && !s.getGenderEligibility().equalsIgnoreCase("ALL")) {
            if (isBlank(profile.getGender()) || !profile.getGender().equalsIgnoreCase(s.getGenderEligibility())) {
                result.addFailureReason("This scholarship is open only to " + s.getGenderEligibility() + " students.");
            }
        }

        // 9. Disability requirement
        if (s.isDisabilityRequired() && !profile.isDisabilityStatus()) {
            result.addFailureReason("This scholarship requires disability status to be declared on your profile.");
        }

        // 10. Minority requirement
        if (s.isMinorityRequired() && !profile.isMinorityStatus()) {
            result.addFailureReason("This scholarship requires minority status to be declared on your profile.");
        }

        return result;
    }

    private boolean isUnrestricted(String field) {
        return field == null || field.trim().isEmpty() || field.trim().equalsIgnoreCase("ALL");
    }

    private boolean containsIgnoreCase(String commaSeparatedList, String value) {
        return Arrays.stream(commaSeparatedList.split(","))
                .map(String::trim)
                .anyMatch(item -> item.equalsIgnoreCase(value.trim()));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safe(String s) {
        return isBlank(s) ? "not set" : s;
    }

    private String formatMoney(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }
}
