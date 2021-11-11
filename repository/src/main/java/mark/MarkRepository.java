package mark;

import secondary.Group;
import secondary.Mark;
import secondary.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MarkRepository {
    //Create
    Mark createMark(Mark mark);

    //Read
    List<Mark> getAllMarks();

    //Update
    Optional<Mark> updateSubjectMarkById(int id, Subject newSubject);
    Optional<Mark> updateDateOfMarkById(int id, LocalDate newDateOfMark);
    Optional<Mark> updateGroupWhereMarkWasGiven(int id, Group newGroup);
    Optional<Mark> updateMarkById(int id, int newMark);

    //Delete
    Optional<Mark> deleteMarkById(int id);
}
