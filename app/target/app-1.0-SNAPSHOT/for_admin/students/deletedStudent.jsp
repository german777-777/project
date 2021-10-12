<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 12.10.2021
  Time: 10:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Удалённый студент</title>
</head>
<body>
<%= "Удалён студент: " + session.getAttribute("deletedStudent")%>
<a style="display: block" href="${pageContext.request.contextPath}/for_admin/students/checkStudents.jsp">Просмотр всех студентов</a>
<a style="display: block" href="${pageContext.request.contextPath}/admin.jsp">На главную</a>
</body>
</html>
