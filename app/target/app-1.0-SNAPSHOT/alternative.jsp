<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 08.10.2021
  Time: 9:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Неправильный логин или пароль</title>
</head>
<body>
<%= session.getAttribute("alternative")%>
<a style="display: block" href="index.jsp">На главную</a>
</body>
</html>
