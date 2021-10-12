<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 10.10.2021
  Time: 19:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Создание преподавателя</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/create_teacher_servlet" method="post">
    Введите ФИО преподавателя:
    <input style="display: block" name="fio" type="text">
    Введите возраст преподавателя:
    <input style="display: block" name="age" type="text">
    Введите логин и пароль для преподавателя, по которым он сможет входить в систему:
    <input style="display: block" name="login" type="text">
    <input style="display: block" name="password" type="text">
    <input style="display: block" type="submit" value="Создать">
</form>

</body>
</html>
