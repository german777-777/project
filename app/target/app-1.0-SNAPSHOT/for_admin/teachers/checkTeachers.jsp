<%@ page import="by.academy.users.Teacher" %>
<%@ page import="by.academy.logic_for_admin.LogicAdmin" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 10.10.2021
  Time: 18:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Список всех учителей</title>
</head>
<body>
<% for (Teacher teacher : LogicAdmin.checkAllTeachers().values()) {%>
<%= teacher.toString()%>
<%}%>
<a style="display: block" href="${pageContext.request.contextPath}/admin.jsp">Назад</a>
</body>
</html>
