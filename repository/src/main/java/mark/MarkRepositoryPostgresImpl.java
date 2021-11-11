package mark;

import secondary.Group;
import secondary.Mark;
import secondary.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MarkRepositoryPostgresImpl implements MarkRepository {
    @Override
    public Mark createMark(Mark mark) {
        return null;
    }

    @Override
    public List<Mark> getAllMarks() {
        return null;
    }

    @Override
    public Optional<Mark> updateSubjectMarkById(int id, Subject newSubject) {
        return Optional.empty();
    }

    @Override
    public Optional<Mark> updateDateOfMarkById(int id, LocalDate newDateOfMark) {
        return Optional.empty();
    }

    @Override
    public Optional<Mark> updateGroupWhereMarkWasGiven(int id, Group newGroup) {
        return Optional.empty();
    }

    @Override
    public Optional<Mark> updateMarkById(int id, int newMark) {
        return Optional.empty();
    }

    @Override
    public Optional<Mark> deleteMarkById(int id) {
        return Optional.empty();
    }
}
