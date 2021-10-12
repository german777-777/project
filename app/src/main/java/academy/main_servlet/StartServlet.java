package academy.main_servlet;

import by.academy.logic_for_admin.LogicAdmin;
import by.academy.users.Admin;
import by.academy.users.Student;
import by.academy.users.Teacher;
import memory_for_persons.MemoryForAdmin;
import memory_for_persons.MemoryForStudents;
import memory_for_persons.MemoryForTeachers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "StartServlet", value = "/enter")
public class StartServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        LogicAdmin.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        RequestDispatcher dispatcher;

        if (isItStudent(req)) {
            Student student = searchStudent(req);
            session.setAttribute("student", student);
            dispatcher = req.getRequestDispatcher("/student_servlet");
            dispatcher.forward(req, resp);
        } else if (isItTeacher(req)) {
            Teacher teacher = searchTeacher(req);
            session.setAttribute("teacher", teacher);
            dispatcher = req.getRequestDispatcher("/teacher_servlet");
            dispatcher.forward(req, resp);
        } else if (isItAdmin(req)) {
            Admin admin = searchAdmin(req);
            session.setAttribute("admin", admin);
            dispatcher = req.getRequestDispatcher("/admin_servlet");
            dispatcher.forward(req, resp);
        } else {
                session.setAttribute("alternative", "Неправильный логин или пароль");
                dispatcher = req.getRequestDispatcher("/alternative");
                dispatcher.forward(req, resp);
        }
    }


    private boolean isItStudent(HttpServletRequest req) {
        boolean isStudent = false;

        for (Student student : MemoryForStudents.getStudents().values()) {
            String loginOfStudent = student.getLoginAndPassword().getLogin();
            String passwordOfStudent = student.getLoginAndPassword().getPassword();

            if (loginOfStudent.equals(req.getParameter("login"))
                    && passwordOfStudent.equals(req.getParameter("password"))) {
                isStudent = true;
                break;
            }
        }

        return isStudent;
    }

    private Student searchStudent(HttpServletRequest req) {
        Student searchingStudent = null;
        for (Student student : MemoryForStudents.getStudents().values()) {
            String loginOfStudent = student.getLoginAndPassword().getLogin();
            String passwordOfStudent = student.getLoginAndPassword().getPassword();

            if (loginOfStudent.equals(req.getParameter("login"))
                    && passwordOfStudent.equals(req.getParameter("password"))) {
                searchingStudent = student;
                break;
            }
        }

        return searchingStudent;
    }

    private boolean isItTeacher(HttpServletRequest req) {
        boolean isTeacher = false;

        for (Teacher teacher : MemoryForTeachers.getTeachers().values()) {
            String loginOfTeacher = teacher.getLoginAndPassword().getLogin();
            String passwordOfTeacher = teacher.getLoginAndPassword().getPassword();

            if (loginOfTeacher.equals(req.getParameter("login"))
                    && passwordOfTeacher.equals(req.getParameter("password"))) {
                isTeacher = true;
                break;
            }
        }

        return isTeacher;
    }

    private Teacher searchTeacher(HttpServletRequest req) {
        Teacher searchingTeacher = null;
        for (Teacher teacher : MemoryForTeachers.getTeachers().values()) {
            String loginOfTeacher = teacher.getLoginAndPassword().getLogin();
            String passwordOfTeacher = teacher.getLoginAndPassword().getPassword();

            if (loginOfTeacher.equals(req.getParameter("login"))
                    && passwordOfTeacher.equals(req.getParameter("password"))) {
                searchingTeacher = teacher;
                break;
            }
        }

        return searchingTeacher;
    }

    private boolean isItAdmin(HttpServletRequest req) {
        boolean isAdmin = false;

        for (Admin admin : MemoryForAdmin.getAdmin().values()) {
            String loginOfAdmin = admin.getLoginAndPassword().getLogin();
            String passwordOfAdmin = admin.getLoginAndPassword().getPassword();

            if (loginOfAdmin.equals(req.getParameter("login"))
                    && passwordOfAdmin.equals(req.getParameter("password"))) {
                isAdmin = true;
                break;
            }
        }
        return isAdmin;
    }

    private Admin searchAdmin(HttpServletRequest req) {
        Admin searchingAdmin = null;
        for (Admin admin : MemoryForAdmin.getAdmin().values()) {
            String loginOfAdmin = admin.getLoginAndPassword().getLogin();
            String passwordOfAdmin = admin.getLoginAndPassword().getPassword();

            if (loginOfAdmin.equals(req.getParameter("login"))
                    && passwordOfAdmin.equals(req.getParameter("password"))) {
                searchingAdmin = admin;
                break;
            }
        }

        return searchingAdmin;
    }

}
