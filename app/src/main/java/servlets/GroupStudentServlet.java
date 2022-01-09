package servlets;

import group.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import person.PersonRepository;
import users.Person;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@WebServlet("/GroupStudentServlet")
public class GroupStudentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        String lastNameOfStudent = req.getParameter("newLastName");
        String firstNameOfStudent = req.getParameter("newFirstName");
        String patronymicOfStudent = req.getParameter("newPatronymic");

        Optional<Person> optionalPerson = personRepository.getPersonByName(firstNameOfStudent, lastNameOfStudent, patronymicOfStudent);
        optionalPerson.ifPresent(person -> {
            log.info("Добавление студента в группу");
            groupRepository.updateStudentsAdd(Integer.parseInt(req.getParameter("groupID")), person);
        });

        req.getRequestDispatcher("admin_subjects_students_in_group.jsp").forward(req, resp);

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        Optional<Person> optionalStudent = personRepository.getPersonById(Integer.parseInt(req.getParameter("studentID")));
        optionalStudent.ifPresent(person -> {
            log.info("Удаление студента из группы");
            groupRepository.updateStudentsRemove(Integer.parseInt(req.getParameter("groupID")), person);
        });

        req.getRequestDispatcher("admin_subjects_students_in_group.jsp").forward(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getParameter("method")) {
            case "post":
                doPost(req, resp);
                break;
            case "delete":
                doDelete(req, resp);
                break;
        }
    }
}
