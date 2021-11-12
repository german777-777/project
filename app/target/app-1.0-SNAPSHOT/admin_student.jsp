<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Страница студетов для Администратора</title>
</head>
<body>
<h1>Стрнаица студентов</h1>
<h3>Все студенты</h3>
<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Логин</th>
        <th>Пароль</th>
        <th>Фамилия</th>
        <th>Имя</th>
        <th>Отчество</th>
        <th>Дата рождения</th>
        <th>Удалить</th>
        <th>Изменить</th>
        <th>Оценки</th>
    </tr>
    <c:forEach var="person" items="${applicationScope.person_repository.allPersons}">
        <%--@elvariable id="Role" type="role.Role"--%>
        <c:if test="${person.role.roleString.equals('Студент')}">
            <tr>
                <td><c:out value="${person.id}"/></td>
                <td><c:out value="${person.credentials.login}"/></td>
                <td><c:out value="${person.credentials.password}"/></td>
                <td><c:out value="${person.lastName}"/></td>
                <td><c:out value="${person.firstName}"/></td>
                <td><c:out value="${person.patronymic}"/></td>
                <td><c:out value="${person.dateOfBirth}"/></td>
                <td>
                    <form action="<c:url value="/StudentServlet"/>" method="post">
                        <input type="hidden" name="method" value="delete">
                        <input type="hidden" name="id" value="${person.id}">
                        <input type="hidden" name="firstName" value="${person.firstName}">
                        <input type="hidden" name="lastName" value="${person.lastName}">
                        <input type="hidden" name="patronymic" value="${person.patronymic}">
                        <button type="submit">Удалить</button>
                    </form>
                </td>
                <td>
                    <form action="<c:url value="/StudentServlet"/>" method="post">
                        <input type="hidden" name="ID" value="${person.id}">
                        <label>
                            <input type="text" name="firstName" required placeholder="Имя" value="${person.firstName}">
                        </label>
                        <label>
                            <input type="text" name="lastName" required placeholder="Фамилия" value="${person.lastName}">
                        </label>
                        <label>
                            <input type="text" name="patronymic" required placeholder="Отчество" value="${person.patronymic}">
                        </label>
                        <label>
                            <input type="text" name="login" required placeholder="Логин" value="${person.credentials.login}">
                        </label>
                        <label>
                            <input type="text" name="password" required placeholder="Пароль" value="${person.credentials.password}">
                        </label>
                        <label>
                            <input type="text" name="dateOfBirth" required placeholder="Дата рождения" value="${person.dateOfBirth}">
                        </label>
                        <button type="submit">Изменить</button>
                    </form>
                </td>
            </tr>
        </c:if>
    </c:forEach>
</table>
</body>
</html>
