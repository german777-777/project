package academy.admin_servlets.creating_models;

import by.academy.logic_for_admin.LogicAdmin;
import by.academy.privacy_data.PrivacyData;
import by.academy.users.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CreateStudentServlet", value = "/create_student_servlet")
public class CreateStudentServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String fioOfStudent = req.getParameter("fio");
        int studentAge = Integer.parseInt(req.getParameter("age"));
        String loginOfStudent = req.getParameter("login");
        String passwordOfStudent = req.getParameter("password");

        Student createdStudent = new Student(fioOfStudent, studentAge, new PrivacyData(loginOfStudent, passwordOfStudent));

        LogicAdmin.createStudent(createdStudent);

        session.setAttribute("createdStudent", createdStudent);

        req.getRequestDispatcher("created_student").forward(req, resp);
    }
}
