package servlets;

import group.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import mark.MarkRepository;
import person.PersonRepository;
import secondary.Group;
import secondary.Mark;
import secondary.Subject;
import subject.SubjectRepository;
import users.Person;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@WebServlet("/MarksServlet")
public class MarksServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarkRepository markRepository = (MarkRepository) getServletContext().getAttribute("mark_repository");
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        log.debug("Получение данных для создания оценки");
        int studentID = Integer.parseInt(req.getParameter("studentID"));
        String groupName = req.getParameter("newGroupName");
        int count = Integer.parseInt(req.getParameter("newMark"));
        LocalDate date = LocalDate.parse(req.getParameter("newDate"));
        String subjectName = req.getParameter("newSubjectName");

        log.info("Проверяется, есть ли студент");
        Optional<Person> optionalStudent = personRepository.getPersonById(studentID);
        Person student = null;
        if (optionalStudent.isPresent()) {
            student = optionalStudent.get();
        }

        log.info("Проверяется, есть ли введённая группа");
        Optional<Group> optionalGroup = groupRepository.getGroupByName(groupName);
        Group group = null;
        if (optionalGroup.isPresent()) {
            group = optionalGroup.get();
        }

        log.info("Проверяется, есть ли введённый предмет");
        Optional<Subject> optionalSubject = subjectRepository.getSubjectByName(subjectName);
        Subject subject = null;
        if (optionalSubject.isPresent()) {
            subject = optionalSubject.get();
        }

        markRepository.createMark(new Mark()
                .withGroup(group)
                .withMark(count)
                .withDateOfMark(date)
                .withSubject(subject)
                .withStudent(student));

        req.getRequestDispatcher("admin_students_marks.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarkRepository markRepository = (MarkRepository) getServletContext().getAttribute("mark_repository");
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        log.debug("Получение новых данных об оценке");
        int markID = Integer.parseInt(req.getParameter("ID"));
        String groupName = req.getParameter("newGroupName");
        int count = Integer.parseInt(req.getParameter("newMark"));
        LocalDate date = LocalDate.parse(req.getParameter("newDate"));
        String subjectName = req.getParameter("newSubjectName");

        markRepository.updateDateOfMarkById(markID, date);

        log.info("Проверяется, есть ли введённая группа");
        Optional<Group> optionalGroup = groupRepository.getGroupByName(groupName);
        optionalGroup.ifPresent(group -> markRepository.updateGroupWhereMarkWasGiven(markID, group));

        markRepository.updateMarkById(markID, count);

        log.info("Проверяется, есть ли введённый предмет");
        Optional<Subject> subjectOptional = subjectRepository.getSubjectByName(subjectName);
        subjectOptional.ifPresent(subject -> markRepository.updateSubjectMarkById(markID, subject));

        req.getRequestDispatcher("admin_students_marks.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarkRepository markRepository = (MarkRepository) getServletContext().getAttribute("mark_repository");
        log.info("Получение ID для удаления оценки");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Удаление оценки");
        markRepository.deleteMarkById(id);

        req.getRequestDispatcher("admin_students_marks.jsp").forward(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getParameter("method")) {
            case "delete":
                doDelete(req, resp);
                break;
            case "put":
                doPut(req, resp);
                break;
            case "post":
                doPost(req, resp);
                break;
        }
    }
}