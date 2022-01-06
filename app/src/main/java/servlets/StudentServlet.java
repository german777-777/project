package servlets;

import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import person.PersonRepository;
import users.Person;
import users.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet("/StudentServlet")
@Slf4j
public class StudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Получения ID пользователя (студента) для перехода к его оценкам");
        int id = Integer.parseInt(req.getParameter("studentID"));

        log.info("Установка в сессию ID студента (необходимая область видимости ID студента)");
        req.getSession().setAttribute("studentID", id);

        log.info("Переход на страницу оценок");
        req.getRequestDispatcher("admin_students_marks.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        log.info("Получение данных о пользователе (студенте) и его создание");
        Person newStudent = new Student()
                .withLastName(req.getParameter("newLastName"))
                .withFirstName(req.getParameter("newFirstName"))
                .withPatronymic(req.getParameter("newPatronymic"))
                .withCredentials(new Credentials()
                        .withLogin(req.getParameter("newLogin"))
                        .withPassword(req.getParameter("newPassword")))
                .withDateOfBirth(LocalDate.parse(req.getParameter("newDateOfBirth")));

        if (checkStudentInRepository(personRepository, newStudent)) {
            personRepository.createPerson(newStudent);
        } else {
            String messageAboutNotCreating = "Студент с введёнными учётными данными уже существует";
            req.setAttribute("message", messageAboutNotCreating);
            req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
        }
        req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        log.info("Получения ID пользователя (студента) для обновления");
        int id = Integer.parseInt(req.getParameter("ID"));

        Optional<Person> updatableStudentOptional = personRepository.getPersonById(id);
        if (updatableStudentOptional.isEmpty()) {
            String messageAboutNotUpdating = "Студент не обновлён";
            req.setAttribute("message", messageAboutNotUpdating);
            req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
        } else {
            updateStudent(req, personRepository, id);
        }

        req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        log.info("Получения ID пользователя (студента) для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));

        Optional<Person> removableStudent = personRepository
                .getPersonById(id);
        if (removableStudent.isPresent()) {
            personRepository.deletePersonById(id);
        } else {
            String messageAboutNotDeleting = "Студент не удалён";
            req.setAttribute("message", messageAboutNotDeleting);
        }
        req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
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

    private void updateStudent(HttpServletRequest req, PersonRepository personRepository, int updatableStudentID) {
        log.info("Получение новых фамилии, имени и отчества пользователя (студента) для обновления");
        String newFirstName = req.getParameter("newFirstName");
        String newLastName = req.getParameter("newLastName");
        String newPatronymic = req.getParameter("newPatronymic");
        personRepository.updatePersonNameById(updatableStudentID, newFirstName, newLastName, newPatronymic);

        log.info("Получение новых логина и пароля пользователя (студента) для обновления");
        String newLogin = req.getParameter("newLogin");
        String newPassword = req.getParameter("newPassword");
        personRepository.updateCredentialByPersonId(updatableStudentID, new Credentials()
                                                                        .withLogin(newLogin)
                                                                        .withPassword(newPassword));

        log.info("Получение новой даты рождения пользователя (студента) для обновления");
        String newDateOfBirthString = req.getParameter("newDateOfBirth");
        LocalDate newDateOfBirth = LocalDate.parse(newDateOfBirthString);
        personRepository.updateDateOfBirthById(updatableStudentID, newDateOfBirth);
    }

    private boolean checkStudentInRepository(PersonRepository personRepository, Person newStudent) {
        log.debug("Проверяется, что пользователя с введённым логином и паролем нет в системе");
        Optional<Person> creatablePersonOptional =  personRepository.getPersonByCredentials(newStudent.getCredentials().getLogin(), newStudent.getCredentials().getPassword());
        return creatablePersonOptional.isEmpty();
    }
}
