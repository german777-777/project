<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 08.10.2021
  Time: 8:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Главная</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/enter" method="post">
        Введите логин и пароль:
        <input name="login" type="text">
        <input name="password" type="text">
        <input type="submit" value="Войти">
    </form>
</body>
</html>
