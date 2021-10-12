package by.academy.admin_servlets.deleting_models;

import by.academy.logic_for_admin.LogicAdmin;
import by.academy.users.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "DeleteStudentServlet", value = "/delete_student_servlet")
public class DeleteStudentServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Student deletedStudent = createStudentForDelete(req);

        LogicAdmin.deleteStudent(deletedStudent);

        session.setAttribute("deletedStudent", deletedStudent);

        req.getRequestDispatcher("deleted_student").forward(req, resp);
    }

    private Student createStudentForDelete(HttpServletRequest req) {
        String fioOfStudent = req.getParameter("fio");
        int studentAge = Integer.parseInt(req.getParameter("age"));
        String loginOfStudent = req.getParameter("login");
        String passwordOfStudent = req.getParameter("password");

        return LogicAdmin.checkAllStudents().values().stream()
                .filter(teacher -> teacher.getFio().equals(fioOfStudent))
                .filter(teacher -> teacher.getAge() == studentAge)
                .filter(teacher -> teacher.getLoginAndPassword().getLogin().equals(loginOfStudent))
                .filter(teacher -> teacher.getLoginAndPassword().getPassword().equals(passwordOfStudent))
                .findAny().get();
    }
}
