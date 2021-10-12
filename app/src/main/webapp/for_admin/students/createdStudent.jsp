<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 12.10.2021
  Time: 10:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Созданный студент</title>
</head>
<body>
<%= "Создан студент: " + session.getAttribute("createdStudent")%>
<a style="display: block" href="${pageContext.request.contextPath}/for_admin/students/checkStudents.jsp">Просмотр всех студентов</a>
<a style="display: block" href="${pageContext.request.contextPath}/admin.jsp">На главную</a>
</body>
</html>
