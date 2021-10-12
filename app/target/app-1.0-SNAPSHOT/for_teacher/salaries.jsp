<%@ page import="by.academy.users.Teacher" %>
<%@ page import="by.academy.logic_for_teacher.LogicTeacher" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 12.10.2021
  Time: 12:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Список заплат</title>
</head>
<body>
<% Teacher teacher = (Teacher) session.getAttribute("teacher");%>
<% if (LogicTeacher.getSalaries(teacher.getLoginAndPassword().getLogin(), teacher.getLoginAndPassword().getPassword()) == null){%>
<%= "Зарплат пока не было"%>
<%} else {%>
<%= LogicTeacher.getSalaries(teacher.getLoginAndPassword().getLogin(), teacher.getLoginAndPassword().getPassword()).toString()%>
<%}%>
<a style="display: block" href="../teacher.jsp">Назад</a>
</body>
</html>
