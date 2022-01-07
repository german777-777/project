package servlets;

import group.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import person.PersonRepository;
import secondary.Group;
import users.Person;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@WebServlet("/GroupServlet")
public class GroupServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        Group group;

        log.info("Получение данных о новой группе");
        String name = req.getParameter("newName");
        String lastFirstPatronymicOfTeacher = req.getParameter("lastFirstPatronymic");

        String lastNameOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[0];
        String firstNameOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[1];
        String patronymicOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[2];

        log.debug("Проверка, есть ли такой учитель");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        Optional<Person> optionalPerson = personRepository.getPersonByName(firstNameOfTeacher, lastNameOfTeacher, patronymicOfTeacher);

        if (optionalPerson.isPresent()) {
            Person teacher = optionalPerson.get();
            group = new Group()
                    .withName(name)
                    .withTeacher(teacher);
        } else {
            group = new Group()
                    .withName(name);
        }

        log.info("Создание группы");
        groupRepository.createGroup(group);

        req.getRequestDispatcher("admin_groups.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");

        log.debug("Получение новых данных о группе");
        int id = Integer.parseInt(req.getParameter("ID"));
        String newName = req.getParameter("newName");
        String newLastFirstPatronymicOfTeacher = req.getParameter("newLastFirstPatronymic");

        String lastNameOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[0];
        String firstNameOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[1];
        String patronymicOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[2];

        log.debug("Проверка, есть ли учитель, который будет вести эту группу");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        Optional<Person> newTeacherOptional = personRepository.getPersonByName(firstNameOfTeacher, lastNameOfTeacher, patronymicOfTeacher);
        newTeacherOptional.ifPresent(person -> {
            log.info("Обновление учителя группы");
            groupRepository.updateGroupTeacherById(id, person);
        });

        log.info("Обновление названия группы");
        groupRepository.updateGroupNameById(id, newName);

        req.getRequestDispatcher("admin_groups.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        log.info("Получение ID группы для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Удаление группы");
        groupRepository.deleteGroupById(id);

        req.getRequestDispatcher("admin_groups.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Получение ID группы для просмотра студентов и предметов этой группы");
        int id = Integer.parseInt(req.getParameter("ID"));

        req.getSession().setAttribute("groupID", id);
        req.getRequestDispatcher("admin_subjects_students_in_group.jsp").forward(req, resp);
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
            case "get":
                doGet(req, resp);
                break;
        }
    }
}
