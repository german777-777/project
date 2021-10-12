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
  <title>Студент</title>
</head>
<body>
<%= session.getAttribute("student")%>
<a style="display: block" href="for_student/marks.jsp">Посмотреть свои оценки</a>
<a style="display: block" href="index.jsp">Выход из аккаунта</a>
</body>
</html>
