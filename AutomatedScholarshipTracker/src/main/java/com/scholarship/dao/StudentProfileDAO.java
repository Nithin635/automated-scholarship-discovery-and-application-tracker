package com.scholarship.dao;

import com.scholarship.model.StudentProfile;
import com.scholarship.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Data access for the `student_profiles` table.
 */
public class StudentProfileDAO {

    public StudentProfile findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM student_profiles WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean exists(long userId) throws SQLException {
        String sql = "SELECT 1 FROM student_profiles WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Inserts a brand new profile row (called once, right after registration). */
    public void insert(StudentProfile p) throws SQLException {
        String sql = "INSERT INTO student_profiles " +
                "(user_id, date_of_birth, gender, state, category, course, college_name, year_of_study, " +
                " cgpa, percentage, annual_family_income, disability_status, minority_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindProfile(ps, p);
            ps.executeUpdate();
        }
    }

    /** Updates an existing profile row (used by "Edit Profile"). */
    public void update(StudentProfile p) throws SQLException {
        String sql = "UPDATE student_profiles SET date_of_birth=?, gender=?, state=?, category=?, course=?, " +
                "college_name=?, year_of_study=?, cgpa=?, percentage=?, annual_family_income=?, " +
                "disability_status=?, minority_status=? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = bindProfileFieldsOnly(ps, p);
            ps.setLong(i, p.getUserId());
            ps.executeUpdate();
        }
    }

    /** Convenience "upsert" used by ProfileServlet so callers don't need to check existence first. */
    public void save(StudentProfile p) throws SQLException {
        if (exists(p.getUserId())) {
            update(p);
        } else {
            insert(p);
        }
    }

    private void bindProfile(PreparedStatement ps, StudentProfile p) throws SQLException {
        ps.setLong(1, p.getUserId());
        int i = bindProfileFieldsOnly(ps, p, 2);
    }

    // Overload used by insert(): binds user_id at index 1, then the rest starting at 2.
    private int bindProfileFieldsOnly(PreparedStatement ps, StudentProfile p, int startIndex) throws SQLException {
        int i = startIndex;
        setDate(ps, i++, p.getDateOfBirth());
        ps.setString(i++, p.getGender());
        ps.setString(i++, p.getState());
        ps.setString(i++, p.getCategory());
        ps.setString(i++, p.getCourse());
        ps.setString(i++, p.getCollegeName());
        setInteger(ps, i++, p.getYearOfStudy());
        setDecimal(ps, i++, p.getCgpa());
        setDecimal(ps, i++, p.getPercentage());
        setDecimal(ps, i++, p.getAnnualFamilyIncome());
        ps.setBoolean(i++, p.isDisabilityStatus());
        ps.setBoolean(i++, p.isMinorityStatus());
        return i;
    }

    // Overload used by update(): fields start at index 1 (no user_id first), returns next free index.
    private int bindProfileFieldsOnly(PreparedStatement ps, StudentProfile p) throws SQLException {
        return bindProfileFieldsOnly(ps, p, 1);
    }

    private void setDate(PreparedStatement ps, int idx, java.sql.Date date) throws SQLException {
        if (date == null) ps.setNull(idx, Types.DATE);
        else ps.setDate(idx, date);
    }

    private void setInteger(PreparedStatement ps, int idx, Integer value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.TINYINT);
        else ps.setInt(idx, value);
    }

    private void setDecimal(PreparedStatement ps, int idx, BigDecimal value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DECIMAL);
        else ps.setBigDecimal(idx, value);
    }

    private StudentProfile mapRow(ResultSet rs) throws SQLException {
        StudentProfile p = new StudentProfile();
        p.setProfileId(rs.getLong("profile_id"));
        p.setUserId(rs.getLong("user_id"));
        p.setDateOfBirth(rs.getDate("date_of_birth"));
        p.setGender(rs.getString("gender"));
        p.setState(rs.getString("state"));
        p.setCategory(rs.getString("category"));
        p.setCourse(rs.getString("course"));
        p.setCollegeName(rs.getString("college_name"));
        int year = rs.getInt("year_of_study");
        p.setYearOfStudy(rs.wasNull() ? null : year);
        p.setCgpa(rs.getBigDecimal("cgpa"));
        p.setPercentage(rs.getBigDecimal("percentage"));
        p.setAnnualFamilyIncome(rs.getBigDecimal("annual_family_income"));
        p.setDisabilityStatus(rs.getBoolean("disability_status"));
        p.setMinorityStatus(rs.getBoolean("minority_status"));
        return p;
    }
}
