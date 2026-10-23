<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageName" value="scholarships" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Scholarships — ScholarTrack</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/student-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>Scholarships</h1>
        <div class="page-subtitle">Showing ${scholarships.size()} scholarship(s)</div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<div class="card" style="margin-bottom:20px;">
    <form method="get" action="${pageContext.request.contextPath}/student/scholarships">
        <div class="form-grid">
            <div class="form-group">
                <label for="q">Search by name, provider or keyword</label>
                <input type="text" id="q" name="q" placeholder="e.g. Reliance, Engineering, SC" value="${query}">
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Search</button>
        </div>
    </form>
    <hr style="border:none;border-top:1px solid var(--color-border); margin:18px 0;">
    <form method="get" action="${pageContext.request.contextPath}/student/scholarships">
        <div class="form-grid">
            <div class="form-group">
                <label for="category">Category</label>
                <select id="category" name="category">
                    <option value="">Any</option>
                    <option value="GENERAL" ${param.category == 'GENERAL' ? 'selected' : ''}>General</option>
                    <option value="OBC" ${param.category == 'OBC' ? 'selected' : ''}>OBC</option>
                    <option value="SC" ${param.category == 'SC' ? 'selected' : ''}>SC</option>
                    <option value="ST" ${param.category == 'ST' ? 'selected' : ''}>ST</option>
                    <option value="EWS" ${param.category == 'EWS' ? 'selected' : ''}>EWS</option>
                </select>
            </div>
            <div class="form-group">
                <label for="course">Course</label>
                <input type="text" id="course" name="course" placeholder="e.g. Engineering" value="${param.course}">
            </div>
            <div class="form-group">
                <label for="state">State</label>
                <input type="text" id="state" name="state" placeholder="e.g. Karnataka" value="${param.state}">
            </div>
            <div class="form-group">
                <label for="maxIncome">Maximum Family Income (₹)</label>
                <input type="number" id="maxIncome" name="maxIncome" value="${param.maxIncome}">
            </div>
            <div class="form-group">
                <label for="minCgpa">Minimum CGPA required ≤</label>
                <input type="number" step="0.01" id="minCgpa" name="minCgpa" value="${param.minCgpa}">
            </div>
            <div class="form-group">
                <label for="type">Scholarship Type</label>
                <select id="type" name="type">
                    <option value="">Any</option>
                    <option value="GOVERNMENT" ${param.type == 'GOVERNMENT' ? 'selected' : ''}>Government</option>
                    <option value="PRIVATE" ${param.type == 'PRIVATE' ? 'selected' : ''}>Private</option>
                    <option value="INSTITUTIONAL" ${param.type == 'INSTITUTIONAL' ? 'selected' : ''}>Institutional</option>
                    <option value="NGO" ${param.type == 'NGO' ? 'selected' : ''}>NGO</option>
                </select>
            </div>
            <div class="form-group">
                <label for="eligibility">Eligibility</label>
                <select id="eligibility" name="eligibility">
                    <option value="">All Scholarships</option>
                    <option value="ELIGIBLE" ${param.eligibility == 'ELIGIBLE' ? 'selected' : ''}>Eligible Only</option>
                </select>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Apply Filters</button>
            <a id="clearFiltersBtn" href="#" class="btn btn-secondary" data-base-url="${pageContext.request.contextPath}/student/scholarships">Clear Filters</a>
        </div>
    </form>
</div>

<c:choose>
    <c:when test="${empty scholarships}">
        <div class="empty-state">
            <div class="empty-icon">🎓</div>
            <h3>No scholarships found</h3>
            <p>Try adjusting your search or filters.</p>
        </div>
    </c:when>
    <c:otherwise>
        <c:forEach var="s" items="${scholarships}">
            <c:set var="elig" value="${eligibilityMap[s.scholarshipId]}"/>
            <c:set var="hasApplied" value="${appliedMap[s.scholarshipId]}"/>
            <div class="scholarship-card">
                <div class="scholarship-card-top">
                    <div>
                        <h3>${s.name}</h3>
                        <div class="scholarship-provider">${s.provider} &middot; ${s.scholarshipType}</div>
                    </div>
                    <div style="display:flex; gap:6px; flex-wrap:wrap;">
                        <c:choose>
                            <c:when test="${s.expired}"><span class="badge badge-closed">Application Closed</span></c:when>
                            <c:when test="${hasApplied}"><span class="badge badge-applied">Applied</span></c:when>
                        </c:choose>
                        <c:if test="${!s.expired && s.closingSoon}"><span class="badge badge-closing-soon">Closing Soon</span></c:if>
                        <c:choose>
                            <c:when test="${elig.eligible}"><span class="badge badge-eligible">Eligible</span></c:when>
                            <c:otherwise><span class="badge badge-not-eligible">Not Eligible</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <p>${s.description}</p>
                <div class="scholarship-meta">
                    <span><strong>Amount:</strong> ${s.amount}</span>
                    <span><strong>Deadline:</strong> <fmt:formatDate value="${s.applicationDeadline}" pattern="dd MMM yyyy"/></span>
                </div>
                <c:if test="${!elig.eligible}">
                    <div class="alert alert-error" style="margin-bottom:0;">
                        <strong>Not Eligible:</strong> ${elig.reasonSummary}
                    </div>
                </c:if>
                <div class="scholarship-actions">
                    <a href="${pageContext.request.contextPath}/student/scholarship-details?id=${s.scholarshipId}" class="btn btn-outline btn-sm">View Details</a>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/includes/student-shell-end.jspf" %>
</body>
</html>
