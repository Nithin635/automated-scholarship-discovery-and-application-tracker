<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageName" value="scholarships" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Scholarship — ScholarTrack Admin</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/admin-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>Edit Scholarship</h1>
        <div class="page-subtitle">${scholarship.name}</div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<div class="card">
    <form method="post" action="${pageContext.request.contextPath}/admin/edit-scholarship" class="js-validate">
        <input type="hidden" name="scholarshipId" value="${scholarship.scholarshipId}">
        <%@ include file="/WEB-INF/includes/scholarship-form-fields.jspf" %>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Update Scholarship</button>
            <a href="${pageContext.request.contextPath}/admin/scholarships" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>

<%@ include file="/WEB-INF/includes/admin-shell-end.jspf" %>
</body>
</html>
