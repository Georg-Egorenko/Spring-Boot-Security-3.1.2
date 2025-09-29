<%--
  Created by IntelliJ IDEA.
  User: test1
  Date: 31.08.2025
  Time: 15:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% request.setCharacterEncoding("UTF-8"); %>
<html>
<head>
    <title>Users Management</title>
</head>
<body>
<h2>Users List</h2>

<form action="users/add" method="post">
    <input type="text" name="firstName" placeholder="First Name" required>
    <input type="text" name="lastName" placeholder="Last Name" required>
    <input type="email" name="email" placeholder="Email" required>
    <button type="submit">Add User</button>
</form>

<table border="1">
    <tr>
        <th>ID</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Email</th>
        <th>Actions</th>
    </tr>
    <c:forEach var="user" items="${users}">
        <tr>
            <td>${user.id}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName}</td>
            <td>${user.email}</td>
            <td>

                <form action="users/delete" method="post" style="display:inline;">
                    <input type="hidden" name="id" value="${user.id}">
                    <button type="submit">Delete</button>
                </form>

                <form action="users/update" method="post" style="display:inline;">
                    <input type="hidden" name="id" value="${user.id}">
                    <input type="text" name="firstName" value="${user.firstName}" required>
                    <input type="text" name="lastName" value="${user.lastName}" required>
                    <input type="email" name="email" value="${user.email}" required>
                    <button type="submit">Update</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>