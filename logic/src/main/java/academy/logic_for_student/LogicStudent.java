package academy.logic_for_student;

import by.academy.secondary_models.Theme;
import memory_for_persons.MemoryForStudents;

import java.util.Map;

public final class LogicStudent {

    public static Map<Theme, Integer> checkMarks(String loginOfStudent, String passwordOfStudent) {
        return MemoryForStudents.getStudents().values().stream()
                .filter(student -> student.getLoginAndPassword().getLogin().equals(loginOfStudent))
                .filter(student -> student.getLoginAndPassword().getPassword().equals(passwordOfStudent))
                .findAny().get().getMarks();
    }


}
