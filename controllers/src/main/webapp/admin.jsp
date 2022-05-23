<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница Администратора</title>
</head>
<body>
    <h1>Авторизация прошла успешно!</h1>

    <h3>
        <c:out value="${messageToPerson}"/>
    </h3>

    <h2>
        <form action="${pageContext.request.contextPath}admin/students/get" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми студентами</button>
        </form>
        <form action="${pageContext.request.contextPath}admin/teachers/get" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми учителями</button>
        </form>
        <form action="${pageContext.request.contextPath}admin/groups" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми группами</button>
        </form>
        <form action="${pageContext.request.contextPath}admin/subjects/get" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми предметами</button>
        </form>
    </h2>
    <h3>
        <a href="${pageContext.request.contextPath}/logout">Выход из аккаунта</a>
    </h3>
</body>
</html>
