<%@ page import="by.academy.users.Student" %>
<%@ page import="by.academy.logic_for_admin.LogicAdmin" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 12.10.2021
  Time: 10:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Список всех студентов</title>
</head>
<body>
<% for (Student student : LogicAdmin.checkAllStudents().values()) {%>
<%= student.toString()%>
<%}%>
<a style="display: block" href="${pageContext.request.contextPath}/admin.jsp">Назад</a>
</body>
</html>
