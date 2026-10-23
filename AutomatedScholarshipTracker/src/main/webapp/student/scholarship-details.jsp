<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageName" value="scholarships" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${scholarship.name} — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/student-shell-start.jspf" %>

<a href="${pageContext.request.contextPath}/student/scholarships" class="btn btn-secondary btn-sm" style="margin-bottom:16px;">&larr; Back to Scholarships</a>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<div class="page-header">
    <div>
        <h1>${scholarship.name}</h1>
        <div class="page-subtitle">${scholarship.provider} &middot; ${scholarship.scholarshipType}</div>
    </div>
    <div style="display:flex; gap:6px; flex-wrap:wrap;">
        <c:choose>
            <c:when test="${scholarship.expired}"><span class="badge badge-closed">Application Closed</span></c:when>
            <c:when test="${applied}"><span class="badge badge-applied">Applied</span></c:when>
        </c:choose>
        <c:if test="${!scholarship.expired && scholarship.closingSoon}"><span class="badge badge-closing-soon">Closing Soon</span></c:if>
        <c:choose>
            <c:when test="${eligibility.eligible}"><span class="badge badge-eligible">Eligible</span></c:when>
            <c:otherwise><span class="badge badge-not-eligible">Not Eligible</span></c:otherwise>
        </c:choose>
    </div>
</div>

<div class="card" style="margin-bottom:20px;">
    <c:choose>
        <c:when test="${eligibility.eligible}">
            <div class="alert alert-success" style="margin-bottom:0;">You are eligible for this scholarship.</div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-error" style="margin-bottom:0;">
                <strong>Not Eligible</strong><br>
                <c:forEach var="reason" items="${eligibility.reasons}">
                    &bull; ${reason}<br>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<div class="card" style="margin-bottom:20px;">
    <h3 style="margin-top:0;">Description</h3>
    <p>${scholarship.description}</p>
    <div class="scholarship-meta">
        <span><strong>Amount:</strong> <c:out value="${scholarship.amount}"/></span>
        <span><strong>Start Date:</strong> <fmt:formatDate value="${scholarship.applicationStartDate}" pattern="dd MMM yyyy"/></span>
        <span><strong>Deadline:</strong> <fmt:formatDate value="${scholarship.applicationDeadline}" pattern="dd MMM yyyy"/></span>
    </div>
</div>

<div class="card" style="margin-bottom:20px;">
    <h3 style="margin-top:0;">Eligibility Criteria</h3>
    <p>${scholarship.eligibilityDescription}</p>
    <div class="table-wrap">
        <table class="data-table">
            <tbody>
            <tr><td>Minimum CGPA</td><td>${empty scholarship.minimumCgpa ? 'Not restricted' : scholarship.minimumCgpa}</td></tr>
            <tr><td>Minimum Percentage</td><td>${empty scholarship.minimumPercentage ? 'Not restricted' : scholarship.minimumPercentage}</td></tr>
            <tr><td>Maximum Family Income</td><td>${empty scholarship.maximumFamilyIncome ? 'Not restricted' : scholarship.maximumFamilyIncome}</td></tr>
            <tr><td>Eligible Categories</td><td>${scholarship.eligibleCategories}</td></tr>
            <tr><td>Eligible Courses</td><td>${scholarship.eligibleCourses}</td></tr>
            <tr><td>Eligible States</td><td>${scholarship.eligibleStates}</td></tr>
            <tr><td>Year of Study</td>
                <td>
                    <c:choose>
                        <c:when test="${empty scholarship.minimumYear and empty scholarship.maximumYear}">Not restricted</c:when>
                        <c:otherwise>${scholarship.minimumYear} to ${scholarship.maximumYear}</c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr><td>Gender</td><td>${scholarship.genderEligibility}</td></tr>
            <tr><td>Disability Required</td><td>${scholarship.disabilityRequired ? 'Yes' : 'No'}</td></tr>
            <tr><td>Minority Required</td><td>${scholarship.minorityRequired ? 'Yes' : 'No'}</td></tr>
            </tbody>
        </table>
    </div>
</div>

<div class="card">
    <h3 style="margin-top:0;">Official Links</h3>
    <p>Applications are submitted on the scholarship provider's own official website — not on ScholarTrack.</p>
    <div class="scholarship-actions">
        <a href="${scholarship.applicationUrl}" target="_blank" rel="noopener" class="btn btn-primary">Apply Now (opens official site)</a>

        <c:if test="${!applied && !scholarship.expired}">
            <form method="post" action="${pageContext.request.contextPath}/student/apply" onsubmit="return confirmMarkAsApplied();" style="display:inline;">
                <input type="hidden" name="scholarshipId" value="${scholarship.scholarshipId}">
                <button type="submit" class="btn btn-accent">Mark as Applied</button>
            </form>
        </c:if>
        <c:if test="${applied}">
            <span class="badge badge-applied" style="padding:9px 16px;">Already Marked as Applied</span>
        </c:if>
        <c:if test="${scholarship.expired}">
            <span class="badge badge-closed" style="padding:9px 16px;">Application Closed</span>
        </c:if>

        <c:choose>
            <c:when test="${not empty scholarship.trackingUrl}">
                <a href="${scholarship.trackingUrl}" target="_blank" rel="noopener" class="btn btn-outline">Track Application</a>
            </c:when>
            <c:otherwise>
                <span class="hint">Official tracking information is not available. Please visit the scholarship provider's website.</span>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="/WEB-INF/includes/student-shell-end.jspf" %>
</body>
</html>
