package servlets;

import lombok.extern.slf4j.Slf4j;
import person.PersonRepository;
import role.Role;
import users.Person;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@WebServlet(name = "LogInServlet", value = "/LogInServlet")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        log.info("Получены логин и пароль пользователя, идёт проверка...");

        RequestDispatcher dispatcher;
        Optional<Person> person = checkRightLoginAndPassword(login, password);
        if (person.isPresent()) {
            switch (Objects.requireNonNull(checkRoleOfPerson(person.get()))) {
                case ADMIN:
                    dispatcher = request.getRequestDispatcher("/admin.jsp");
                    dispatcher.forward(request, response);
                    break;
                case TEACHER:
                    dispatcher = request.getRequestDispatcher("/teacher.jsp");
                    dispatcher.forward(request, response);
                    break;
                case STUDENT:
                    dispatcher = request.getRequestDispatcher("/student.jsp");
                    dispatcher.forward(request, response);
                    break;
                default:
                    request.setAttribute("error", "Не существует такого типа пользователя...");
                    dispatcher = request.getRequestDispatcher("/login.jsp");
                    dispatcher.forward(request, response);
            }
        } else {
            request.setAttribute("error", "Неправильный логин или пароль...");
            dispatcher = request.getRequestDispatcher("/login_page.jsp");
            dispatcher.forward(request, response);
        }
    }

    private Optional<Person> checkRightLoginAndPassword(String login, String password) {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        Optional<Person> personOptional = personRepository.getPersonByCredentials(login, password);
        if (personOptional.isPresent()) {
            return personOptional;
        }
        log.error("Введены неправильные логин или пароль...");
        return Optional.empty();
    }

    private Role checkRoleOfPerson(Person person) {
        switch (person.getRole()) {
            case ADMIN:
                return Role.ADMIN;
            case TEACHER:
                return Role.TEACHER;
            case STUDENT:
                return Role.STUDENT;
            default:
                return null;
        }
    }
}
