package com.scholarship.dao;

import com.scholarship.model.Application;
import com.scholarship.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the `applications` table. Joins against scholarships
 * (and users, for the admin view) so the UI has display-ready data
 * without needing a service-layer aggregation step.
 */
public class ApplicationDAO {

    public boolean hasApplied(long studentId, long scholarshipId) throws SQLException {
        String sql = "SELECT 1 FROM applications WHERE student_id = ? AND scholarship_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, scholarshipId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Inserts a new application row with status APPLIED. Returns false if a duplicate exists. */
    public boolean insertApplication(long studentId, long scholarshipId) throws SQLException {
        if (hasApplied(studentId, scholarshipId)) {
            return false; // duplicate application prevented
        }
        String sql = "INSERT INTO applications (student_id, scholarship_id, applied_date, status) " +
                "VALUES (?, ?, CURDATE(), 'APPLIED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, scholarshipId);
            ps.executeUpdate();
            return true;
        }
    }

    public List<Application> findByStudent(long studentId) throws SQLException {
        String sql = "SELECT a.*, s.name AS scholarship_name, s.provider AS scholarship_provider, " +
                "s.tracking_url AS tracking_url " +
                "FROM applications a JOIN scholarships s ON a.scholarship_id = s.scholarship_id " +
                "WHERE a.student_id = ? ORDER BY a.applied_date DESC";
        List<Application> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowWithScholarship(rs));
            }
        }
        return list;
    }

    public Application findById(long applicationId) throws SQLException {
        String sql = "SELECT a.*, s.name AS scholarship_name, s.provider AS scholarship_provider, " +
                "s.tracking_url AS tracking_url " +
                "FROM applications a JOIN scholarships s ON a.scholarship_id = s.scholarship_id " +
                "WHERE a.application_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowWithScholarship(rs);
            }
        }
        return null;
    }

    /** Updates status; the caller (servlet) must first verify the application belongs to the requesting student. */
    public void updateStatus(long applicationId, String newStatus) throws SQLException {
        String sql = "UPDATE applications SET status = ? WHERE application_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, applicationId);
            ps.executeUpdate();
        }
    }

    public List<Application> findAllForAdmin() throws SQLException {
        String sql = "SELECT a.*, s.name AS scholarship_name, s.provider AS scholarship_provider, " +
                "s.tracking_url AS tracking_url, u.full_name AS student_name " +
                "FROM applications a " +
                "JOIN scholarships s ON a.scholarship_id = s.scholarship_id " +
                "JOIN users u ON a.student_id = u.user_id " +
                "ORDER BY a.last_updated DESC";
        List<Application> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Application a = mapRowWithScholarship(rs);
                a.setStudentName(rs.getString("student_name"));
                list.add(a);
            }
        }
        return list;
    }

    public int countByStudent(long studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM applications WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countByStudentAndStatus(long studentId, String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM applications WHERE student_id = ? AND status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countAll() throws SQLException {
        return countWhere("1=1");
    }

    public int countPending() throws SQLException {
        return countWhere("status IN ('APPLIED','UNDER_REVIEW','PENDING')");
    }

    private int countWhere(String whereClause) throws SQLException {
        String sql = "SELECT COUNT(*) FROM applications WHERE " + whereClause;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Application mapRowWithScholarship(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setApplicationId(rs.getLong("application_id"));
        a.setStudentId(rs.getLong("student_id"));
        a.setScholarshipId(rs.getLong("scholarship_id"));
        a.setAppliedDate(rs.getDate("applied_date"));
        a.setStatus(rs.getString("status"));
        a.setLastUpdated(rs.getTimestamp("last_updated"));
        a.setNotes(rs.getString("notes"));
        a.setScholarshipName(rs.getString("scholarship_name"));
        a.setScholarshipProvider(rs.getString("scholarship_provider"));
        a.setTrackingUrl(rs.getString("tracking_url"));
        return a;
    }
}
