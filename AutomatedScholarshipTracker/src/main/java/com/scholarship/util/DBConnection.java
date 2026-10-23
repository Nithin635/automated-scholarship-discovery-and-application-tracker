package com.scholarship.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized JDBC connection utility.
 *
 * ================================================================
 *  ENTER YOUR MYSQL PASSWORD ON THE LINE BELOW MARKED  "DB_PASSWORD"
 * ================================================================
 * This is the ONLY place database credentials live in the whole
 * project — no JSP or servlet ever opens its own connection or
 * hard-codes credentials.
 */
public final class DBConnection {

    // ---- EDIT THESE THREE VALUES FOR YOUR LOCAL MYSQL SETUP ----

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/scholarship_tracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";

    private static final String DB_USERNAME =
            System.getenv().getOrDefault("DB_USERNAME", "root");

    private static final String DB_PASSWORD =
            System.getenv().getOrDefault("DB_PASSWORD", ""); // <-- put your MySQL password here
    // --------------------------------------------------------------

    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found on classpath.", e);
        }
    }

    private DBConnection() {
        // utility class, no instances
    }

    /**
     * Opens a brand new JDBC connection. Callers are responsible for
     * closing it (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
}
