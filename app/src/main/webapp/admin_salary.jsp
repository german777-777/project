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
        <c:if test="${requestScope.teacherID == salary.teacher.id}">
            <tr style="text-align: center">
            <td><c:out value="${salary.id}"/></td>
            <td><c:out
                    value="${salary.teacher.firstName} ${salary.teacher.lastName} ${salary.teacher.patronymic}"/></td>
            <td><c:out value="${salary.dateOfSalary}"/></td>
            <td><c:out value="${salary.salary}"/></td>
            <td>
                <form action="<c:url value="/SalaryServlet"/>" method="post">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="ID" value="${salary.id}">
                    <input type="hidden" name="teacher" value="${salary.teacher}">
                    <button style="align-content: center" type="submit">Удалить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/SalaryServlet"/>">
                    <input type="hidden" name="method" value="put">
                    <input type="hidden" name="ID" value="${salary.id}">
                    <label>
                        <input style="text-align: center; display: block" type="text" placeholder="Новая зарплата">
                    </label>
                    <label>
                        <input style="text-align: center; display: block" type="date" placeholder="Новая дата выдачи">
                    </label>
                    <button style="align-content: center" type="submit">Изменить</button>
                </form>
            </td>
            </tr>
        </c:if>
    </c:forEach>
</table>
</body>
</html>
