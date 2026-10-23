<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageName" value="applications" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>My Applications — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/student-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>My Applications</h1>
        <div class="page-subtitle">Track every scholarship you've applied to.</div>
    </div>
    <a href="${pageContext.request.contextPath}/student/scholarships" class="btn btn-primary">Find More Scholarships</a>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<c:choose>
    <c:when test="${empty applications}">
        <div class="empty-state">
            <div class="empty-icon">📋</div>
            <h3>No applications yet</h3>
            <p>Once you apply to a scholarship and mark it as applied, it will show up here.</p>
            <a href="${pageContext.request.contextPath}/student/scholarships" class="btn btn-primary">Browse Scholarships</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Scholarship</th>
                    <th>Provider</th>
                    <th>Applied Date</th>
                    <th>Status</th>
                    <th>Last Updated</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="a" items="${applications}">
                    <tr>
                        <td><strong>${a.scholarshipName}</strong></td>
                        <td>${a.scholarshipProvider}</td>
                        <td><fmt:formatDate value="${a.appliedDate}" pattern="dd MMM yyyy"/></td>
                        <td><span class="badge badge-status-${a.status}">${a.status}</span></td>
                        <td><fmt:formatDate value="${a.lastUpdated}" pattern="dd MMM yyyy, hh:mm a"/></td>
                        <td style="white-space:nowrap;">
                            <form method="post" action="${pageContext.request.contextPath}/student/update-status" style="display:inline;">
                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                <select name="status" onchange="this.form.submit()" class="btn-sm" style="padding:6px 8px;">
                                    <option value="APPLIED" ${a.status == 'APPLIED' ? 'selected' : ''}>Applied</option>
                                    <option value="UNDER_REVIEW" ${a.status == 'UNDER_REVIEW' ? 'selected' : ''}>Under Review</option>
                                    <option value="APPROVED" ${a.status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                                    <option value="REJECTED" ${a.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                                    <option value="PENDING" ${a.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                </select>
                            </form>
                            <a href="${pageContext.request.contextPath}/student/track?applicationId=${a.applicationId}" class="btn btn-outline btn-sm">Track</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/includes/student-shell-end.jspf" %>
</body>
</html>
