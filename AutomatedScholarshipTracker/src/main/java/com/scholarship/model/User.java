package com.scholarship.model;

import java.sql.Timestamp;

/**
 * Represents a row in the `users` table. Shared by both STUDENT and
 * ADMIN accounts; role determines which dashboard the user is sent to.
 */
public class User {

    private long userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String role; // "STUDENT" or "ADMIN"
    private String phoneNumber;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {
    }

    public User(String fullName, String email, String passwordHash, String role, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", fullName='" + fullName + "', email='" + email + "', role='" + role + "'}";
    }
}
