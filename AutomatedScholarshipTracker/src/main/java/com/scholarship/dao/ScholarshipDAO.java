package com.scholarship.dao;

import com.scholarship.model.Scholarship;
import com.scholarship.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the `scholarships` table, including search and the
 * multi-criteria filter used by the Scholarship List page. All filtering
 * is pushed down into SQL with PreparedStatement rather than fetching
 * every row and filtering in Java.
 */
public class ScholarshipDAO {

    public List<Scholarship> findAllActive() throws SQLException {
        String sql = "SELECT * FROM scholarships WHERE status = 'ACTIVE' ORDER BY application_deadline ASC";
        List<Scholarship> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Scholarship> findAll() throws SQLException {
        String sql = "SELECT * FROM scholarships ORDER BY scholarship_id DESC";
        List<Scholarship> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Scholarship findById(long id) throws SQLException {
        String sql = "SELECT * FROM scholarships WHERE scholarship_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Search by name / provider / keyword (uses SQL LIKE, case-insensitive by default collation). */
    public List<Scholarship> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM scholarships WHERE status = 'ACTIVE' AND " +
                "(name LIKE ? OR provider LIKE ? OR description LIKE ?) ORDER BY application_deadline ASC";
        List<Scholarship> list = new ArrayList<>();
        String like = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Multi-criteria filter. Any parameter left null/blank is ignored
     * (unrestricted). category/course/state are matched against the
     * comma-separated eligibility lists or "ALL".
     */
    public List<Scholarship> filter(String category, String course, String state,
                                     BigDecimal maxIncome, BigDecimal minCgpaCeiling,
                                     String scholarshipType) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM scholarships WHERE status = 'ACTIVE' ");
        List<Object> params = new ArrayList<>();

        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("ALL")) {
            sql.append("AND (eligible_categories = 'ALL' OR FIND_IN_SET(?, eligible_categories) > 0) ");
            params.add(category);
        }
        if (course != null && !course.isEmpty() && !course.equalsIgnoreCase("ALL")) {
            sql.append("AND (eligible_courses = 'ALL' OR FIND_IN_SET(?, eligible_courses) > 0) ");
            params.add(course);
        }
        if (state != null && !state.isEmpty() && !state.equalsIgnoreCase("ALL")) {
            sql.append("AND (eligible_states = 'ALL' OR FIND_IN_SET(?, eligible_states) > 0) ");
            params.add(state);
        }
        if (maxIncome != null) {
            sql.append("AND (maximum_family_income IS NULL OR maximum_family_income >= ?) ");
            params.add(maxIncome);
        }
        if (minCgpaCeiling != null) {
            sql.append("AND (minimum_cgpa IS NULL OR minimum_cgpa <= ?) ");
            params.add(minCgpaCeiling);
        }
        if (scholarshipType != null && !scholarshipType.isEmpty() && !scholarshipType.equalsIgnoreCase("ALL")) {
            sql.append("AND scholarship_type = ? ");
            params.add(scholarshipType);
        }
        sql.append("ORDER BY application_deadline ASC");

        List<Scholarship> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Scholarship> searchAdmin(String keyword) throws SQLException {
        String sql = "SELECT * FROM scholarships WHERE name LIKE ? OR provider LIKE ? ORDER BY scholarship_id DESC";
        String like = "%" + keyword + "%";
        List<Scholarship> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public long insert(Scholarship s) throws SQLException {
        String sql = "INSERT INTO scholarships (name, provider, description, scholarship_type, amount, " +
                "eligibility_description, minimum_cgpa, minimum_percentage, maximum_family_income, " +
                "eligible_categories, eligible_courses, eligible_states, minimum_year, maximum_year, " +
                "gender_eligibility, disability_required, minority_required, application_start_date, " +
                "application_deadline, application_url, tracking_url, status) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, s);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    public void update(Scholarship s) throws SQLException {
        String sql = "UPDATE scholarships SET name=?, provider=?, description=?, scholarship_type=?, amount=?, " +
                "eligibility_description=?, minimum_cgpa=?, minimum_percentage=?, maximum_family_income=?, " +
                "eligible_categories=?, eligible_courses=?, eligible_states=?, minimum_year=?, maximum_year=?, " +
                "gender_eligibility=?, disability_required=?, minority_required=?, application_start_date=?, " +
                "application_deadline=?, application_url=?, tracking_url=?, status=? WHERE scholarship_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int nextIndex = bind(ps, s);
            ps.setLong(nextIndex, s.getScholarshipId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM scholarships WHERE scholarship_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public int countActive() throws SQLException {
        return countWhere("status = 'ACTIVE' AND application_deadline >= CURDATE()");
    }

    public int countExpired() throws SQLException {
        return countWhere("application_deadline < CURDATE()");
    }

    public int countAll() throws SQLException {
        return countWhere("1=1");
    }

    private int countWhere(String whereClause) throws SQLException {
        String sql = "SELECT COUNT(*) FROM scholarships WHERE " + whereClause;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private int bind(PreparedStatement ps, Scholarship s) throws SQLException {
        int i = 1;
        ps.setString(i++, s.getName());
        ps.setString(i++, s.getProvider());
        ps.setString(i++, s.getDescription());
        ps.setString(i++, s.getScholarshipType());
        ps.setString(i++, s.getAmount());
        ps.setString(i++, s.getEligibilityDescription());
        setDecimal(ps, i++, s.getMinimumCgpa());
        setDecimal(ps, i++, s.getMinimumPercentage());
        setDecimal(ps, i++, s.getMaximumFamilyIncome());
        ps.setString(i++, s.getEligibleCategories());
        ps.setString(i++, s.getEligibleCourses());
        ps.setString(i++, s.getEligibleStates());
        setInteger(ps, i++, s.getMinimumYear());
        setInteger(ps, i++, s.getMaximumYear());
        ps.setString(i++, s.getGenderEligibility());
        ps.setBoolean(i++, s.isDisabilityRequired());
        ps.setBoolean(i++, s.isMinorityRequired());
        setDate(ps, i++, s.getApplicationStartDate());
        setDate(ps, i++, s.getApplicationDeadline());
        ps.setString(i++, s.getApplicationUrl());
        ps.setString(i++, s.getTrackingUrl());
        ps.setString(i++, s.getStatus());
        return i;
    }

    private void setDate(PreparedStatement ps, int idx, Date date) throws SQLException {
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

    private Scholarship mapRow(ResultSet rs) throws SQLException {
        Scholarship s = new Scholarship();
        s.setScholarshipId(rs.getLong("scholarship_id"));
        s.setName(rs.getString("name"));
        s.setProvider(rs.getString("provider"));
        s.setDescription(rs.getString("description"));
        s.setScholarshipType(rs.getString("scholarship_type"));
        s.setAmount(rs.getString("amount"));
        s.setEligibilityDescription(rs.getString("eligibility_description"));
        s.setMinimumCgpa(rs.getBigDecimal("minimum_cgpa"));
        s.setMinimumPercentage(rs.getBigDecimal("minimum_percentage"));
        s.setMaximumFamilyIncome(rs.getBigDecimal("maximum_family_income"));
        s.setEligibleCategories(rs.getString("eligible_categories"));
        s.setEligibleCourses(rs.getString("eligible_courses"));
        s.setEligibleStates(rs.getString("eligible_states"));
        int minYear = rs.getInt("minimum_year");
        s.setMinimumYear(rs.wasNull() ? null : minYear);
        int maxYear = rs.getInt("maximum_year");
        s.setMaximumYear(rs.wasNull() ? null : maxYear);
        s.setGenderEligibility(rs.getString("gender_eligibility"));
        s.setDisabilityRequired(rs.getBoolean("disability_required"));
        s.setMinorityRequired(rs.getBoolean("minority_required"));
        s.setApplicationStartDate(rs.getDate("application_start_date"));
        s.setApplicationDeadline(rs.getDate("application_deadline"));
        s.setApplicationUrl(rs.getString("application_url"));
        s.setTrackingUrl(rs.getString("tracking_url"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}
