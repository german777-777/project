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
    private int subjectId;

    // дата выставления оценки
    private LocalDate dateOfMark;

    // из какой группы, так сказать, идёт оценка
    private int groupId;

    // сама оценка
    private int mark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Mark mark1 = (Mark) o;
        return studentId == mark1.studentId && mark == mark1.mark && subjectId == mark1.subjectId && groupId == mark1.groupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentId, subjectId, groupId, mark);
    }

    public Mark withId(int id) {
        setId(id);
        return this;
    }

    public Mark withStudentId(int studentId) {
        setStudentId(studentId);
        return this;
    }

    public Mark withSubjectId(int subjectId) {
        setSubjectId(subjectId);
        return this;
    }

    public Mark withDateOfMark(LocalDate dateOfMark) {
        setDateOfMark(dateOfMark);
        return this;
    }

    public Mark withGroupId(int groupId) {
        setGroupId(groupId);
        return this;
    }

    public Mark withMark(int mark) {
        setMark(mark);
        return this;
    }
}
