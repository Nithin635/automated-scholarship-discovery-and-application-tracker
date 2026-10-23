<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/public-navbar.jspf" %>

<div class="auth-wrap">
    <div class="auth-card narrow">
        <div class="auth-logo"><span class="brand-badge" style="width:30px;height:30px;border-radius:8px;background:linear-gradient(135deg,#1e5f8c,#d97b3f);display:inline-flex;align-items:center;justify-content:center;color:#fff;">SD</span> ScholarTrack</div>
        <p class="auth-subtitle">Log in to view your eligible scholarships</p>

        <%@ include file="/WEB-INF/includes/alerts.jspf" %>

        <form method="post" action="${pageContext.request.contextPath}/login" class="js-validate">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" data-required="true" placeholder="you@example.com" required>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" data-required="true" placeholder="Your password" required>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-block">Login</button>
            </div>
        </form>

        <div class="auth-footer-links">
            <a href="${pageContext.request.contextPath}/forgot-password">Forgot password?</a>
            &nbsp;·&nbsp;
            Don't have an account? <a href="${pageContext.request.contextPath}/register.jsp">Register</a>
        </div>


    </div>
</div>
</body>
</html>
