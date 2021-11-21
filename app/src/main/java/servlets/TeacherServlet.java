package servlets;

import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import person.PersonRepository;
import users.Person;
import users.Teacher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@WebServlet("/TeacherServlet")
public class TeacherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        int teacherID = Integer.parseInt(req.getParameter("teacherID"));

        Optional<Person> teacherOptional = personRepository.getPersonById(teacherID);
        if (teacherOptional.isPresent()) {
            log.info("Переход на страницу зарплат");
            req.setAttribute("teacherID", teacherID);
            req.getRequestDispatcher("/admin_salary.jsp").forward(req, resp);
        } else {
            String message = "Не найден учитель";
            req.setAttribute("message", message);
            req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        Person newTeacher = new Teacher()
                .withLastName(req.getParameter("newLastName"))
                .withFirstName(req.getParameter("newFirstName"))
                .withPatronymic(req.getParameter("newPatronymic"))
                .withCredentials(new Credentials()
                        .withLogin(req.getParameter("newLogin"))
                        .withPassword(req.getParameter("newPassword")))
                .withDateOfBirth(LocalDate.parse(req.getParameter("newDateOfBirth")));

        if (!checkTeacherInRepository(personRepository, newTeacher)) {
            personRepository.createPerson(newTeacher);
        } else {
            String messageAboutNotCreating = "Такой учитель уже существует";
            req.setAttribute("message", messageAboutNotCreating);
            req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
            return;
        }
        req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        int updatableTeacherID = Integer.parseInt(req.getParameter("ID"));
        Optional<Person> updatableTeacherOptional = personRepository.getPersonById(updatableTeacherID);
        if (updatableTeacherOptional.isEmpty()) {
            String messageAboutNotUpdating = "Не обновлён учитель";
            req.setAttribute("message", messageAboutNotUpdating);
            req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
        }

        updateTeacher(req, personRepository, updatableTeacherID);

        req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        int removableTeacherID = Integer.parseInt(req.getParameter("ID"));
        Optional<Person> removableTeacher = personRepository
                .getPersonById(removableTeacherID);
        if (removableTeacher.isPresent()) {
            personRepository.deletePersonById(removableTeacherID);
        } else {
            String messageAboutNotDeleting = "Не удалён учитель";
            req.setAttribute("message", messageAboutNotDeleting);
        }
        req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        switch (method) {
            case "put":
                doPut(req, resp);
                break;
            case "delete":
                doDelete(req, resp);
                break;
            case "get":
                doGet(req, resp);
                break;
            case "post":
                doPost(req, resp);
                break;
            default:
                super.service(req, resp);
        }
    }

    private void updateTeacher(HttpServletRequest req, PersonRepository personRepository, int updatableTeacherID) {
        String newFirstName = req.getParameter("newFirstName");
        String newLastName = req.getParameter("newLastName");
        String newPatronymic = req.getParameter("newPatronymic");
        personRepository.updatePersonNameById(updatableTeacherID, newFirstName, newLastName, newPatronymic);

        String newLogin = req.getParameter("newLogin");
        String newPassword = req.getParameter("newPassword");
        personRepository.updateCredentialByPersonId(updatableTeacherID, new Credentials().withLogin(newLogin).withPassword(newPassword));

        String newDateOfBirthString = req.getParameter("newDateOfBirth");
        LocalDate newDateOfBirth = LocalDate.parse(newDateOfBirthString);
        personRepository.updateDateOfBirthById(updatableTeacherID, newDateOfBirth);
    }

    private boolean checkTeacherInRepository(PersonRepository personRepository, Person newTeacher) {
        Optional<Person> creatableTeacherOptional = personRepository.getPersonByName(newTeacher.getFirstName(), newTeacher.getLastName(), newTeacher.getPatronymic());
        if (creatableTeacherOptional.isPresent()) {
            creatableTeacherOptional = personRepository.getPersonByCredentials(newTeacher.getCredentials().getLogin(), newTeacher.getCredentials().getPassword());
            return creatableTeacherOptional.isPresent();
        }
        return false;
    }
}
