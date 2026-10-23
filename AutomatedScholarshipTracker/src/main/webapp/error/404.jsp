<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Page Not Found — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/public-navbar.jspf" %>
<div class="container" style="padding-top:70px;">
    <div class="empty-state">
        <div class="empty-icon">🔎</div>
        <h2>404 — Page Not Found</h2>
        <p>${errorMessage != null ? errorMessage : "The page you're looking for doesn't exist or may have been moved."}</p>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">Back to Home</a>
    </div>
</div>
</body>
</html>
