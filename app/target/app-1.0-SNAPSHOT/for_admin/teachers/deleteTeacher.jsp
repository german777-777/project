<%@ page import="by.academy.users.Teacher" %>
<%@ page import="by.academy.logic_for_admin.LogicAdmin" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 11.10.2021
  Time: 16:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Удаление преподавателя</title>
</head>
<body>
Список всех преподавателей:
<% for (Teacher teacher : LogicAdmin.checkAllTeachers().values()) { %>
<%= " [" + teacher.getFio() + ", возраст: " + teacher.getAge() + " ,логин: "
    + teacher.getLoginAndPassword().getLogin() + ",пароль: " + teacher.getLoginAndPassword().getPassword() + "] "%>
<%}%>
<form action="${pageContext.request.contextPath}/delete_teacher_servlet" method="post">
    Введите ФИО удаляемого преподавателя:
    <input style="display: block" name="fio" type="text">
    Введите возраст преподавателя:
    <input style="display: block" name="age" type="text">
    Введите логин и пароль для преподавателя:
    <input style="display: block" name="login" type="text">
    <input style="display: block" name="password" type="text">
    <input style="display: block" type="submit" value="Удалить">
</form>
</body>
</html>
