# Automated Scholarship Discovery and Application Tracker

A Java web application (Servlets + JSP + JDBC + MySQL) that lets students build one
profile, automatically see which scholarships they're eligible for, apply on the
official provider websites, and track every application from a single dashboard.
Admins manage the scholarship catalogue and monitor applications.

**Stack:** Java 24 (compiled to bytecode release 17 for Tomcat 9 compatibility) ·
Java Servlets (`javax.servlet.*`) · JSP · JSTL · JDBC · MySQL · Apache Tomcat 9 ·
Maven · Eclipse. No Spring, Hibernate, or JS frameworks — deliberately, so the
whole stack is easy to explain in a viva.

---

## 1. Project Structure

```
AutomatedScholarshipTracker/
├── pom.xml
├── README.md
├── database.sql
└── src/main/
    ├── java/com/scholarship/
    │   ├── controller/   20 servlets (auth, student, admin)
    │   ├── dao/          UserDAO, StudentProfileDAO, ScholarshipDAO, ApplicationDAO
    │   ├── model/        User, StudentProfile, Scholarship, Application, EligibilityResult
    │   ├── service/      AuthService, EligibilityService, ApplicationService
    │   ├── util/         DBConnection, PasswordUtil, ValidationUtil
    │   └── filter/       AuthenticationFilter, AdminAuthorizationFilter
    └── webapp/
        ├── WEB-INF/
        │   ├── web.xml
        │   └── includes/   shared JSP fragments (navbar, sidebar, alerts, form fields)
        ├── css/style.css
        ├── js/main.js
        ├── index.jsp, login.jsp, register.jsp, forgot-password.jsp
        ├── student/        dashboard, profile, scholarships, scholarship-details, applications
        ├── admin/          dashboard, scholarships, add/edit-scholarship, applications, students
        └── error/          404.jsp, 500.jsp
```

### A note on two intentional consolidations

The original spec lists `ProfileServlet` + `UpdateProfileServlet` and
`SearchScholarshipServlet` + `FilterScholarshipServlet` as separate servlets. In
this implementation:

- **`ProfileServlet`** handles both viewing (`GET`) and saving (`POST`) the
  profile, because they operate on the exact same resource and a single edit
  form (not a separate read-only + edit page) is simpler to maintain.
- **`ScholarshipListServlet`** handles plain listing, keyword search (`?q=`) and
  multi-criteria filtering (`?category=`, `?course=`, ...) in one controller,
  because all three render the identical JSP with the identical
  eligible/not-eligible annotation logic. Splitting them would have meant
  duplicating that annotation code in three places.

Everything else maps 1:1 to the spec's servlet list.

---

## 2. Database Design

Four tables: `users`, `student_profiles`, `scholarships`, `applications`.

- `student_profiles.user_id` and `applications.student_id` both have a foreign
  key to `users.user_id` (`ON DELETE CASCADE`).
- `applications.scholarship_id` has a foreign key to `scholarships.scholarship_id`.
- `applications` has a `UNIQUE (student_id, scholarship_id)` constraint — this is
  what prevents duplicate applications at the database level, in addition to the
  application-level check in `ApplicationDAO.hasApplied()`.
- `eligible_categories` / `eligible_courses` / `eligible_states` are stored as
  comma-separated strings (or the literal `ALL` meaning "unrestricted"). A fully
  normalized design would use three junction tables instead; the simpler design
  was chosen deliberately so the schema, and the `EligibilityService` logic that
  reads it, stay easy to explain in a viva. This is documented here rather than
  hidden.
- `CHECK` constraints enforce CGPA 0–10, percentage 0–100, non-negative income,
  and year-of-study 1–6 at the database layer, on top of the same checks in
  `ValidationUtil` at the application layer.

Run `database.sql` once to create everything, including seed data.

---

## 3. Real Scholarship Data — Sourcing Note

`database.sql` seeds 12 scholarship rows. Ten are real, currently-operating
Indian scholarship programmes with **official** application URLs, verified by
web search while this project was generated (August 2026):

| Scholarship | Official URL |
|---|---|
| National Means-cum-Merit Scholarship, Post-Matric SC/OBC, Central Sector Scheme, AICTE Pragati/Saksham, National Fellowship for OBC, Karnataka Post-Matric | `scholarships.gov.in` (National Scholarship Portal — the real single-window government portal) |
| Reliance Foundation Undergraduate Scholarship | `scholarships.reliancefoundation.org` |
| J N Tata Endowment Loan Scholarship | `jntataendowment.org` |
| Sitaram Jindal Foundation Scholarship Scheme | `sitaramjindalfoundation.org` |

