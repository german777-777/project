package by.academy.logic_for_teacher;

import memory_for_persons.MemoryForTeachers;

import java.math.BigDecimal;
import java.util.List;

public final class LogicTeacher {

    public static List<BigDecimal> getSalaries(String loginOfTeacher, String passwordOfTeacher) {
        return MemoryForTeachers.getTeachers().values().stream()
                .filter(teacher -> teacher.getLoginAndPassword().getLogin().equals(loginOfTeacher))
                .filter(teacher -> teacher.getLoginAndPassword().getPassword().equals(passwordOfTeacher))
                .findAny().get().getSalaries();
    }
}
