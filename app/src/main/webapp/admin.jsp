<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница Администратора</title>
</head>
<body>
    <h1>Авторизация прошла успешно!</h1>
    <h2>
        <a style="display: block" href="admin_student.jsp">На страницу со студентами</a>
        <a style="display: block" href="admin_teacher.jsp">На страницу с учителями</a>
        <a style="display: block" href="admin_groups.jsp">На страницу с группами</a>
        <a style="display: block" href="admin_subjects.jsp">На страницу с предметами</a>
    </h2>
    <h3>
        <form action="<c:url value="/LogoutServlet"/>" method="post">
            <button type="submit" style="display: block">Выход из аккаунта</button>
        </form>
    </h3>
</body>
</html>
