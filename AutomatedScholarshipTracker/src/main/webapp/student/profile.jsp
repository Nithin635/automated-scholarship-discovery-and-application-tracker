<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageName" value="profile" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>My Profile — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/student-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>My Profile</h1>
        <div class="page-subtitle">Keep this up to date for accurate eligibility results.</div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<div class="card" style="margin-bottom:20px;">
    <div style="display:flex; justify-content:space-between; margin-bottom:8px;">
        <strong>Profile Completion</strong>
        <span>${empty profile ? 0 : profile.completionPercentage}%</span>
    </div>
    <div class="progress-track">
        <div class="progress-fill" style="width:${empty profile ? 0 : profile.completionPercentage}%;"></div>
    </div>
</div>

<div class="card">
    <form method="post" action="${pageContext.request.contextPath}/student/profile" class="js-validate">
        <h4 style="margin-top:0;">Academic &amp; Personal Details</h4>
        <div class="form-grid">
            <div class="form-group">
                <label for="dob">Date of Birth</label>
                <input type="date" id="dob" name="dob"
                       value="${profile.dateOfBirth}">
            </div>
            <div class="form-group">
                <label for="gender">Gender</label>
                <select id="gender" name="gender">
                    <option value="MALE" ${profile.gender == 'MALE' ? 'selected' : ''}>Male</option>
                    <option value="FEMALE" ${profile.gender == 'FEMALE' ? 'selected' : ''}>Female</option>
                    <option value="OTHER" ${profile.gender == 'OTHER' ? 'selected' : ''}>Other</option>
                </select>
            </div>
            <div class="form-group">
                <label for="state">State</label>
                <input type="text" id="state" name="state" value="${profile.state}">
            </div>
            <div class="form-group">
                <label for="category">Category</label>
                <select id="category" name="category">
                    <option value="GENERAL" ${profile.category == 'GENERAL' ? 'selected' : ''}>General</option>
                    <option value="OBC" ${profile.category == 'OBC' ? 'selected' : ''}>OBC</option>
                    <option value="SC" ${profile.category == 'SC' ? 'selected' : ''}>SC</option>
                    <option value="ST" ${profile.category == 'ST' ? 'selected' : ''}>ST</option>
                    <option value="EWS" ${profile.category == 'EWS' ? 'selected' : ''}>EWS</option>
                </select>
            </div>
            <div class="form-group">
                <label for="course">Course</label>
                <input type="text" id="course" name="course" value="${profile.course}">
            </div>
            <div class="form-group">
                <label for="college">College / University</label>
                <input type="text" id="college" name="college" value="${profile.collegeName}">
            </div>
            <div class="form-group">
                <label for="yearOfStudy">Year of Study</label>
                <select id="yearOfStudy" name="yearOfStudy">
                    <c:forEach begin="1" end="6" var="y">
                        <option value="${y}" ${profile.yearOfStudy == y ? 'selected' : ''}>Year ${y}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="cgpa">CGPA (0–10)</label>
                <input type="number" id="cgpa" name="cgpa" step="0.01" min="0" max="10"
                       data-min="0" data-max="10" value="${profile.cgpa}">
            </div>
            <div class="form-group">
                <label for="percentage">Percentage / Marks (0–100)</label>
                <input type="number" id="percentage" name="percentage" step="0.01" min="0" max="100"
                       data-min="0" data-max="100" value="${profile.percentage}">
            </div>
            <div class="form-group">
                <label for="income">Annual Family Income (₹)</label>
                <input type="number" id="income" name="income" min="0" value="${profile.annualFamilyIncome}">
            </div>
        </div>
        <div class="form-grid">
            <div class="form-group checkbox-row">
                <input type="checkbox" id="disability" name="disability" ${profile.disabilityStatus ? 'checked' : ''}>
                <label for="disability" style="margin:0;">I have a declared disability status</label>
            </div>
            <div class="form-group checkbox-row">
                <input type="checkbox" id="minority" name="minority" ${profile.minorityStatus ? 'checked' : ''}>
                <label for="minority" style="margin:0;">I belong to a declared minority community</label>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Save Profile</button>
        </div>
    </form>
</div>

<%@ include file="/WEB-INF/includes/student-shell-end.jspf" %>
</body>
</html>
