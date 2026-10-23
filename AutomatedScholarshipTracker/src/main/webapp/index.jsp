<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>ScholarTrack — Automated Scholarship Discovery and Application Tracker</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/public-navbar.jspf" %>

<section class="hero">
    <h1>Stop searching scholarship websites one by one.</h1>
    <p>Create your profile once. ScholarTrack automatically checks your eligibility against every scholarship
        in the system, and tracks every application you make from one dashboard.</p>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-accent">Get Started — It's Free</a>
        <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline" style="border-color:#fff;color:#fff;">Login</a>
    </div>
</section>

<div class="container">
    <div class="feature-grid">
        <div class="feature-item">
            <div class="icon">🎓</div>
            <h3>Automatic Eligibility</h3>
            <p>Your CGPA, income, category, course and state are matched against every scholarship automatically.</p>
        </div>
        <div class="feature-item">
            <div class="icon">🔍</div>
            <h3>Search &amp; Filter</h3>
            <p>Search by name or provider, or filter by category, course, state, income and more.</p>
        </div>
        <div class="feature-item">
            <div class="icon">📋</div>
            <h3>Application Tracker</h3>
            <p>Apply on the official website, then mark it here. Track status from Applied to Approved.</p>
        </div>
        <div class="feature-item">
            <div class="icon">⏰</div>
            <h3>Deadline Awareness</h3>
            <p>See upcoming deadlines at a glance and never miss a closing date again.</p>
        </div>
    </div>
</div>

<footer class="site-footer">
    Automated Scholarship Discovery and Application Tracker &mdash; a college project built with Java Servlets, JSP &amp; MySQL.
</footer>
</body>
</html>
