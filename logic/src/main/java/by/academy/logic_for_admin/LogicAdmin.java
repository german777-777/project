package by.academy.logic_for_admin;

import by.academy.privacy_data.PrivacyData;
import by.academy.users.Admin;
import by.academy.users.Student;
import by.academy.users.Teacher;
import memory_for_persons.MemoryForAdmin;
import memory_for_persons.MemoryForStudents;
import memory_for_persons.MemoryForTeachers;

import java.util.Map;

public final class LogicAdmin {
    public static void init() {
        Admin admin = new Admin("Admin Anonymous", 28, new PrivacyData("Admin", "AdminPassword"));
        MemoryForAdmin.put(admin.getId(), admin);
    }
    // для студентов
    public static void createStudent(Student student) {
        MemoryForStudents.put(student.getId(), student);
    }
    public static void deleteStudent(Student student) {
        MemoryForStudents.delete(student.getId(), student);
    }
    public static Map<Integer, Student> checkAllStudents() {
        return MemoryForStudents.getStudents();
    }

    // для преподавателей
    public static void createTeacher(Teacher teacher) {
        MemoryForTeachers.put(teacher.getId(), teacher);
    }
    public static void deleteTeacher(Teacher teacher) {
        MemoryForTeachers.delete(teacher.getId(), teacher);
    }
    public static Map<Integer, Teacher> checkAllTeachers() {
        return MemoryForTeachers.getTeachers();
    }



}
