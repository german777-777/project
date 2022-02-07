<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
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
        <form action="${pageContext.request.contextPath}/students/get" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми студентами</button>
        </form>
        <form action="${pageContext.request.contextPath}/teachers/get" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми учителями</button>
        </form>
        <form action="${pageContext.request.contextPath}/groups/get" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми группами</button>
        </form>
        <form action="${pageContext.request.contextPath}/subjects/get" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми предметами</button>
        </form>
    </h2>
    <h3>
        <a href="${pageContext.request.contextPath}/logout">Выход из аккаунта</a>
    </h3>
</body>
</html>
