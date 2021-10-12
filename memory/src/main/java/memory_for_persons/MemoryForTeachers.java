package memory_for_persons;

import by.academy.users.Teacher;

import java.util.HashMap;
import java.util.Map;

public final class MemoryForTeachers {
    private static final Map<Integer, Teacher> teachers = new HashMap<>();

    public static void put(Integer id, Teacher teacher) {
        teachers.put(id, teacher);
    }

    public static void delete(Integer id, Teacher teacher) {
        teachers.remove(id, teacher);
    }

    public static Map<Integer, Teacher> getTeachers() {
        return teachers;
    }
}
