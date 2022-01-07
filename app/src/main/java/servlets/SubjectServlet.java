package servlets;

import lombok.extern.slf4j.Slf4j;
import secondary.Subject;
import subject.SubjectRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet("/SubjectServlet")
public class SubjectServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Получение данных о новом предмете");
        String newName = req.getParameter("newName");

        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        log.debug("Создание предмета");
        subjectRepository.createSubject(new Subject()
                .withName(newName));

        req.getRequestDispatcher("/admin_subjects.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Получение ID предмета для обновления");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Получение новых данных о предмете");
        String newName = req.getParameter("newName");

        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        log.debug("Обновление названия предмета");
        subjectRepository.updateSubjectNameById(id, newName);

        req.getRequestDispatcher("/admin_subjects.jsp").forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Получение ID предмета для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));

        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");
        log.debug("Удаление предмета");
        subjectRepository.deleteSubjectById(id);

        req.getRequestDispatcher("/admin_subjects.jsp").forward(req, resp);
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
