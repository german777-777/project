package servlets;

import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import mark.MarkRepository;
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
        int id = Integer.parseInt(req.getParameter("studentID"));
        req.getSession().setAttribute("studentID", id);

        log.info("Переход на страницу оценок");
        req.getRequestDispatcher("admin_students_marks.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
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
            String messageAboutNotCreating = "Не создан студент";
            req.setAttribute("message", messageAboutNotCreating);
            req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
        }
        req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        int updatableStudentID = Integer.parseInt(req.getParameter("ID"));
        Optional<Person> updatableStudentOptional = personRepository.getPersonById(updatableStudentID);
        if (updatableStudentOptional.isEmpty()) {
            String messageAboutNotUpdating = "Не обновлён студент";
            req.setAttribute("message", messageAboutNotUpdating);
            req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
        }

        updateStudent(req, personRepository, updatableStudentID);

        req.getRequestDispatcher("/admin_student.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        int removableStudentID = Integer.parseInt(req.getParameter("ID"));
        Optional<Person> removableStudent = personRepository
                .getPersonById(removableStudentID);
        if (removableStudent.isPresent()) {
            personRepository.deletePersonById(removableStudentID);
        } else {
            String messageAboutNotDeleting = "Не удалён студент";
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
        String newFirstName = req.getParameter("newFirstName");
        String newLastName = req.getParameter("newLastName");
        String newPatronymic = req.getParameter("newPatronymic");
        personRepository.updatePersonNameById(updatableStudentID, newFirstName, newLastName, newPatronymic);

        String newLogin = req.getParameter("newLogin");
        String newPassword = req.getParameter("newPassword");
        personRepository.updateCredentialByPersonId(updatableStudentID, new Credentials().withLogin(newLogin).withPassword(newPassword));

        String newDateOfBirthString = req.getParameter("newDateOfBirth");
        LocalDate newDateOfBirth = LocalDate.parse(newDateOfBirthString);
        personRepository.updateDateOfBirthById(updatableStudentID, newDateOfBirth);
    }

    private boolean checkStudentInRepository(PersonRepository personRepository, Person newStudent) {
        Optional<Person> creatablePersonOptional = personRepository.getPersonByName(newStudent.getFirstName(), newStudent.getLastName(), newStudent.getPatronymic());
        if (creatablePersonOptional.isEmpty()) {
            creatablePersonOptional = personRepository.getPersonByCredentials(newStudent.getCredentials().getLogin(), newStudent.getCredentials().getPassword());
            return creatablePersonOptional.isEmpty();
        }
        return false;
    }
}
