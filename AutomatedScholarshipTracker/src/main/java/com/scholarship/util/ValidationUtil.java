package com.scholarship.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Shared server-side validation rules. All form-processing servlets run
 * incoming data through these checks before it ever reaches a DAO —
 * JSP pages only do light client-side checks for a nicer UX, never the
 * authoritative validation.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[6-9]\\d{9}$"); // 10-digit Indian mobile number

    private ValidationUtil() {
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        // Minimum 6 characters for a demo college project; never empty.
        return isNotEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidCgpa(BigDecimal cgpa) {
        if (cgpa == null) return true; // optional field
        return cgpa.compareTo(BigDecimal.ZERO) >= 0 && cgpa.compareTo(BigDecimal.TEN) <= 0;
    }

    public static boolean isValidPercentage(BigDecimal percentage) {
        if (percentage == null) return true; // optional field
        return percentage.compareTo(BigDecimal.ZERO) >= 0 && percentage.compareTo(new BigDecimal("100")) <= 0;
    }

    public static boolean isNonNegative(BigDecimal value) {
        if (value == null) return true; // optional field
        return value.compareTo(BigDecimal.ZERO) >= 0;
    }

    /** Parses a BigDecimal from a form field, returning null for blank input. */
    public static BigDecimal parseDecimalOrNull(String raw) {
        if (!isNotEmpty(raw)) return null;
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Parses an Integer from a form field, returning null for blank/invalid input. */
    public static Integer parseIntOrNull(String raw) {
        if (!isNotEmpty(raw)) return null;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Basic HTML-escaping to defend against stored/reflected XSS when echoing user input. */
    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
