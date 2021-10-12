package by.academy.secondary_models;

import by.academy.users.Student;
import by.academy.users.Teacher;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Group {
    private String name;
    private List<Student> students;
    private Set<Theme> themes;
    private Teacher teacher;

    public Group(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public Set<Theme> getThemes() {
        return themes;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setThemes(Set<Theme> themes) {
        this.themes = themes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return name.equals(group.name) && students.equals(group.students) && themes.equals(group.themes) && teacher.equals(group.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, students, themes, teacher);
    }

    @Override
    public String toString() {
        return "Группа [" + name + "] ";
    }
}
