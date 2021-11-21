package mark;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import group.GroupRepositoryPostgresImpl;
import secondary.Group;
import secondary.Mark;
import secondary.Subject;

import java.time.LocalDate;
import java.util.List;

public class MarkRepositoryPostgresImpl implements MarkRepository {
    private static volatile MarkRepositoryPostgresImpl instance;
    private final ComboPooledDataSource pool;

    private MarkRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    public static MarkRepositoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (GroupRepositoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new MarkRepositoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

    @Override
    public Mark createMark(Mark mark) {
        return null;
    }

    @Override
    public List<Mark> getAllMarks() {
        return null;
    }

    @Override
    public boolean updateSubjectMarkById(int id, Subject newSubject) {
        return false;
    }

    @Override
    public boolean updateDateOfMarkById(int id, LocalDate newDateOfMark) {
        return false;
    }

    @Override
    public boolean updateGroupWhereMarkWasGiven(int id, Group newGroup) {
        return false;
    }

    @Override
    public boolean updateMarkById(int id, int newMark) {
        return false;
    }

    @Override
    public boolean deleteMarkById(int id) {
        return false;
    }

}
