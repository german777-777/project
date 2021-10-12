package academy.admin_servlets;

import by.academy.users.Admin;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AdminServlet", value = "/admin_servlet")
public class AdminServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Admin admin = (Admin) session.getAttribute("admin");
        RequestDispatcher dispatcher;

        if (admin != null) {
            dispatcher = req.getRequestDispatcher("/admin");
        } else {
            dispatcher = req.getRequestDispatcher("/alternative");
        }
        dispatcher.forward(req, resp);


    }
}
