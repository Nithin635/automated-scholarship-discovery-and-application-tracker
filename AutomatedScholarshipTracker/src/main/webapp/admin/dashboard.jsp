<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageName" value="dashboard" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Admin Dashboard — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/admin-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>Admin Dashboard</h1>
        <div class="page-subtitle">System-wide scholarship and application overview.</div>
    </div>
    <a href="${pageContext.request.contextPath}/admin/add-scholarship" class="btn btn-primary">+ Add Scholarship</a>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<div class="stat-grid">
    <div class="stat-card">
        <div class="stat-label">Total Scholarships</div>
        <div class="stat-value">${totalScholarships}</div>
    </div>
    <div class="stat-card success">
        <div class="stat-label">Active Scholarships</div>
        <div class="stat-value">${activeScholarships}</div>
    </div>
    <div class="stat-card danger">
        <div class="stat-label">Expired Scholarships</div>
        <div class="stat-value">${expiredScholarships}</div>
    </div>
    <div class="stat-card accent">
        <div class="stat-label">Total Students</div>
        <div class="stat-value">${totalStudents}</div>
    </div>
    <div class="stat-card">
        <div class="stat-label">Total Applications</div>
        <div class="stat-value">${totalApplications}</div>
    </div>
    <div class="stat-card warning">
        <div class="stat-label">Pending Applications</div>
        <div class="stat-value">${pendingApplications}</div>
    </div>
</div>

<div class="card">
    <h3 style="margin-top:0;">Quick Actions</h3>
    <div class="scholarship-actions">
        <a href="${pageContext.request.contextPath}/admin/scholarships" class="btn btn-outline">Manage Scholarships</a>
        <a href="${pageContext.request.contextPath}/admin/applications" class="btn btn-outline">View Applications</a>
        <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-outline">View Students</a>
    </div>
</div>

<%@ include file="/WEB-INF/includes/admin-shell-end.jspf" %>
</body>
</html>
