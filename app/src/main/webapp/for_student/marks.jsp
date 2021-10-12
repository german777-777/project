<%@ page import="by.academy.logic_for_student.LogicStudent" %>
<%@ page import="by.academy.users.Student" %>
<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 08.10.2021
  Time: 9:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Оценки</title>
</head>
<body>
<% Student student = (Student) session.getAttribute("student"); %>
<% if (LogicStudent.checkMarks(student.getLoginAndPassword().getLogin(), student.getLoginAndPassword().getPassword()) == null){%>
<%= "Оценок ещё нет" %>
<%}else{%>
<%= LogicStudent.checkMarks(student.getLoginAndPassword().getLogin(), student.getLoginAndPassword().getPassword()).toString() %>
<%}%>
<a style="display: block" href="../student.jsp">Назад</a>
</body>
</html>