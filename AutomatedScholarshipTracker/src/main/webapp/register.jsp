<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Register — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/public-navbar.jspf" %>

<div class="auth-wrap">
    <div class="auth-card wide">
        <div class="auth-logo">Create Your Student Account</div>
        <p class="auth-subtitle">Fill in your details once — we'll match you to scholarships automatically.</p>

        <%@ include file="/WEB-INF/includes/alerts.jspf" %>

        <form method="post" action="${pageContext.request.contextPath}/register" class="js-validate">
            <h4 style="margin-top:6px;">Account Details</h4>
            <div class="form-grid">
                <div class="form-group">
                    <label for="fullName">Full Name</label>
                    <input type="text" id="fullName" name="fullName" data-required="true" required
                           value="${param.fullName}">
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" data-required="true" required
                           value="${param.email}">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" data-required="true" required
                           minlength="6">
                    <div class="hint">At least 6 characters.</div>
                </div>
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="tel" id="phone" name="phone" data-required="true" required
                           placeholder="9876543210" value="${param.phone}">
                </div>
            </div>

            <h4>Personal Details</h4>
            <div class="form-grid">
                <div class="form-group">
                    <label for="dob">Date of Birth</label>
                    <input type="date" id="dob" name="dob" value="${param.dob}">
                </div>
                <div class="form-group">
                    <label for="gender">Gender</label>
                    <select id="gender" name="gender">
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="state">State</label>
                    <select id="state" name="state">
                        <option value="Andhra Pradesh">Andhra Pradesh</option>
                        <option value="Bihar">Bihar</option>
                        <option value="Delhi">Delhi</option>
                        <option value="Gujarat">Gujarat</option>
                        <option value="Karnataka" selected>Karnataka</option>
                        <option value="Kerala">Kerala</option>
                        <option value="Maharashtra">Maharashtra</option>
                        <option value="Punjab">Punjab</option>
                        <option value="Rajasthan">Rajasthan</option>
                        <option value="Tamil Nadu">Tamil Nadu</option>
                        <option value="Telangana">Telangana</option>
                        <option value="Uttar Pradesh">Uttar Pradesh</option>
                        <option value="West Bengal">West Bengal</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="category">Category</label>
                    <select id="category" name="category">
                        <option value="GENERAL">General</option>
                        <option value="OBC">OBC</option>
                        <option value="SC">SC</option>
                        <option value="ST">ST</option>
                        <option value="EWS">EWS</option>
                    </select>
                </div>
            </div>

            <h4>Academic Details</h4>
            <div class="form-grid">
                <div class="form-group">
                    <label for="course">Course</label>
                    <input type="text" id="course" name="course" placeholder="e.g. B.Tech, B.Com, M.Sc" value="${param.course}">
                </div>
                <div class="form-group">
                    <label for="college">College / University</label>
                    <input type="text" id="college" name="college" value="${param.college}">
                </div>
                <div class="form-group">
                    <label for="yearOfStudy">Year of Study</label>
                    <select id="yearOfStudy" name="yearOfStudy">
                        <option value="1">1st Year</option>
                        <option value="2">2nd Year</option>
                        <option value="3">3rd Year</option>
                        <option value="4">4th Year</option>
                        <option value="5">5th Year</option>
                        <option value="6">6th Year / PG</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="cgpa">CGPA (0–10)</label>
                    <input type="number" id="cgpa" name="cgpa" step="0.01" min="0" max="10"
                           data-min="0" data-max="10" value="${param.cgpa}">
                </div>
                <div class="form-group">
                    <label for="percentage">Percentage / Marks (0–100)</label>
                    <input type="number" id="percentage" name="percentage" step="0.01" min="0" max="100"
                           data-min="0" data-max="100" value="${param.percentage}">
                </div>
                <div class="form-group">
                    <label for="income">Annual Family Income (₹)</label>
                    <input type="number" id="income" name="income" min="0" value="${param.income}">
                </div>
            </div>

            <div class="form-grid">
                <div class="form-group checkbox-row">
                    <input type="checkbox" id="disability" name="disability">
                    <label for="disability" style="margin:0;">I have a declared disability status</label>
                </div>
                <div class="form-group checkbox-row">
                    <input type="checkbox" id="minority" name="minority">
                    <label for="minority" style="margin:0;">I belong to a declared minority community</label>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-block">Create Account</button>
            </div>
        </form>

        <div class="auth-footer-links">
            Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
        </div>
    </div>
</div>
</body>
</html>
