-- =====================================================================
-- Automated Scholarship Discovery and Application Tracker
-- Complete MySQL Database Script
-- =====================================================================
-- Run this entire file once (MySQL Workbench "Execute" or
-- `mysql -u root -p < database.sql`) to create the database, tables,
-- constraints, indexes and seed data.
-- =====================================================================

DROP DATABASE IF EXISTS scholarship_tracker;
CREATE DATABASE scholarship_tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE scholarship_tracker;

-- ---------------------------------------------------------------------
-- TABLE: users
-- Stores login credentials and role for both students and admins.
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(120)  NOT NULL,
    email           VARCHAR(150)  NOT NULL UNIQUE,
    password_hash   VARCHAR(100)  NOT NULL,
    role            ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
    phone_number    VARCHAR(20)   NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- TABLE: student_profiles
-- One-to-one extension of users for STUDENT role: academic/eligibility data.
-- ---------------------------------------------------------------------
CREATE TABLE student_profiles (
    profile_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT NOT NULL UNIQUE,
    date_of_birth         DATE,
    gender                ENUM('MALE','FEMALE','OTHER') DEFAULT NULL,
    state                 VARCHAR(60),
    category              ENUM('GENERAL','OBC','SC','ST','EWS') DEFAULT 'GENERAL',
    course                VARCHAR(100),
    college_name          VARCHAR(150),
    year_of_study         TINYINT,
    cgpa                  DECIMAL(4,2),
    percentage            DECIMAL(5,2),
    annual_family_income  DECIMAL(12,2),
    disability_status     BOOLEAN DEFAULT FALSE,
    minority_status       BOOLEAN DEFAULT FALSE,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_cgpa CHECK (cgpa IS NULL OR (cgpa >= 0 AND cgpa <= 10)),
    CONSTRAINT chk_percentage CHECK (percentage IS NULL OR (percentage >= 0 AND percentage <= 100)),
    CONSTRAINT chk_income CHECK (annual_family_income IS NULL OR annual_family_income >= 0),
    CONSTRAINT chk_year CHECK (year_of_study IS NULL OR (year_of_study BETWEEN 1 AND 6))
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- TABLE: scholarships
-- Master data for every scholarship the system knows about.
-- Comma-separated lists (eligible_categories/courses/states) are used
-- deliberately to keep the schema simple for a college project; a
-- fully-normalized design would use junction tables (documented in README).
-- ---------------------------------------------------------------------
CREATE TABLE scholarships (
    scholarship_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                     VARCHAR(200) NOT NULL,
    provider                 VARCHAR(150) NOT NULL,
    description              TEXT,
    scholarship_type         ENUM('GOVERNMENT','PRIVATE','INSTITUTIONAL','NGO') NOT NULL DEFAULT 'GOVERNMENT',
    amount                   VARCHAR(100),
    eligibility_description  TEXT,
    minimum_cgpa             DECIMAL(4,2)  DEFAULT NULL,
    minimum_percentage       DECIMAL(5,2)  DEFAULT NULL,
    maximum_family_income    DECIMAL(12,2) DEFAULT NULL,
    eligible_categories      VARCHAR(150)  DEFAULT 'ALL',
    eligible_courses         VARCHAR(200)  DEFAULT 'ALL',
    eligible_states          VARCHAR(300)  DEFAULT 'ALL',
    minimum_year             TINYINT       DEFAULT NULL,
    maximum_year              TINYINT       DEFAULT NULL,
    gender_eligibility       ENUM('ALL','MALE','FEMALE') DEFAULT 'ALL',
    disability_required      BOOLEAN DEFAULT FALSE,
    minority_required        BOOLEAN DEFAULT FALSE,
    application_start_date   DATE,
    application_deadline     DATE NOT NULL,
    application_url          VARCHAR(500) NOT NULL,
    tracking_url             VARCHAR(500) DEFAULT NULL,
    status                   ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sch_deadline (application_deadline),
    INDEX idx_sch_status (status),
    INDEX idx_sch_name (name),
    FULLTEXT INDEX ft_sch_search (name, provider, description)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- TABLE: applications
-- Tracks each student's application to a scholarship.
-- ---------------------------------------------------------------------
CREATE TABLE applications (
    application_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id        BIGINT NOT NULL,
    scholarship_id    BIGINT NOT NULL,
    applied_date       DATE NOT NULL,
    status             ENUM('APPLIED','UNDER_REVIEW','APPROVED','REJECTED','PENDING') NOT NULL DEFAULT 'APPLIED',
    last_updated        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    notes               VARCHAR(500),
    CONSTRAINT fk_app_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_app_scholarship FOREIGN KEY (scholarship_id) REFERENCES scholarships(scholarship_id) ON DELETE CASCADE,
    CONSTRAINT uq_student_scholarship UNIQUE (student_id, scholarship_id),
    INDEX idx_app_student (student_id),
    INDEX idx_app_status (status)
) ENGINE=InnoDB;

-- =====================================================================
-- SEED DATA
-- =====================================================================

-- Passwords below are real BCrypt hashes (never plain text).
-- Demo Admin  -> email: admin@scholarship.com   password: Admin@123
-- Demo Student-> email: student@scholarship.com password: Student@123
-- These hashes were generated with a $2a$ cost factor of 12 and verify
-- correctly through PasswordUtil.java (backed by the jBCrypt library) at
-- login time. If you register new accounts through the app, fresh hashes
-- are generated automatically — you never need to hash a password by hand.
INSERT INTO users (full_name, email, password_hash, role, phone_number) VALUES
('System Administrator', 'admin@scholarship.com', '$2a$12$gfb0j3R3HUlxpb/sU2N8RO4VEUTnvkewU1psHFy6de0cOcouNHEW2', 'ADMIN', '9999999999'),
('Nithin Kumar', 'student@scholarship.com', '$2a$12$Ms6GCSiHLPU0zVG1TlZpnuIPSwXd/4sadkOJn/dDhUOM.fetMDLE2', 'STUDENT', '9876543210');

INSERT INTO student_profiles
(user_id, date_of_birth, gender, state, category, course, college_name, year_of_study, cgpa, percentage, annual_family_income, disability_status, minority_status)
VALUES
(2, '2004-05-14', 'MALE', 'Karnataka', 'OBC', 'B.Tech', 'PES University', 3, 8.20, 82.00, 200000.00, FALSE, FALSE);

-- ---------------------------------------------------------------------
-- SCHOLARSHIPS (real Indian scholarship programs; official URLs verified
-- via web search at the time this script was generated — Aug 2026).
-- Deadlines below are illustrative demo values for the current
-- application cycle; always confirm the live deadline on the official
-- site before relying on it, since scholarship deadlines change yearly.
-- ---------------------------------------------------------------------
INSERT INTO scholarships
(name, provider, description, scholarship_type, amount, eligibility_description,
 minimum_cgpa, minimum_percentage, maximum_family_income, eligible_categories, eligible_courses, eligible_states,
 minimum_year, maximum_year, gender_eligibility, disability_required, minority_required,
 application_start_date, application_deadline, application_url, tracking_url, status)
VALUES
('National Means-cum-Merit Scholarship (NMMS)', 'Ministry of Education, Govt. of India',
 'Central government scholarship providing financial assistance to meritorious students from economically weaker sections to reduce the dropout rate at the secondary stage. Applications and renewals are processed centrally through the National Scholarship Portal.',
 'GOVERNMENT', '₹12,000 per year', 'Merit-cum-means based; family income must be below the prescribed limit and the student must clear the qualifying selection test.',
 NULL, 55.00, 350000.00, 'ALL', 'ALL', 'ALL', 1, 6, 'ALL', FALSE, FALSE,
 '2026-07-01', '2026-10-31', 'https://scholarships.gov.in/', 'https://scholarships.gov.in/', 'ACTIVE'),

('Post-Matric Scholarship for SC Students', 'Ministry of Social Justice and Empowerment, Govt. of India',
 'Central Sector scheme reimbursing tuition fees, maintenance allowance and other charges for Scheduled Caste students studying at the post-matriculation/post-secondary stage, applied for through the National Scholarship Portal.',
 'GOVERNMENT', 'Full tuition fee reimbursement + maintenance allowance', 'Applicant must belong to the Scheduled Caste (SC) category and satisfy the income ceiling notified for the state.',
 NULL, NULL, 250000.00, 'SC', 'ALL', 'ALL', 1, 6, 'ALL', FALSE, FALSE,
 '2026-07-15', '2026-11-30', 'https://scholarships.gov.in/', 'https://scholarships.gov.in/', 'ACTIVE'),

('Post-Matric Scholarship for OBC Students', 'Ministry of Social Justice and Empowerment, Govt. of India',
 'Central government scheme that supports Other Backward Class students pursuing post-matriculation studies with tuition fee reimbursement and a maintenance allowance, applied for through the National Scholarship Portal.',
 'GOVERNMENT', 'Up to ₹20,000 per year (course-dependent)', 'Applicant must belong to the OBC category (non-creamy layer) and satisfy the income ceiling.',
 NULL, NULL, 300000.00, 'OBC', 'ALL', 'ALL', 1, 6, 'ALL', FALSE, FALSE,
 '2026-07-15', '2026-11-30', 'https://scholarships.gov.in/', 'https://scholarships.gov.in/', 'ACTIVE'),

('Central Sector Scheme of Scholarship for College and University Students', 'Department of Higher Education, Govt. of India',
 'Merit-based scholarship for students who rank within the top percentile of their Class XII board results and are pursuing regular degree courses, administered on the National Scholarship Portal.',
 'GOVERNMENT', '₹10,000 – ₹20,000 per year', 'Student must be among the top-performing Class XII candidates of their board and enrolled in a regular undergraduate course.',
 NULL, 80.00, 800000.00, 'ALL', 'ALL', 'ALL', 1, 3, 'ALL', FALSE, FALSE,
 '2026-08-01', '2026-10-31', 'https://scholarships.gov.in/', 'https://scholarships.gov.in/', 'ACTIVE'),

('AICTE Pragati Scholarship for Girl Students', 'All India Council for Technical Education (AICTE)',
 'Scholarship exclusively for female students in their 1st year (or 2nd year via lateral entry) of an AICTE-approved technical diploma or degree programme, to support fees, books, laptop and equipment purchases.',
 'GOVERNMENT', '₹50,000 per year', 'Female candidates admitted to an AICTE-approved diploma/degree technical programme; maximum two girls per family may apply.',
 NULL, NULL, 800000.00, 'ALL', 'Engineering,Diploma', 'ALL', 1, 2, 'FEMALE', FALSE, FALSE,
 '2026-08-01', '2026-10-31', 'https://scholarships.gov.in/', 'https://scholarships.gov.in/', 'ACTIVE'),

('AICTE Saksham Scholarship for Specially-Abled Students', 'All India Council for Technical Education (AICTE)',
 'Scholarship for specially-abled students (with benchmark disability of 40% or more) pursuing AICTE-approved technical diploma or degree programmes.',
 'GOVERNMENT', '₹50,000 per year', 'Candidate must have a benchmark disability of 40% or more and be enrolled in an AICTE-approved technical programme.',
 NULL, NULL, 800000.00, 'ALL', 'Engineering,Diploma', 'ALL', 1, 4, 'ALL', TRUE, FALSE,
 '2026-08-01', '2026-10-31', 'https://scholarships.gov.in/', 'https://scholarships.gov.in/', 'ACTIVE'),

('Reliance Foundation Undergraduate Scholarship', 'Reliance Foundation',
 'One of India''s largest merit-cum-means undergraduate scholarship programmes, supporting first-year students across all disciplines with a lump-sum grant paid over the duration of the degree, based on academic merit and an aptitude test.',
 'PRIVATE', 'Up to ₹2,00,000 over the course', 'Student must be in the first year of an undergraduate programme; preference given to household income below ₹2.5 lakh (overall ceiling ₹15 lakh).',
 NULL, NULL, 1500000.00, 'ALL', 'ALL', 'ALL', 1, 1, 'ALL', FALSE, FALSE,
 '2026-08-01', '2026-10-15', 'https://www.scholarships.reliancefoundation.org/', 'https://scholarshipportal.reliancefoundation.org/LoginScholar', 'ACTIVE'),

('J N Tata Endowment Loan Scholarship', 'J N Tata Endowment (Tata Trusts)',
 'India''s oldest loan-scholarship programme (established 1892), offering low-interest loan scholarships to Indian students pursuing Master''s, PhD or postdoctoral studies overseas, purely on merit.',
 'PRIVATE', 'Up to ₹20,00,000 (loan scholarship, 2% interest)', 'Graduate degree from a recognized Indian university with strong academic record; must be pursuing postgraduate study abroad.',
 6.00, 60.00, NULL, 'ALL', 'ALL', 'ALL', NULL, NULL, 'ALL', FALSE, FALSE,
 '2026-01-05', '2026-03-29', 'https://jntataendowment.org/', NULL, 'ACTIVE'),

('Sitaram Jindal Foundation Scholarship Scheme', 'Sitaram Jindal Foundation',
 'Merit-cum-means scholarship supporting students from Class 11 through postgraduation (General, Medical and Engineering streams) with a monthly stipend covering tuition and related expenses.',
 'NGO', '₹500 – ₹3,200 per month (course-dependent)', 'Enrolled in Class 11/12, ITI, diploma, graduate or postgraduate programme; family income below ₹2.5–4 lakh depending on employment type.',
 NULL, 50.00, 400000.00, 'ALL', 'ALL', 'ALL', 1, 6, 'ALL', FALSE, FALSE,
 '2026-01-01', '2026-09-10', 'https://www.sitaramjindalfoundation.org/', NULL, 'ACTIVE'),

('National Fellowship for OBC Students', 'University Grants Commission (UGC), Govt. of India',
 'Fellowship supporting OBC candidates pursuing M.Phil and PhD degrees in Indian universities/institutions, covering a monthly fellowship amount and contingency grant, applied for through the National Scholarship Portal.',
 'GOVERNMENT', '₹31,000 – ₹35,000 per month', 'Candidate must belong to the OBC (non-creamy layer) category and be registered for M.Phil/PhD in a recognized university.',
 6.00, NULL, 600000.00, 'OBC', 'ALL', 'ALL', NULL, NULL, 'ALL', FALSE, FALSE,
 '2026-07-01', '2026-12-15', 'https://scholarships.gov.in/', 'https://scholarships.gov.in/', 'ACTIVE'),

('Karnataka Post-Matric Scholarship (State Scheme)', 'Department of Backward Classes Welfare, Govt. of Karnataka',
 'State-run scholarship for students of Karnataka domicile from SC/ST/OBC/Minority backgrounds studying at the post-matriculation level, routed through the National Scholarship Portal state module.',
 'GOVERNMENT', 'Tuition fee reimbursement + maintenance allowance', 'Student must have Karnataka domicile and belong to SC, ST, OBC or minority category with income within the state-notified ceiling.',
 NULL, NULL, 250000.00, 'OBC,SC,ST', 'ALL', 'Karnataka', 1, 6, 'ALL', FALSE, FALSE,
 '2026-07-01', '2026-11-15', 'https://scholarships.gov.in/', 'https://scholarships.gov.in/', 'ACTIVE'),

('Expired Demo Scholarship (For Testing Closed Status)', 'Demo Provider',
 'Demo record included only to demonstrate that the application correctly marks past-deadline scholarships as closed instead of active opportunities.',
 'INSTITUTIONAL', '₹10,000', 'Demo eligibility: open to all categories and courses.',
 NULL, NULL, 500000.00, 'ALL', 'ALL', 'ALL', 1, 4, 'ALL', FALSE, FALSE,
 '2025-01-01', '2025-06-30', 'https://scholarships.gov.in/', NULL, 'ACTIVE');

-- ---------------------------------------------------------------------
-- Sample application row so "My Applications" is non-empty on first run.
-- ---------------------------------------------------------------------
INSERT INTO applications (student_id, scholarship_id, applied_date, status, notes)
VALUES (2, 5, '2026-08-10', 'APPLIED', 'Submitted online application; awaiting document verification.');

-- =====================================================================
-- End of database.sql
-- =====================================================================
