package com.scholarship.service;

import com.scholarship.dao.StudentProfileDAO;
import com.scholarship.dao.UserDAO;
import com.scholarship.model.StudentProfile;
import com.scholarship.model.User;
import com.scholarship.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Business logic for registration and login. Servlets call into this
 * service rather than talking to the DAOs (and password hashing)
 * directly, keeping that logic in one reusable, testable place.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentProfileDAO profileDAO = new StudentProfileDAO();

    public static class RegistrationResult {
        public boolean success;
        public String errorMessage;
        public long userId;
    }

    public static class LoginResult {
        public boolean success;
        public String errorMessage;
        public User user;
    }

    /**
     * Registers a new STUDENT account plus their initial eligibility
     * profile in one call. All fields have already been validated by the
     * servlet using ValidationUtil before this is invoked.
     */
    public RegistrationResult registerStudent(String fullName, String email, String plainPassword, String phone,
                                               Date dob, String gender, String state, String category,
                                               String course, String college, Integer yearOfStudy,
                                               BigDecimal cgpa, BigDecimal percentage, BigDecimal income,
                                               boolean disability, boolean minority) {
        RegistrationResult result = new RegistrationResult();
        try {
            if (userDAO.emailExists(email)) {
                result.success = false;
                result.errorMessage = "An account with this email already exists. Please log in instead.";
                return result;
            }

            User user = new User(fullName, email, PasswordUtil.hashPassword(plainPassword), "STUDENT", phone);
            long userId = userDAO.insertUser(user);
            if (userId <= 0) {
                result.success = false;
                result.errorMessage = "Registration failed due to a database error. Please try again.";
                return result;
            }

            StudentProfile profile = new StudentProfile();
            profile.setUserId(userId);
            profile.setDateOfBirth(dob);
            profile.setGender(gender);
            profile.setState(state);
            profile.setCategory(category);
            profile.setCourse(course);
            profile.setCollegeName(college);
            profile.setYearOfStudy(yearOfStudy);
            profile.setCgpa(cgpa);
            profile.setPercentage(percentage);
            profile.setAnnualFamilyIncome(income);
            profile.setDisabilityStatus(disability);
            profile.setMinorityStatus(minority);
            profileDAO.insert(profile);

            result.success = true;
            result.userId = userId;
            return result;
        } catch (SQLException e) {
            result.success = false;
            result.errorMessage = "A database error occurred while registering: " + e.getMessage();
            return result;
        }
    }

    public LoginResult login(String email, String plainPassword) {
        LoginResult result = new LoginResult();
        try {
            User user = userDAO.findByEmail(email);
            if (user == null) {
                result.success = false;
                result.errorMessage = "No account found with this email address.";
                return result;
            }
            if (!PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
                result.success = false;
                result.errorMessage = "Incorrect password. Please try again.";
                return result;
            }
            result.success = true;
            result.user = user;
            return result;
        } catch (SQLException e) {
            result.success = false;
            result.errorMessage = "A database error occurred while logging in: " + e.getMessage();
            return result;
        }
    }

    /**
     * Simple demo-safe password reset: verifies the account exists, then
     * sets a new password directly. A production system would instead
     * email a signed, time-limited reset link; that requires SMTP
     * configuration which is out of scope for this college project and
     * is documented as such in the README.
     */
    public boolean resetPassword(String email, String newPlainPassword) {
        try {
            User user = userDAO.findByEmail(email);
            if (user == null) return false;
            userDAO.updatePassword(user.getUserId(), PasswordUtil.hashPassword(newPlainPassword));
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
