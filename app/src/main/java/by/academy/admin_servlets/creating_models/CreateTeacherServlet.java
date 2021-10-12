package by.academy.admin_servlets.creating_models;

import by.academy.logic_for_admin.LogicAdmin;
import by.academy.privacy_data.PrivacyData;
import by.academy.users.Teacher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CreateTeacherServlet", value = "/create_teacher_servlet")
public class CreateTeacherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String fioOfTeacher = req.getParameter("fio");
        int teacherAge = Integer.parseInt(req.getParameter("age"));
        String loginOfTeacher = req.getParameter("login");
        String passwordOfTeacher = req.getParameter("password");

        Teacher createdTeacher = new Teacher(fioOfTeacher, teacherAge, new PrivacyData(loginOfTeacher, passwordOfTeacher));

        LogicAdmin.createTeacher(createdTeacher);

        session.setAttribute("createdTeacher", createdTeacher);

        req.getRequestDispatcher("created_teacher").forward(req, resp);
    }
}
