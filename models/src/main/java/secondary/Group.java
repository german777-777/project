package secondary;

import entity.AbstractEntity;
import lombok.*;
import users.Student;
import users.Teacher;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Group extends AbstractEntity {
    private String name;
    private Teacher teacher;
    private List<Student> students;
    private List<Subject> subjects;

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

    public Group withTeacher(Teacher teacher) {
        setTeacher(teacher);
        return this;
    }

    public Group withStudents(List<Student> students) {
        setStudents(students);
        return this;
    }

    public Group withSubject(List<Subject> subjects) {
        setSubjects(subjects);
        return this;
    }
}
