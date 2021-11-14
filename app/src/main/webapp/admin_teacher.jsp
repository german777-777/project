<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница учителей для Администратора</title>
</head>
<body>
<table>
<c:forEach var="person" items="${applicationScope.person_repository.allPersons}">
    <%--@elvariable id="Role" type="role.Role"--%>
    <c:if test="${person.role.roleString.equals('Учитель')}">
        <tr>
            <td><c:out value="${person.id}"/></td>
            <td><c:out value="${person.credentials.login}"/></td>
            <td><c:out value="${person.credentials.password}"/></td>
            <td><c:out value="${person.lastName}"/></td>
            <td><c:out value="${person.firstName}"/></td>
            <td><c:out value="${person.patronymic}"/></td>
            <td><c:out value="${person.dateOfBirth}"/></td>
            <td>
                <form action="<c:url value="/TeacherServlet"/>" method="post">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="ID" value="${person.id}">
                    <input type="hidden" name="firstName" value="${person.firstName}">
                    <input type="hidden" name="lastName" value="${person.lastName}">
                    <input type="hidden" name="patronymic" value="${person.patronymic}">
                    <button style="align-content: center" type="submit">Удалить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/TeacherServlet"/>" method="post">
                    <input type="hidden" name="method" value="put">
                    <input type="hidden" name="ID" value="${person.id}">
                    <label>
                        <input style="display: block" type="text" name="newLastName" placeholder="Новая фамилия">
                    </label>
                    <label>
                        <input style="display: block" type="text" name="newFirstName" placeholder="Новое имя">
                    </label>
                    <label>
                        <input style="display: block" type="text" name="newPatronymic" placeholder="Новое отчество">
                    </label>
                    <label>
                        <input style="display: block" type="text" name="newLogin" placeholder="Новый логин">
                    </label>
                    <label>
                        <input style="display: block" type="text" name="newPassword" placeholder="Новый пароль">
                    </label>
                    <label>
                        <input style="display: block" type="date" name="newDateOfBirth"
                               placeholder="Новая дата рождения">
                    </label>
                    <button type="submit">Изменить</button>
                </form>
            </td>
            <td>
                <a href="">Оценки</a>
            </td>
        </tr>
    </c:if>
</c:forEach>

<c:if test="${not empty requestScope.message}">
    <c:out value="${requestScope.message}"/>
</c:if>
</table>
<a style="display: block" href="admin.jsp">Назад</a>
</body>
</html>
