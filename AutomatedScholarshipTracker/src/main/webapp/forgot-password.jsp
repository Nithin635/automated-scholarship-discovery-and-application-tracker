<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Forgot Password — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/public-navbar.jspf" %>

<div class="auth-wrap">
    <div class="auth-card narrow">
        <div class="auth-logo">Reset Your Password</div>
        <p class="auth-subtitle">
            Demo project note: this form resets your password directly after verifying your email,
            since no email server is configured. A production system would instead send a signed,
            time-limited reset link to your inbox.
        </p>

        <%@ include file="/WEB-INF/includes/alerts.jspf" %>

        <form method="post" action="${pageContext.request.contextPath}/forgot-password" class="js-validate">
            <div class="form-group">
                <label for="email">Registered Email</label>
                <input type="email" id="email" name="email" data-required="true" required>
            </div>
            <div class="form-group">
                <label for="newPassword">New Password</label>
                <input type="password" id="newPassword" name="newPassword" data-required="true" required minlength="6">
                <div class="hint">At least 6 characters.</div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-block">Reset Password</button>
            </div>
        </form>

        <div class="auth-footer-links">
            <a href="${pageContext.request.contextPath}/login.jsp">Back to Login</a>
        </div>
    </div>
</div>
</body>
</html>
