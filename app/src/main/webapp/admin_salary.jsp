<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница зарплат для Администратора</title>
</head>
<body>
<h1>Страница зарплат</h1>
<h3>Все зарплаты</h3>
<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>ФИО учителя</th>
        <th>Дата получения</th>
        <th>Зарплата</th>
        <th>Удалить</th>
        <th>Изменить</th>
    </tr>

    <c:forEach var="salary" items="${applicationScope.salary_repository.allSalaries}">
        <c:if test="${sessionScope.teacherID == salary.teacher.id}">
            <tr style="text-align: center">
                <td><c:out value="${salary.id}"/></td>
                <td><c:out value="
                    ${salary.teacher.firstName}
                    ${salary.teacher.lastName}
                    ${salary.teacher.patronymic}"/>
                </td>
                <td><c:out value="${salary.dateOfSalary}"/></td>
                <td><c:out value="${salary.salary}"/></td>
                <td>
                    <form action="<c:url value="/SalaryServlet"/>" method="post">
                        <input type="hidden" name="method" value="delete">
                        <input type="hidden" name="ID" value="${salary.id}">
                        <button style="align-content: center" type="submit">Удалить</button>
                    </form>
                </td>
                <td>
                    <form action="<c:url value="/SalaryServlet"/>" method="post">
                        <input type="hidden" name="method" value="put">
                        <input type="hidden" name="ID" value="${salary.id}">
                        <label>
                            <input style="text-align: center; display: block" type="text"
                                   name="newSalary" placeholder="Новая зарплата">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="date"
                                   name="newDateOfSalary" placeholder="Новая дата выдачи">
                        </label>
                        <button style="align-content: center" type="submit">Изменить</button>
                    </form>
                </td>
            </tr>
        </c:if>
    </c:forEach>
</table>

<h4>Добавить зарплату</h4>
<form action="<c:url value="/SalaryServlet"/>" method="post">
    <input type="hidden" name="method" value="post">
    <input type="hidden" name="teacherID" value="${sessionScope.teacherID}">
    <label>
        Дата выдачи: <input style="display: block; text-align: center" type="date" name="newDateOfSalary">
    </label>
    <label>
        Зарплата в размере: <input style="display: block; text-align: center" type="text" name="newCount">
    </label>
    <button style="display: block; align-content: center" type="submit">Добавить зарплату</button>
</form>

<a style="display: block" href="admin_teacher.jsp">Назад</a>
</body>
</html>
