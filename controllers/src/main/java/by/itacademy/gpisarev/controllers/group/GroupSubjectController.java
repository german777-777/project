package by.itacademy.gpisarev.controllers.group;

import by.itacademy.gpisarev.group.GroupRepository;
import by.itacademy.gpisarev.secondary.Group;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.subject.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping("groups/{groupID}/subjects")
public class GroupSubjectController {

    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public GroupSubjectController(GroupRepository groupRepository, SubjectRepository subjectRepository) {
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
    }

    private Group getGroupByID(int groupID) {
        Group group = groupRepository.getGroupById(groupID);
        if (group != null) {
            Set<Subject> subjects = subjectRepository.getSubjectsByGroupID(groupID);
            group.setSubjects(subjects);
            return group;
        } else {
            log.error("Группа не найдена.");
            return null;
        }
    }

    private ModelAndView getGroup(int groupID, String message) {
        ModelAndView modelAndView = new ModelAndView();
        Group group = getGroupByID(groupID);

        if (group != null) {
            modelAndView.setViewName("/admin_subjects_students_in_group");
            modelAndView.getModel().put("group", group);
            modelAndView.getModel().put("messageFromGroupSubject", message);
        } else {
            log.error("Возврат");
            modelAndView.setViewName("/admin_groups");
        }
        return modelAndView;
    }


    private Subject getSubjectByID(int subjectID) {
        Subject subject = subjectRepository.getSubjectById(subjectID);
        if (subject != null) {
            return subject;
        } else {
            log.error("Предмет не найден");
            return null;
        }
    }

    private Subject getSubjectByName(String name) {
        Subject subject = subjectRepository.getSubjectByName(name);
        if (subject != null) {
            return subject;
        } else {
            log.error("Предмет не найден");
            return null;
        }
    }

    @GetMapping
    public ModelAndView get(@PathVariable("groupID") int groupID) {
        return getGroup(groupID, "Все предметы в группе");
    }

    @PostMapping("/post")
    public ModelAndView post(@PathVariable("groupID") int groupID,
                             @RequestParam("newName") String name) {
        Group group = getGroupByID(groupID);
        Subject subject = getSubjectByName(name);

        if (group != null) {
            if (subject != null) {
                if (groupRepository.updateSubjectsAdd(group, subject)) {
                    log.info("Предмет добавлен в группу");
                } else {
                    log.error("Предмет не добавлен в группу");
                }
            } else {
                log.error("Предмет не найден");
                return getGroup(groupID, "Предмет не найден. Добавления не произошло");
            }
        } else {
            log.error("Группа не найдена");
            return getGroup(groupID, "Группа не найдена. Добавления не произошло");
        }
        return getGroup(groupID, "Предмет добавлен");
    }


    @PostMapping("/{subjectID}/delete")
    public ModelAndView delete(@PathVariable("groupID") int groupID, @PathVariable("subjectID") int subjectID) {
        Group group = getGroupByID(groupID);
        Subject subject = getSubjectByID(subjectID);

        if (group != null) {
            if (subject != null) {
                if (groupRepository.updateSubjectsRemove(group, subject)) {
                    log.info("Предмет удален из группы");
                } else {
                    log.error("Предмет не удален из группы");
                }
            } else {
                log.error("Предмет не найден");
                return getGroup(groupID, "Предмет не найден. Удаления не произошло");
            }
        } else {
            log.error("Группа не найдена");
            return getGroup(groupID, "Группа не найдена. Удаления не произошло");
        }
        return getGroup(groupID, "Предмет удалён");
    }
}
