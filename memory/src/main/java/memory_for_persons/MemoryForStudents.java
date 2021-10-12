package memory_for_persons;

import by.academy.users.Student;

import java.util.HashMap;
import java.util.Map;

public final class MemoryForStudents {
    private static final Map<Integer, Student> students = new HashMap<>();

    public static void put(Integer id, Student student) {
        students.put(id, student);
    }

    public static void delete(Integer id, Student student) {
        students.remove(id, student);
    }

    public static Map<Integer, Student> getStudents() {
        return students;
    }

}
