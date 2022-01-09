package secondary;

import entity.AbstractEntity;
import lombok.*;
import role.Role;
import users.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Group extends AbstractEntity {
    private String name;
    private Person teacher;
    private List<Person> students = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Group group = (Group) o;
        return name.equals(group.name) && Objects.equals(teacher, group.teacher) && Objects.equals(students, group.students) && Objects.equals(subjects, group.subjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, teacher, students, subjects);
    }

    public Group withId(int id) {
        setId(id);
        return this;
    }

    public Group withName(String name) {
        setName(name);
        return this;
    }

    public Group withTeacher(Person teacher) {
        if (teacher.getRole() == Role.TEACHER) {
            setTeacher(teacher);
        }
        return this;
    }

    public Group withStudents(List<Person> students) {
        setStudents(students);
        return this;
    }

    public Group withSubjects(List<Subject> subjects) {
        setSubjects(subjects);
        return this;
    }

    public Group addSubject(Subject subject) {
        this.subjects.add(subject);
        return this;
    }

    public Group addStudent(Person student) {
        if (student.getRole() == Role.STUDENT) {
            this.students.add(student);
        }
        return this;
    }

    public Group removeSubject(Subject subject) {
        this.subjects.remove(subject);
        return this;
    }

    public Group removeStudent(Person student) {
        if (student.getRole() == Role.STUDENT) {
            this.students.remove(student);
        }
        return this;
    }
}