Amounts, exact eligibility cut-offs and deadlines for any scholarship change
year to year — treat every figure in `database.sql` as **demo data for a
college project**, not a guarantee of the live 2026-27 terms. Always check the
official site before relying on a number. Tracking URLs were only filled in
where a genuine official tracking/status page was found (the NSP portal itself,
and Reliance Foundation's applicant login); everywhere else `tracking_url` is
left `NULL` on purpose, and the UI shows *"Official tracking information is not
available. Please visit the scholarship provider's website."* rather than
inventing a link. One additional row, "Expired Demo Scholarship", is clearly
labelled as a demo record used only to test the closed/expired badge — it is not
a real scholarship.

---

## 4. Eclipse + Tomcat 9 + MySQL Setup

1. **Install Java 24** — from [https://jdk.java.net](https://jdk.java.net) or Oracle.
   Verify with `java -version`.
2. **Install Eclipse IDE for Enterprise Java and Web Developers** (Eclipse IDE
   for Java EE Developers works too) from [https://www.eclipse.org/downloads/](https://www.eclipse.org/downloads/).
3. **Install and configure Tomcat 9**
   - Download the Tomcat 9 core ZIP/tar.gz from
     [https://tomcat.apache.org/download-90.cgi](https://tomcat.apache.org/download-90.cgi) and unzip it anywhere,
     e.g. `C:\tomcat9` or `~/tomcat9`.
   - In Eclipse: **Window → Preferences → Server → Runtime Environments → Add →
     Apache Tomcat v9.0**, then browse to the folder you unzipped.
4. **Install MySQL** (MySQL Community Server 8.x) and MySQL Workbench, or just
   the `mysql` CLI client.
5. **Create the database.**
   - Using MySQL Workbench: open `database.sql`, click the lightning-bolt
     "Execute" button to run the whole script.
   - Or from the command line:
     ```bash
     mysql -u root -p < database.sql
     ```
   This creates the `scholarship_tracker` database, all four tables, and the
   seed data (demo admin, demo student, 12 scholarships).
6. **Import the Maven project into Eclipse**
   - `File → Import → Maven → Existing Maven Projects`, point it at the
     `AutomatedScholarshipTracker` folder, click Finish. Eclipse will download
     the dependencies listed in `pom.xml` (MySQL Connector/J, JSTL, jBCrypt,
     the Servlet/JSP APIs marked `provided`).
7. **Configure your database credentials**
   - Open `src/main/java/com/scholarship/util/DBConnection.java`.
   - Change the line `DB_PASSWORD = "YOUR_MYSQL_PASSWORD_HERE"` to your actual
     local MySQL root password. If your MySQL username isn't `root`, change
     `DB_USERNAME` too. **This is the only file in the whole project that
     contains database credentials.**
8. **Add the Tomcat server to Eclipse**
   - `Window → Show View → Servers` → right-click in the Servers view →
     `New → Server` → choose **Apache Tomcat v9.0** → Finish.
9. **Add the project to Tomcat**
   - Double-click the new server entry → in the "Overview" tab click
     **"Add and Remove…"** → move `AutomatedScholarshipTracker` to the
     right-hand "Configured" list → Finish.
10. **Clean / build the project**
    - Right-click the project → `Maven → Update Project…` (check "Force
      Update"), then `Project → Clean…` to force a rebuild.
11. **Start Tomcat**
    - Right-click the server in the Servers view → **Start** (or Debug, if you
      want to step through servlet code).
12. **Open the application in your browser**
    - `http://localhost:8080/AutomatedScholarshipTracker/`

---

## 5. Demo Accounts

| Role | Email | Password |
|---|---|---|
| Admin | `admin@scholarship.com` | `Admin@123` |
| Student | `student@scholarship.com` | `Student@123` |

Both password hashes in `database.sql` are real BCrypt hashes (via jBCrypt,
cost factor 12) generated for exactly these passwords — nothing is stored in
plain text, and you never need to hash a password by hand. New accounts
created through the **Register** page get a fresh hash automatically.

---

## 6. Full Demonstration Script

1. Open the home page, click **Register**, and create a new student account
   (or use `student@scholarship.com` / `Student@123`).
2. Log in — you land on the **Student Dashboard**.
3. Go to **My Profile**, fill in CGPA, percentage, income, category, course,
   state, year of study — watch the **Profile Completion** bar move.
4. Go to **Scholarships** — every card shows an **Eligible** / **Not Eligible**
   badge computed live against your profile.
5. Search for "Reliance" in the search box.
6. Clear search, apply filters: Category = OBC, Max Income = 300000.
7. Click **View Details** on an eligible scholarship — see the full
   eligibility breakdown table.
8. Click **Apply Now** — the official scholarship website opens in a new tab.
9. Return to the ScholarTrack tab, click **Mark as Applied** (confirm the
   prompt) — the application is stored in MySQL and status is `APPLIED`.
10. Go to **My Applications** — see the new row.
11. Change its status dropdown to **Under Review**.
12. Click **Track** — if a tracking URL exists you're sent to the official
    tracker; otherwise you see the "not available" message.
13. **Logout.**
14. Log in as Admin (`admin@scholarship.com` / `Admin@123`).
15. **Admin Dashboard** — see system-wide counts.
16. **Add Scholarship** — create a new scholarship with full eligibility rules.
17. **Manage Scholarships → Edit** an existing one; try **Delete** (confirm in
    the modal).
18. **Applications** — see every student's applications, update one's status.
19. **Students** — see the read-only list of registered students.
20. **Logout.**

---

## 7. Eligibility Engine — Test Cases

`EligibilityService.evaluate(profile, scholarship)` walks ten criteria in
order (CGPA, percentage, income, category, course, state, year range, gender,
disability, minority). Any criterion the scholarship leaves unrestricted
(`NULL` or `"ALL"`) is skipped and never counts against the student.

| Case | Student | Scholarship | Expected Result |
|---|---|---|---|
| 1. Fully eligible | CGPA 8.2, income ₹2L, OBC, B.Tech, Karnataka, Year 3 | Min CGPA 7.0, Max income ₹3L, Categories OBC/SC/ST, Courses "ALL", States "ALL" | **ELIGIBLE** |
| 2. Income too high | Income ₹9L | Max income ₹3L | **NOT ELIGIBLE** — "Family income exceeds the maximum allowed income of ₹300000." |
| 3. CGPA too low | CGPA 6.0 | Min CGPA 7.0 | **NOT ELIGIBLE** — "Your CGPA (6.0) is below the minimum required CGPA of 7.0." |
| 4. Category mismatch | Category = General | Categories = OBC,SC,ST | **NOT ELIGIBLE** — "Your category (GENERAL) does not match the eligible categories (OBC,SC,ST)." |
| 5. Course mismatch | Course = B.Com | Courses = Engineering,Diploma | **NOT ELIGIBLE** — course reason shown |
| 6. Multiple restrictions | Income too high **and** CGPA too low | Both restricted | **NOT ELIGIBLE** — both reasons listed together |

All six were traced through the code by hand: `EligibilityService` never
short-circuits on the first failure — it collects every failing reason so
the UI can show the complete picture, matching the "Reason" example in the
spec.

---

## 8. Security Checklist

- Every SQL query in every DAO uses `PreparedStatement` — no string
  concatenation of user input into SQL anywhere in the project.
- Passwords are hashed with BCrypt (jBCrypt) before being stored; login
  compares hashes via `BCrypt.checkpw`, never plain text.
- `AuthenticationFilter` blocks any unauthenticated request to `/student/*`
  or `/admin/*` (JSPs and servlets alike).
- `AdminAuthorizationFilter` additionally blocks a logged-in **student** from
  reaching any `/admin/*` URL.
- `ApplicationService.updateStatus()` checks that the application being
  modified belongs to the requesting student before allowing an update
  (unless the caller is an admin) — a student cannot change another
  student's application by guessing an ID in the form.
- All error pages (`404.jsp`, `500.jsp`) show a friendly message, never a raw
  Java stack trace; `web.xml` routes uncaught exceptions there.
- `DBConnection.java` is the only file with database credentials — no JSP or
  servlet opens its own connection or embeds a password.

---

## 9. Consistency Review

Performed by hand across the whole generated project:

- **Java** — every servlet's `@WebServlet` path matches the `action="..."`
  and `href="..."` URLs used in the corresponding JSPs (checked link by
  link). Model getters/setters match every `${...}` EL expression used in
  the JSPs (e.g. `Scholarship.isExpired()` → `${s.expired}`,
  `StudentProfile.getCompletionPercentage()` → `${profile.completionPercentage}`).
  DAO method signatures match every call site in the services and servlets.
- **Database** — table/column names in every DAO's SQL match `database.sql`
  exactly (checked column-by-column). Foreign keys and the
  `(student_id, scholarship_id)` unique constraint match the duplicate-check
  logic in `ApplicationDAO.insertApplication()`.
- **JSP** — every form's `action` attribute resolves to a real `@WebServlet`
  mapping; every session attribute read (`sessionScope.user`) is set exactly
  once, in `LoginServlet`, right after a successful login.
- **Authentication** — traced: unauthenticated request to any `/student/*`
  or `/admin/*` URL → redirected to `login.jsp`. Logged-in student
  requesting `/admin/*` → redirected back to the student dashboard with an
  error message. Logout invalidates the session.
- **Application tracker** — traced the full chain: `apply` (external link,
  new tab) → `Mark as Applied` (POST to `ApplyScholarshipServlet`) →
  `ApplicationService.markAsApplied()` checks expiry and duplicates →
  `ApplicationDAO.insertApplication()` (blocked by both the app-level check
  and the DB unique constraint if a duplicate slips through) → row appears
  in `My Applications` → status update form → `TrackApplicationServlet`
  redirects to `tracking_url` or shows the "not available" message.

---

## 10. Project Explanation (Viva Notes)

**What problem does the project solve?** Indian students otherwise have to
check dozens of separate scholarship websites, read each one's eligibility
rules by hand, and keep track of what they've applied to in a notebook or
spreadsheet. This system centralizes discovery (automatic eligibility
matching) and tracking (a single "My Applications" table) while still sending
students to the *official* site to actually apply — it deliberately does not
try to replace or auto-submit government/private application forms.

**How does automatic eligibility checking work?** `EligibilityService` is a
plain Java class with one method, `evaluate(StudentProfile, Scholarship)`. It
compares each of the ten scholarship rule fields to the corresponding profile
field. If the scholarship field is `NULL`/`"ALL"` the check is skipped
(unrestricted); otherwise a failure appends a human-readable reason to an
`EligibilityResult`. The scholarship is "Eligible" only if zero reasons were
added.

**How does JDBC connect Java to MySQL?** `DBConnection.getConnection()` uses
`DriverManager.getConnection(url, username, password)` with the MySQL
Connector/J driver (`com.mysql.cj.jdbc.Driver`, loaded once in a static
block) to open a `java.sql.Connection`. Every DAO method opens a connection
in a try-with-resources block, builds a `PreparedStatement` with `?`
placeholders, binds real Java values to them (never string-concatenates
user input), and closes everything automatically when the block exits.

**Why are Servlets used?** Servlets are the controller layer: they read
`HttpServletRequest` parameters, call into the service/DAO layers, and
either forward to a JSP (`RequestDispatcher.forward`) to render a view or
`sendRedirect` to a new URL after a state-changing action (login, save,
delete) — the standard Post/Redirect/Get pattern that avoids duplicate form
submissions on refresh.

**Why is JSP used?** JSP pages are the view layer — HTML with JSTL
(`<c:forEach>`, `<c:if>`, `<c:choose>`) and EL (`${...}`) for displaying data
the servlet placed into `request` attributes. No Java scriptlet code
(`<% %>`) is used anywhere in this project's JSPs — only JSTL/EL — which is
the recommended modern practice.

**How does authentication work?** `LoginServlet` verifies the email/password
against the hashed value in `users.password_hash` via `PasswordUtil`, then
stores the resulting `User` object in the `HttpSession` under the key
`"user"`. `AuthenticationFilter` checks for that session attribute on every
`/student/*` and `/admin/*` request; `AdminAuthorizationFilter` additionally
checks `user.isAdmin()` for `/admin/*`. `LogoutServlet` calls
`session.invalidate()`.

**How does the application tracker work?** See section 9 above — the flow is
apply externally → mark as applied → row inserted in `applications` → status
can be updated by the student (their own row only) or by an admin (any row)
→ "Track" either redirects to a verified official tracking URL or explains
that one isn't available.

**How does Admin CRUD work?** `ScholarshipDAO` provides `insert`, `update`,
`delete`, `findAll`, `findById` and `searchAdmin`. The four admin servlets
(`AddScholarshipServlet`, `UpdateScholarshipServlet`, `DeleteScholarshipServlet`,
`AdminScholarshipListServlet`) call exactly those methods; `Delete` requires
a JavaScript confirmation modal before the DELETE POST is ever sent.

**What are the major database tables?** `users` (login + role), 
`student_profiles` (1:1 with users, all eligibility fields), `scholarships`
(master catalogue with every eligibility rule), `applications` (join table
between a student and a scholarship, with status history via
`last_updated`).

**What is the role of each layer?**
`JSP` (renders HTML) → `Servlet` (reads request, orchestrates, decides
forward/redirect) → `Service` (business rules: eligibility matching,
application rules, auth rules) → `DAO` (the only layer that touches SQL) →
`MySQL` (persistent storage).

---

## 11. What Was Intentionally Left Out

Per the "do not overengineer" requirement, this project does **not** include
Spring, Hibernate/JPA, microservices, Docker, a JS framework, a real email
server (see the Forgot Password note below), or automated notifications
beyond the "Closing Soon" badge already visible on the scholarship list.

**Forgot Password** uses a demo-safe flow: the student verifies their
registered email, then sets a new password directly — no email is actually
sent, because that would require configuring an SMTP server, which is out of
scope for a college project. This is documented in the UI itself
(`forgot-password.jsp`) so it's never presented as more than it is.
