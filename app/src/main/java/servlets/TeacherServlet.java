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
        log.debug("Получения ID пользователя (учителя) для перехода на страницу зарплат");
        int teacherID = Integer.parseInt(req.getParameter("teacherID"));

        log.info("Установка в запрос ID учителя (необходимая область видимости ID учителя)");
        req.getSession().setAttribute("teacherID", teacherID);

        log.info("Переход на страницу зарплат");
        req.getRequestDispatcher("/admin_salary.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        log.info("Получение новых данных о пользователе (учителе) и его создание");
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
            String messageAboutNotCreating = "Учитель с введёнными учётными данными уже существует";
            req.setAttribute("message", messageAboutNotCreating);
            req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
            return;
        }
        req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        log.info("Получения ID пользователя (учителя) для обновления");
        int id = Integer.parseInt(req.getParameter("ID"));

        Optional<Person> updatableTeacherOptional = personRepository.getPersonById(id);
        if (updatableTeacherOptional.isEmpty()) {
            String messageAboutNotUpdating = "Учитель не обновлён";
            req.setAttribute("message", messageAboutNotUpdating);
            req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
        }

        updateTeacher(req, personRepository, id);

        req.getRequestDispatcher("/admin_teacher.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        log.info("Получения ID пользователя (учителя) для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));
        Optional<Person> removableTeacher = personRepository
                .getPersonById(id);
        if (removableTeacher.isPresent()) {
            personRepository.deletePersonById(id);
        } else {
            String messageAboutNotDeleting = "Учитель не удалён";
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
        log.info("Получение новых фамилии, имени и отчества пользователя (учителя) для обновления");
        String newFirstName = req.getParameter("newFirstName");
        String newLastName = req.getParameter("newLastName");
        String newPatronymic = req.getParameter("newPatronymic");
        personRepository.updatePersonNameById(updatableTeacherID, newFirstName, newLastName, newPatronymic);

        log.info("Получение новых логина и пароля пользователя (учителя) для обновления");
        String newLogin = req.getParameter("newLogin");
        String newPassword = req.getParameter("newPassword");
        personRepository.updateCredentialByPersonId(updatableTeacherID, new Credentials().withLogin(newLogin).withPassword(newPassword));

        log.info("Получение новой даты рождения пользователя (студента) для обновления");
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
