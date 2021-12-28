package servlets;

import group.GroupRepository;
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

@WebServlet("/GroupServlet")
public class GroupServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        Group group;

        String name = req.getParameter("newName");
        String lastFirstPatronymicOfTeacher = req.getParameter("lastFirstPatronymic");

        String lastNameOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[0];
        String firstNameOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[1];
        String patronymicOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[2];

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

        groupRepository.createGroup(group);

        req.getRequestDispatcher("admin_groups.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");

        int id = Integer.parseInt(req.getParameter("ID"));
        String newName = req.getParameter("newName");
        String newLastFirstPatronymicOfTeacher = req.getParameter("newLastFirstPatronymic");

        String lastNameOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[0];
        String firstNameOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[1];
        String patronymicOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[2];

        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        Optional<Person> newTeacherOptional = personRepository.getPersonByName(firstNameOfTeacher, lastNameOfTeacher, patronymicOfTeacher);
        newTeacherOptional.ifPresent(person -> groupRepository.updateGroupTeacherById(id, person));
        groupRepository.updateGroupNameById(id, newName);

        req.getRequestDispatcher("admin_groups.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");

        int id = Integer.parseInt(req.getParameter("ID"));

        groupRepository.deleteGroupById(id);

        req.getRequestDispatcher("admin_groups.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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
