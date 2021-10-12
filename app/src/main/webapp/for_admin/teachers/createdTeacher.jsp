<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 10.10.2021
  Time: 19:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Созданный преподаватель</title>
</head>
<body>
<%= "Создан препродаватель: " + session.getAttribute("createdTeacher")%>
<a style="display: block" href="${pageContext.request.contextPath}/for_admin/teachers/checkTeachers.jsp">Просмотр всех учителей</a>
<a style="display: block" href="${pageContext.request.contextPath}/admin.jsp">На главную</a>
</body>
</html>
