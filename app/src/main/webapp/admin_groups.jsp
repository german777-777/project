<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница групп для Администратора</title>
</head>
<body>
<h1>Страница групп</h1>
<h3>Все группы</h3>
<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Учитель</th>
        <th>Студенты и предметы</th>
        <th>Обновить</th>
        <th>Удалить</th>
    </tr>

    <c:forEach var="group" items="${applicationScope.group_repository.allGroups}">
        <tr style="text-align: center">
            <td><c:out value="${group.id}"/></td>
            <td><c:out value="${group.name}"/></td>
            <td><c:out value="${group.teacher.firstName}
                              ${group.teacher.lastName}
                              ${group.teacher.patronymic}"/>
            </td>
            <td>
                <%-- тут будет передаваться ID группы в requestScope (в сервлете) --%>
                <form action="<c:url value="/GroupServlet"/>" method="get">
                    <input type="hidden" name="method" value="get">
                    <input type="hidden" name="ID" value="${group.id}">
                    <button style="align-content: center; display: block" type="submit">Студенты и предметы</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/GroupServlet"/>" method="get">
                    <input type="hidden" name="method" value="put">
                    <input type="hidden" name="ID" value="${group.id}">
                    <label>
                        Новый учитель ('фамилия' 'имя' 'отчество'):
                        <input style="display: block; text-align: center" type="text" name="newLastFirstPatronymic"
                               placeholder="Новый учитель">
                    </label>
                    <label>
                        Новое название: <input style="display: block; text-align: center" type="text" name="newName"
                                               placeholder="Новое название">
                    </label>
                    <button style="align-content: center; display: block" type="submit">Изменить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/GroupServlet"/>" method="get">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="ID" value="${group.id}">
                    <button style="align-content: center; display: block" type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
<h3 style="color: crimson">
    <c:if test="${not empty requestScope.message}">
        <c:out value="${requestScope.message}"/>
    </c:if>
</h3>
<h4>
    Добавление новой группы
</h4>
<form action="<c:url value="/GroupServlet"/>">
    <input type="hidden" name="method" value="post">
    <label>
        Название: <input style="text-align: center; display: block" type="text" name="newName">
    </label>
    <label>
        Учитель ('фамилия' 'имя' 'отчество'): <input style="text-align: center; display: block" type="text"
                                                     name="lastFirstPatronymic">
    </label>
    <button style="align-content: center; display: block" type="submit">Создать</button>
</form>
<a style="display: block" href="admin.jsp">Назад</a>
</body>
</html>
