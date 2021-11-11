package secondary;

import entity.AbstractEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Mark extends AbstractEntity {
    //какому студенту выставлена оценка
    private int studentId;

    // по какому предмету оценка
    private Subject subject;

    // дата выставления оценки
    private LocalDate dateOfMark;

    // из какой группы, так сказать, идёт оценка
    private Group group;

    // сама оценка
    private int mark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Mark mark1 = (Mark) o;
        return studentId == mark1.studentId && mark == mark1.mark && subject.equals(mark1.subject) && group.equals(mark1.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentId, subject, group, mark);
    }

    public Mark withId(int id) {
        setId(id);
        return this;
    }

    public Mark withSubject(Subject subject) {
        setSubject(subject);
        return this;
    }

    public Mark withDateOfMark(LocalDate dateOfMark) {
        setDateOfMark(dateOfMark);
        return this;
    }

    public Mark withGroup(Group group) {
        setGroup(group);
        return this;
    }

    public Mark withMark(int mark) {
        setMark(mark);
        return this;
    }
}
