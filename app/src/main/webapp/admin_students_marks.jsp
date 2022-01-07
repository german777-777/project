<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Страница оценок студента</title>
</head>
<body>
    <h1>Оценки студента</h1>
    <table  border="1" width="100%">
        <tr>
            <th>ID</th>
            <th>Оценка</th>
            <th>Группа (ID)</th>
            <th>Дата</th>
            <th>Предмет (ID)</th>
            <th>Изменить</th>
            <th>Удалить</th>
        </tr>

        <c:set var="studentID" value="${sessionScope.studentID}"/>

        <c:forEach var="mark" items="${applicationScope.mark_repository.allMarks}">
            <c:if test="${mark.studentId == studentID}">
                <tr style="text-align: center">
                    <td><c:out value="${mark.id}"/></td>
                    <td><c:out value="${mark.mark}"/></td>
                    <td><c:out value="${mark.groupId}"/></td>
                    <td><c:out value="${mark.dateOfMark}"/></td>
                    <td><c:out value="${mark.subjectId}"/></td>
                    <td>
                        <form action="<c:url value="/MarksServlet"/>" method="post">
                            <input type="hidden" name="method" value="put">
                            <input type="hidden" name="ID" value="${mark.id}">
                            <label>
                                Новая группа (ID; если не изменяется - ввести прошлый):
                                <input style="text-align: center; display: block" type="text" name="newGroupID">
                            </label>
                            <label>
                                Новая оценка (если не изменяется - ввести прошлую):
                                <input style="text-align: center; display: block" type="text" name="newMark">
                            </label>
                            <label>
                                Новая дата: (если не изменятся - ввести прошлую):
                                <input style="text-align: center; display: block" type="date" name="newDate">
                            </label>
                            <label>
                                Новый предмет: (ID; если не изменяется - ввести прошлый)
                                <input style="text-align: center; display: block" type="text" name="newSubjectID">
                            </label>
                            <button style="align-content: center" type="submit">Изменить</button>
                        </form>
                    </td>
                    <td>
                        <form action="<c:url value="/MarksServlet"/>" method="post">
                            <input type="hidden" name="method" value="delete">
                            <input type="hidden" name="ID" value="${mark.id}">
                            <button style="align-content: center" type="submit">Удалить</button>
                        </form>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
    </table>

<h4>Добавить оценку студенту</h4>
<form action="<c:url value="/MarksServlet"/>" method="post">
    <input type="hidden" name="studentID" value="${studentID}">
    <input type="hidden" name="method" value="post">
    <label>
        Новая группа (ID):
        <input style="text-align: center; display: block" type="text" name="newGroupID">
    </label>
    <label>
        Новая оценка:
        <input style="text-align: center; display: block" type="text" name="newMark">
    </label>
    <label>
        Новая дата:
        <input style="text-align: center; display: block" type="date" name="newDate">
    </label>
    <label>
        Новый предмет: (ID)
        <input style="text-align: center; display: block" type="text" name="newSubjectID">
    </label>
    <button style="align-content: center" type="submit">Создать</button>
</form>

<a style="display: block" href="admin_student.jsp">Назад</a>
</body>
</html>
