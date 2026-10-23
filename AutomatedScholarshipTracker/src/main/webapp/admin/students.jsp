<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageName" value="students" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Students — ScholarTrack Admin</title>
    <%@ include file="/WEB-INF/includes/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/includes/admin-shell-start.jspf" %>

<div class="page-header">
    <div>
        <h1>Registered Students</h1>
        <div class="page-subtitle">${students.size()} student(s) registered</div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/alerts.jspf" %>

<c:choose>
    <c:when test="${empty students}">
        <div class="empty-state">
            <div class="empty-icon">🧑‍🎓</div>
            <p>No students have registered yet.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Course</th>
                    <th>Category</th>
                    <th>CGPA</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="s" items="${students}">
                    <c:set var="p" value="${profiles[s.userId]}"/>
                    <tr>
                        <td><strong>${s.fullName}</strong></td>
                        <td>${s.email}</td>
                        <td>${s.phoneNumber}</td>
                        <td>${p.course}</td>
                        <td>${p.category}</td>
                        <td>${p.cgpa}</td>
                        <td>${p.state}</td>
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
