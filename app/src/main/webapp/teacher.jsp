<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 08.10.2021
  Time: 9:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Преподаватель</title>
</head>
<body>
 <%= session.getAttribute("teacher")%>
<a style="display: block" href="for_teacher/salaries.jsp">Посмтреть список зарплат</a>
<a style="display: block" href="index.jsp">Выход из аккаунта</a>
</body>
</html>
