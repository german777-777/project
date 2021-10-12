package by.academy.admin_servlets.deleting_models;

import by.academy.logic_for_admin.LogicAdmin;
import by.academy.users.Teacher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "DeleteTeacherServlet", value = "/delete_teacher_servlet")
public class DeleteTeacherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Teacher deletedTeacher = createTeacherForDelete(req);

        LogicAdmin.deleteTeacher(deletedTeacher);

        session.setAttribute("deletedTeacher", deletedTeacher);

        req.getRequestDispatcher("deleted_teacher").forward(req, resp);
    }

    private Teacher createTeacherForDelete(HttpServletRequest req) {
        String fioOfTeacher = req.getParameter("fio");
        int teacherAge = Integer.parseInt(req.getParameter("age"));
        String loginOfTeacher = req.getParameter("login");
        String passwordOfTeacher = req.getParameter("password");

        return LogicAdmin.checkAllTeachers().values().stream()
                .filter(teacher -> teacher.getFio().equals(fioOfTeacher))
                .filter(teacher -> teacher.getAge() == teacherAge)
                .filter(teacher -> teacher.getLoginAndPassword().getLogin().equals(loginOfTeacher))
                .filter(teacher -> teacher.getLoginAndPassword().getPassword().equals(passwordOfTeacher))
                .findAny().get();
    }
}
