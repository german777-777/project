package by.academy.teacher_servlet;

import by.academy.users.Teacher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "TeacherServlet", value = "/teacher_servlet")
public class TeacherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Teacher teacher = (Teacher) session.getAttribute("teacher");
        RequestDispatcher dispatcher;

        if (teacher != null) {
            dispatcher = req.getRequestDispatcher("/teacher");
        } else {
            dispatcher = req.getRequestDispatcher("/alternative");
        }
        dispatcher.forward(req, resp);
    }
}
