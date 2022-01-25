package servlets;

import lombok.extern.slf4j.Slf4j;
import person.PersonRepository;
import role.Role;
import salary.SalaryRepository;
import secondary.Salary;
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
@WebServlet("/SalaryServlet")
public class SalaryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Получение данных о новой зарплате");
        LocalDate newDateOfSalary = LocalDate.parse(req.getParameter("newDateOfSalary"));
        int newSalary = Integer.parseInt(req.getParameter("newCount"));

        log.debug("Получение ID учителя, которому назначается зарплата");
        int teacherID = Integer.parseInt(req.getParameter("teacherID"));

        SalaryRepository salaryRepository = (SalaryRepository) getServletContext().getAttribute("salary_repository");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        log.debug("Поиск учителя по ID для присвоения зарплаты");
        Optional<Person> optionalTeacher = personRepository.getPersonById(teacherID);
        optionalTeacher.ifPresent(teacher -> {
            log.info("Создание зарплаты");
            salaryRepository.createSalary(new Salary()
                    .withSalary(newSalary)
                    .withDateOfSalary(newDateOfSalary)
                    .withTeacher((Teacher) teacher));
        });

        req.getRequestDispatcher("/admin_salary.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Получение ID зарплаты для изменения");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Получение новых данных о зарплате");
        int newSalary = Integer.parseInt(req.getParameter("newSalary"));
        LocalDate newDateOfSalary = LocalDate.parse(req.getParameter("newDateOfSalary"));

        SalaryRepository salaryRepository = (SalaryRepository) getServletContext().getAttribute("salary_repository");

        log.debug("Обновление даты получения зарплаты");
        salaryRepository.updateDateOfSalaryById(id, newDateOfSalary);

        log.debug("Обновление размера зарплаты");
        salaryRepository.updateSalaryById(id, newSalary);

        req.getRequestDispatcher("/admin_salary.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Получение ID зарплаты для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));

        SalaryRepository salaryRepository = (SalaryRepository) getServletContext().getAttribute("salary_repository");
        log.debug("Удаление зарплаты");
        salaryRepository.deleteSalaryById(id);

        req.getRequestDispatcher("/admin_salary.jsp").forward(req, resp);
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
        }
    }
}
