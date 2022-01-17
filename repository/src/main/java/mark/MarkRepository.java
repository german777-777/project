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
    Optional<Mark> getMarkByID(int id);
    List<Mark> getAllMarks();

    //Update
    boolean updateSubjectMarkById(int id, Subject newSubject);
    boolean updateDateOfMarkById(int id, LocalDate newDateOfMark);
    boolean updateGroupWhereMarkWasGiven(int id, Group newGroup);
    boolean updateMarkById(int id, int newMark);

    //Delete
    boolean deleteMarkById(int id);
}
