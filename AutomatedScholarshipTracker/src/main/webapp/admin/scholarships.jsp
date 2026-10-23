<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageName" value="scholarships" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Manage Scholarships — ScholarTrack Admin</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/admin-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>Manage Scholarships</h1>
        <div class="page-subtitle">${scholarships.size()} scholarship(s) in the system</div>
    </div>
    <a href="${pageContext.request.contextPath}/admin/add-scholarship" class="btn btn-primary">+ Add Scholarship</a>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<div class="card" style="margin-bottom:18px;">
    <form method="get" action="${pageContext.request.contextPath}/admin/scholarships">
        <div class="form-grid">
            <div class="form-group">
                <label for="q">Search by name or provider</label>
                <input type="text" id="q" name="q" value="${query}">
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="${pageContext.request.contextPath}/admin/scholarships" class="btn btn-secondary">Clear</a>
        </div>
    </form>
</div>

<div class="table-wrap">
    <table class="data-table">
        <thead>
        <tr>
            <th>Name</th>
            <th>Provider</th>
            <th>Type</th>
            <th>Deadline</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${scholarships}">
            <tr>
                <td><strong>${s.name}</strong></td>
                <td>${s.provider}</td>
                <td>${s.scholarshipType}</td>
                <td><fmt:formatDate value="${s.applicationDeadline}" pattern="dd MMM yyyy"/></td>
                <td>
                    <c:choose>
                        <c:when test="${s.expired}"><span class="badge badge-closed">Expired</span></c:when>
                        <c:when test="${s.status == 'ACTIVE'}"><span class="badge badge-eligible">Active</span></c:when>
                        <c:otherwise><span class="badge badge-not-eligible">Inactive</span></c:otherwise>
                    </c:choose>
                </td>
                <td style="white-space:nowrap;">
                    <a href="${pageContext.request.contextPath}/admin/edit-scholarship?id=${s.scholarshipId}" class="btn btn-outline btn-sm">Edit</a>
                    <button type="button" class="btn btn-danger btn-sm js-delete-trigger"
                            data-id="${s.scholarshipId}" data-name="${s.name}">Delete</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${empty scholarships}">
    <div class="empty-state">
        <div class="empty-icon">🎓</div>
        <p>No scholarships found.</p>
    </div>
</c:if>

<!-- Delete confirmation modal -->
<div class="modal-overlay" id="deleteModalOverlay">
    <div class="modal-box">
        <h3>Delete Scholarship?</h3>
        <p>Are you sure you want to delete "<span id="deleteModalScholarshipName"></span>"? This cannot be undone.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/delete-scholarship" id="deleteModalForm">
            <input type="hidden" name="scholarshipId" id="deleteModalScholarshipId">
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" id="deleteModalCancel">Cancel</button>
                <button type="submit" class="btn btn-danger">Yes, Delete</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/includes/admin-shell-end.jspf" %>
</body>
</html>
