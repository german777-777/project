package filters;

import credentials.Credentials;
import fabric.RepositoryFactory;
import fabric.RepositoryFactoryLocalImpl;
import lombok.extern.slf4j.Slf4j;
import secondary.Salary;
import users.Admin;
import users.Person;
import users.Student;
import users.Teacher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

@WebFilter(filterName = "InitializeRepository")
@Slf4j
public class InitializeRepositoryFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("Получение типа репозитория");
        String typeOfRepository = ResourceBundle.getBundle("repository").getString("type");

        switch (typeOfRepository) {
            case ("postgres"):
                loadRepositoryPostgres(filterConfig);
                break;
            case ("memory"):
            default:
                loadRepositoryMemory(filterConfig);
                break;
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void loadRepositoryMemory(FilterConfig config) {
        RepositoryFactory factory = RepositoryFactoryLocalImpl.getInstance();
        setRepositoriesToContext(config, factory);
        setValuesToRepositories(factory);
    }

    private void loadRepositoryPostgres(FilterConfig config) {

    }

    private void setRepositoriesToContext(FilterConfig config, RepositoryFactory factory) {
        log.info("Загрузка репозиториев");
        ServletContext context = config.getServletContext();
        context.setAttribute("credential_repository", factory.getCredentialRepository());
        context.setAttribute("group_repository", factory.getGroupRepository());
        context.setAttribute("mark_repository", factory.getMarkRepository());
        context.setAttribute("person_repository", factory.getPersonRepository());
        context.setAttribute("salary_repository", factory.getSalaryRepository());
        context.setAttribute("subject_repository", factory.getSubjectRepository());
    }

    private void setValuesToRepositories(RepositoryFactory factory) {
        log.info("Добавление учителей");
        Person teacher1 = new Teacher()
                .withFirstName("Гришечкин")
                .withLastName("Юрий")
                .withPatronymic("Геннадьевич")
                .withCredentials(new Credentials()
                        .withLogin("Grisha")
                        .withPassword("No name"))
                .withDateOfBirth(LocalDate.of(1987, Month.APRIL, 20));
        Person teacher2 = new Teacher()
                .withFirstName("Виктор")
                .withLastName("Андреев")
                .withPatronymic("Васильевич")
                .withCredentials(new Credentials()
                        .withLogin("Vitek")
                        .withPassword("Andrew"))
                .withDateOfBirth(LocalDate.of(1965, Month.JANUARY, 10));
        Person teacher3 = new Teacher()
                .withFirstName("Андрей")
                .withLastName("Самофалов")
                .withPatronymic("Леонидович")
                .withCredentials(new Credentials()
                        .withLogin("Samofalov123")
                        .withPassword("Leonidich"))
                .withDateOfBirth(LocalDate.of(1989, Month.AUGUST, 19));
        factory.getPersonRepository().createPerson(teacher1);
        factory.getPersonRepository().createPerson(teacher2);
        factory.getPersonRepository().createPerson(teacher3);

        log.info("Добавление студентов");
        Person student1 = new Student()
                .withFirstName("Писарев")
                .withLastName("Герман")
                .withPatronymic("Дмитриевич")
                .withCredentials(new Credentials()
                        .withLogin("Mongol")
                        .withPassword("Gurmanidze"))
                .withDateOfBirth(LocalDate.of(2001, Month.AUGUST, 7));
        Person student2 = new Student()
                .withFirstName("Иван")
                .withLastName("Угловец")
                .withPatronymic("Иванович")
                .withCredentials(new Credentials()
                        .withLogin("vuglovets")
                        .withPassword("molven"))
                .withDateOfBirth(LocalDate.of(2000, Month.OCTOBER, 27));
        factory.getPersonRepository().createPerson(student1);
        factory.getPersonRepository().createPerson(student2);

        log.info("Добавление админа");
        Person admin = new Admin()
                .withFirstName("Someone")
                .withLastName("Anon")
                .withPatronymic("No")
                .withCredentials(new Credentials()
                        .withLogin("Anonymous")
                        .withPassword("Anonim"))
                .withDateOfBirth(LocalDate.of(1, Month.MARCH, 1));
        factory.getPersonRepository().createPerson(admin);

        Salary salary1 = new Salary()
                .withSalary(1000)
                .withDateOfSalary(LocalDate.of(2021, Month.JANUARY, 1))
                .withTeacher(teacher3);
        factory.getSalaryRepository().createSalary(salary1);
    }
}
