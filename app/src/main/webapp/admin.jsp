<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 10.10.2021
  Time: 18:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Админ</title>
</head>
<body>
<%= session.getAttribute("admin")%>
<a style="display: block" href="for_admin/teachers/createTeacher.jsp">Создать преподавателя</a>
<a style="display: block" href="for_admin/teachers/deleteTeacher.jsp">Удалить преподавателя</a>
<a style="display: block" href="for_admin/students/createStudent.jsp">Создать студента</a>
<a style="display: block" href="for_admin/students/deleteStudent.jsp">Удалить студента</a>
<a style="display: block" href="for_admin/teachers/checkTeachers.jsp">Просмотр всех преподавателей</a>
<a style="display: block" href="for_admin/students/checkStudents.jsp">Просмотр всех студентов</a>
<a style="display: block" href="index.jsp">Выход</a>
</body>
</html>
