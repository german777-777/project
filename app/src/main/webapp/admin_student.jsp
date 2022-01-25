<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Страница студентов для Администратора</title>
</head>
<body>
<h1>Страница студентов</h1>
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
            <tr style="text-align: center">
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
                        <input type="hidden" name="ID" value="${person.id}">
                        <input type="hidden" name="firstName" value="${person.firstName}">
                        <input type="hidden" name="lastName" value="${person.lastName}">
                        <input type="hidden" name="patronymic" value="${person.patronymic}">
                        <button style="align-content: center" type="submit">Удалить</button>
                    </form>
                </td>
                <td>
                    <form action="<c:url value="/StudentServlet"/>" method="post">
                        <input type="hidden" name="method" value="put">
                        <input type="hidden" name="ID" value="${person.id}">
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newLastName" placeholder="Новая фамилия">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newFirstName" placeholder="Новое имя">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newPatronymic" placeholder="Новое отчество">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newLogin" placeholder="Новый логин">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newPassword" placeholder="Новый пароль">
                        </label>
                        <label>
                            <input style="align-content: center; display: block" type="date" name="newDateOfBirth"
                                   placeholder="Новая дата рождения">
                        </label>
                        <button type="submit">Изменить</button>
                    </form>
                </td>
                <td>
                    <form action="<c:url value="/StudentServlet"/>" method="post">
                        <input type="hidden" name="method" value="get">
                        <input type="hidden" name="studentID" value="${person.id}">
                        <button style="align-content: center" type="submit">Оценки</button>
                    </form>
                </td>
            </tr>
        </c:if>
    </c:forEach>
</table>
<h3 style="color: crimson">
    <c:if test="${not empty requestScope.message}">
        <c:out value="${requestScope.message}"/>
    </c:if>
</h3>
<h4>
    Добавление нового студента
</h4>
<form action="<c:url value="/StudentServlet"/>">
    <input type="hidden" name="method" value="post">
    <label>
        Фамилия: <input style="text-align: center; display: block" type="text" name="newLastName">
    </label>
    <label>
        Имя:<input style="text-align: center; display: block" type="text" name="newFirstName">
    </label>
    <label>
        Отчество:<input style="text-align: center; display: block" type="text" name="newPatronymic">
    </label>
    <label>
        Логин:<input style="text-align: center; display: block" type="text" name="newLogin">
    </label>
    <label>
        Пароль:<input style="text-align: center; display: block" type="text" name="newPassword">
    </label>
    <label>
        Дата рождения:<input style="align-content: center; display: block" type="date" name="newDateOfBirth">
    </label>
    <button style="align-content: center; display: block" type="submit">Создать</button>
</form>

<a style="display: block" href="admin.jsp">Назад</a>
</body>
</html>
