<%@ page import="by.academy.users.Student" %>
<%@ page import="by.academy.logic_for_admin.LogicAdmin" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 12.10.2021
  Time: 10:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Удаление студента</title>
</head>
<body>
Список всех студентов:
<% for (Student student : LogicAdmin.checkAllStudents().values()) { %>
<%= " [" + student.getFio() + ", возраст: " + student.getAge() + " ,логин: "
        + student.getLoginAndPassword().getLogin() + ",пароль: " + student.getLoginAndPassword().getPassword() + "] "%>
<%}%>
<form action="${pageContext.request.contextPath}/delete_student_servlet" method="post">
    Введите ФИО удаляемого студента:
    <input style="display: block" name="fio" type="text">
    Введите возраст студента:
    <input style="display: block" name="age" type="text">
    Введите логин и пароль для студента:
    <input style="display: block" name="login" type="text">
    <input style="display: block" name="password" type="text">
    <input style="display: block" type="submit" value="Удалить">
</form>
</body>
</html>
