package by.academy.student_servlet;

import by.academy.users.Student;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "StudentServlet", value = "/student_servlet")
public class StudentServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Student student = (Student) session.getAttribute("student");
        RequestDispatcher dispatcher;

        if (student != null) {
            dispatcher = req.getRequestDispatcher("/student");
        } else {
            dispatcher = req.getRequestDispatcher("/alternative");
        }
        dispatcher.forward(req, resp);
    }
}
