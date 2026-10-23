<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageName" value="applications" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Applications — ScholarTrack Admin</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/admin-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>All Applications</h1>
        <div class="page-subtitle">${applications.size()} application(s) across all students</div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<c:choose>
    <c:when test="${empty applications}">
        <div class="empty-state">
            <div class="empty-icon">📋</div>
            <p>No applications have been submitted yet.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Student</th>
                    <th>Scholarship</th>
                    <th>Applied Date</th>
                    <th>Status</th>
                    <th>Last Updated</th>
                    <th>Update Status</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="a" items="${applications}">
                    <tr>
                        <td>${a.studentName}</td>
                        <td><strong>${a.scholarshipName}</strong><br><span style="color:var(--color-text-muted); font-size:12.5px;">${a.scholarshipProvider}</span></td>
                        <td><fmt:formatDate value="${a.appliedDate}" pattern="dd MMM yyyy"/></td>
                        <td><span class="badge badge-status-${a.status}">${a.status}</span></td>
                        <td><fmt:formatDate value="${a.lastUpdated}" pattern="dd MMM yyyy, hh:mm a"/></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/admin/applications">
                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                <select name="status" onchange="this.form.submit()" style="padding:6px 8px;">
                                    <option value="APPLIED" ${a.status == 'APPLIED' ? 'selected' : ''}>Applied</option>
                                    <option value="UNDER_REVIEW" ${a.status == 'UNDER_REVIEW' ? 'selected' : ''}>Under Review</option>
                                    <option value="APPROVED" ${a.status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                                    <option value="REJECTED" ${a.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                                    <option value="PENDING" ${a.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                </select>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/includes/admin-shell-end.jspf" %>
</body>
</html>
