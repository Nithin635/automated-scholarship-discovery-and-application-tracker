<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageName" value="dashboard" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/student-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>Welcome, ${sessionScope.user.fullName}</h1>
        <div class="page-subtitle">Here's your scholarship snapshot for today.</div>
    </div>
    <a href="${pageContext.request.contextPath}/student/scholarships" class="btn btn-primary">Browse Scholarships</a>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<c:if test="${empty profile or profile.completionPercentage < 100}">
    <div class="card" style="margin-bottom:20px; border-left:4px solid #d97b3f;">
        <strong>Complete your profile</strong> to get accurate eligibility results.
        <a href="${pageContext.request.contextPath}/student/profile">Update profile &rarr;</a>
    </div>
</c:if>

<div class="stat-grid">
    <div class="stat-card success">
        <div class="stat-label">Eligible Scholarships</div>
        <div class="stat-value">${eligibleCount}</div>
    </div>
    <div class="stat-card">
        <div class="stat-label">Total Scholarships</div>
        <div class="stat-value">${totalScholarships}</div>
    </div>
    <div class="stat-card accent">
        <div class="stat-label">My Applications</div>
        <div class="stat-value">${totalApplications}</div>
    </div>
    <div class="stat-card warning">
        <div class="stat-label">Pending</div>
        <div class="stat-value">${pendingCount}</div>
    </div>
    <div class="stat-card success">
        <div class="stat-label">Approved</div>
        <div class="stat-value">${approvedCount}</div>
    </div>
</div>

<div class="card">
    <h3>Upcoming Deadlines</h3>
    <c:choose>
        <c:when test="${empty upcomingDeadlines}">
            <div class="empty-state">
                <div class="empty-icon">🗓️</div>
                <p>No upcoming deadlines right now. Check back soon.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Scholarship</th>
                        <th>Provider</th>
                        <th>Deadline</th>
                        <th>Days Left</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="s" items="${upcomingDeadlines}">
                        <tr>
                            <td><strong>${s.name}</strong></td>
                            <td>${s.provider}</td>
                            <td><fmt:formatDate value="${s.applicationDeadline}" pattern="dd MMM yyyy"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${s.closingSoon}"><span class="badge badge-closing-soon">${s.daysRemaining} days left</span></c:when>
                                    <c:otherwise>${s.daysRemaining} days left</c:otherwise>
                                </c:choose>
                            </td>
                            <td><a href="${pageContext.request.contextPath}/student/scholarship-details?id=${s.scholarshipId}" class="btn btn-outline btn-sm">Details</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/includes/student-shell-end.jspf" %>
</body>
</html>
