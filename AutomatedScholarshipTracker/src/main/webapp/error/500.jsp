<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Something Went Wrong — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/public-navbar.jspf" %>
<div class="container" style="padding-top:70px;">
    <div class="empty-state">
        <div class="empty-icon">⚠️</div>
        <h2>Something went wrong</h2>
        <p>
            ${errorMessage != null ? errorMessage : "An unexpected error occurred. Please try again in a moment."}
        </p>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">Back to Home</a>
    </div>
</div>
</body>
</html>
