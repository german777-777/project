package filters;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import fabric.RepositoryFactory;
import fabric.RepositoryFactoryJpaImpl;
import fabric.RepositoryFactoryLocalImpl;
import fabric.RepositoryFactoryPostgresImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import secondary.Group;
import secondary.Salary;
import secondary.Subject;
import users.Admin;
import users.Person;
import users.Student;
import users.Teacher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

@WebFilter(filterName = "InitializeRepository")
@Slf4j
public class InitializeRepositoryFilter implements Filter {
    private final ResourceBundle bundle = ResourceBundle.getBundle("repository");

    @SneakyThrows
    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("Получение типа репозитория");
        String typeOfRepository = bundle.getString("type0");

        switch (typeOfRepository) {
            case ("postgres"):
                log.info("Тип: Postgres");
                loadRepositoryPostgres(filterConfig);
                break;
            case ("hibernate"):
                log.info("Тип: Hibernate");
                loadRepositoryHibernate(filterConfig);
                break;
            case ("memory"):
            default:
                log.info("Тип: Memory");
                loadRepositoryMemory(filterConfig);
                break;
        }
    }



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void loadRepositoryHibernate(FilterConfig filterConfig) {
        Configuration configuration = new Configuration().configure();
        try {
            SessionFactory factory = configuration.buildSessionFactory();
            RepositoryFactory repositoryFactory = RepositoryFactoryJpaImpl.getInstance(factory);
            setRepositoriesToContext(filterConfig, repositoryFactory);
            setValuesToRepositories(repositoryFactory);
        } catch (Exception e) {
            log.error("Не удалось загрузить тип Hibernate: " + e.getMessage());
            loadRepositoryMemory(filterConfig);
        }

    }

    private void loadRepositoryMemory(FilterConfig config) {
        RepositoryFactory repositoryFactory = RepositoryFactoryLocalImpl.getInstance();
        setRepositoriesToContext(config, repositoryFactory);
        setValuesToRepositories(repositoryFactory);
    }

    private void loadRepositoryPostgres(FilterConfig config) throws PropertyVetoException {
        RepositoryFactory repositoryFactory;
        if (isDriverLoad()) {
            ComboPooledDataSource pool = setPoolPropertiesAndReturn();
            repositoryFactory = RepositoryFactoryPostgresImpl.getInstance(pool);
        } else {
            repositoryFactory = RepositoryFactoryLocalImpl.getInstance();
        }
        setRepositoriesToContext(config, repositoryFactory);
        setValuesToRepositories(repositoryFactory);
    }

    private void setRepositoriesToContext(FilterConfig config, RepositoryFactory factory) {
        log.info("Загрузка репозиториев в Context");
        ServletContext context = config.getServletContext();
        context.setAttribute("credential_repository", factory.getCredentialRepository());
        context.setAttribute("group_repository", factory.getGroupRepository());
        context.setAttribute("mark_repository", factory.getMarkRepository());
        context.setAttribute("person_repository", factory.getPersonRepository());
        context.setAttribute("salary_repository", factory.getSalaryRepository());
        context.setAttribute("subject_repository", factory.getSubjectRepository());
    }

    private boolean isDriverLoad() {
        try {
            Class.forName(bundle.getString("driver"));
            log.info("Драйвер загружен");
            return true;
        } catch (ClassNotFoundException e) {
            log.error("Драйвер не загружен");
        }
        return false;
    }

    private ComboPooledDataSource setPoolPropertiesAndReturn() throws PropertyVetoException {
        log.info("Установка свойств pool-a соединений");
        ComboPooledDataSource pool = new ComboPooledDataSource();
        pool.setJdbcUrl(bundle.getString("url"));
        pool.setUser(bundle.getString("login"));
        pool.setPassword(bundle.getString("password"));

        pool.setInitialPoolSize(5);
        pool.setMinPoolSize(3);
        pool.setAcquireIncrement(2);
        pool.setMaxPoolSize(10);
        pool.setMaxStatements(100);
        pool.setDriverClass(bundle.getString("driver"));
        return pool;
    }

    private void setValuesToRepositories(RepositoryFactory factory) {
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

       // log.info("Добавление учителей");
       // Person teacher1 = new Teacher()
       //         .withFirstName("Юрий")
       //         .withLastName("Гришечкин")
       //         .withPatronymic("Геннадьевич")
       //         .withCredentials(new Credentials()
       //                 .withLogin("Grisha")
       //                 .withPassword("No name"))
       //         .withDateOfBirth(LocalDate.of(1987, Month.APRIL, 20));
       // Person teacher2 = new Teacher()
       //         .withFirstName("Виктор")
       //         .withLastName("Андреев")
       //         .withPatronymic("Васильевич")
       //         .withCredentials(new Credentials()
       //                 .withLogin("Vitek")
       //                 .withPassword("Andrew"))
       //         .withDateOfBirth(LocalDate.of(1965, Month.JANUARY, 10));
       // Person teacher3 = new Teacher()
       //         .withFirstName("Андрей")
       //         .withLastName("Самофалов")
       //         .withPatronymic("Леонидович")
       //         .withCredentials(new Credentials()
       //                 .withLogin("Samofalov123")
       //                 .withPassword("Leonidich"))
       //         .withDateOfBirth(LocalDate.of(1989, Month.AUGUST, 19));
       // factory.getPersonRepository().createPerson(teacher1);
       // factory.getPersonRepository().createPerson(teacher2);
       // factory.getPersonRepository().createPerson(teacher3);
//
       // log.info("Добавление студентов");
       // Person student1 = new Student()
       //         .withFirstName("Герман")
       //         .withLastName("Писарев")
       //         .withPatronymic("Дмитриевич")
       //         .withCredentials(new Credentials()
       //                 .withLogin("Mongol")
       //                 .withPassword("Gurmanidze"))
       //         .withDateOfBirth(LocalDate.of(2001, Month.AUGUST, 7));
       // Person student2 = new Student()
       //         .withFirstName("Иван")
       //         .withLastName("Угловец")
       //         .withPatronymic("Иванович")
       //         .withCredentials(new Credentials()
       //                 .withLogin("vuglovets")
       //                 .withPassword("molven"))
       //         .withDateOfBirth(LocalDate.of(2000, Month.OCTOBER, 27));
       // factory.getPersonRepository().createPerson(student1);
       // factory.getPersonRepository().createPerson(student2);
//
       // log.info("Добавление зарплаты учителю");
       // Person teacherWhoReceivedSalary = factory.getPersonRepository().getPersonByName("Андрей", "Самофалов", "Леонидович").get();
       // Salary salary1 = new Salary()
       //         .withSalary(1000)
       //         .withDateOfSalary(LocalDate.of(2021, Month.JANUARY, 1))
       //         .withTeacher(teacherWhoReceivedSalary);
       // factory.getSalaryRepository().createSalary(salary1);
//
       // log.info("Добавление предметов");
       // Subject subject1 = new Subject()
       //         .withName("GIT");
       // factory.getSubjectRepository().createSubject(subject1);
//
       // Subject subject2 = new Subject()
       //         .withName("Maven");
       // factory.getSubjectRepository().createSubject(subject2);
//
       // Subject subject3 = new Subject()
       //         .withName("Tomcat");
       // factory.getSubjectRepository().createSubject(subject3);
//
       // Subject subject4 = new Subject()
       //         .withName("Hibernate");
       // factory.getSubjectRepository().createSubject(subject4);
//
       // log.info("Добавление группы");
       // Group group1 = new Group()
       //         .withName("JEE-2021")
       //         .withTeacher(teacher3)
       //         .addStudent(student1)
       //         .addStudent(student2)
       //         .addSubject(subject1)
       //         .addSubject(subject2)
       //         .addSubject(subject3);
       // factory.getGroupRepository().createGroup(group1);

    }
}
