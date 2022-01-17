package servlets;

import group.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import secondary.Subject;
import subject.SubjectRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@WebServlet("/GroupSubjectServlet")
public class GroupSubjectServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        Optional<Subject> optionalSubject = subjectRepository.getSubjectByName(req.getParameter("newName"));
        optionalSubject.ifPresent(subject -> {
            log.info("Добавление предмета в группу");
            groupRepository.updateSubjectsAdd(Integer.parseInt(req.getParameter("groupID")), subject);
        });

        req.getRequestDispatcher("admin_subjects_students_in_group.jsp").forward(req, resp);
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        Optional<Subject> optionalSubject = subjectRepository.getSubjectById(Integer.parseInt(req.getParameter("subjectID")));
        optionalSubject.ifPresent(subject -> {
            log.info("Удаление предмета из группы");
            groupRepository.updateSubjectsRemove(Integer.parseInt(req.getParameter("groupID")), subject);
        });

        req.getRequestDispatcher("admin_subjects_students_in_group.jsp").forward(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getParameter("method")) {
            case "post":
                doPost(req, resp);
                break;
            case "delete":
                doDelete(req, resp);
                break;
        }
    }
}
