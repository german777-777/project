package by.itacademy.gpisarev.servlets.system;

import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.users.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("login")
public class LoginServlet {

    private final PersonRepository personRepository;

    @Autowired
    public LoginServlet(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PostMapping
    public ModelAndView doPost(@RequestParam("login") String login, @RequestParam("password") String password, HttpSession session) {
        log.info("Проверка пользователя по логину ({}) и паролю ({})", login, password);
        Person person = checkRightLoginAndPassword(login, password);
        ModelAndView modelAndView = new ModelAndView();
        if (person != null) {
            log.info("Пользователь {} {} {} найден", person.getLastName(), person.getFirstName(), person.getCredentials());
            setAttributeToSession(session, person);
            modelAndView.getModel().put("messageToPerson", "Здравствуйте, " + person.getLastName() + " " + person.getFirstName() + " " + person.getPatronymic());
            switch (person.getRole()) {
                case STUDENT:
                    modelAndView.setViewName("admin_student");
                    break;
                case TEACHER:
                    modelAndView.setViewName("admin_teacher");
                    break;
                case ADMIN:
                    modelAndView.setViewName("admin");
                    break;
            }
        } else {
            modelAndView.getModel().put("errorMessage", "Неправильный логин или пароль. Попробуйте снова");
            modelAndView.setViewName("index");
        }
        return modelAndView;
    }

    private Person checkRightLoginAndPassword(String login, String password) {
        Person person = personRepository.getPersonByCredentials(login, password);
        if (person != null) {
            return person;
        }
        log.error("Введены неправильные логин или пароль...");
        return null;
    }

    private void setAttributeToSession(HttpSession session, Person person) {
        session.setAttribute("user", person);
        log.info("Пользователь {} {} {} положен в сессию", person.getFirstName(), person.getLastName(), person.getCredentials());
    }

}
