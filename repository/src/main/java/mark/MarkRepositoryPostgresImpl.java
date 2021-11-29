package mark;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import group.GroupRepositoryPostgresImpl;
import lombok.extern.slf4j.Slf4j;
import secondary.Group;
import secondary.Mark;
import secondary.Subject;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static constants.Queries.*;
@Slf4j
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
        log.debug("Попытка добавить оценку");
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForInsert = connection.prepareStatement(putMark))
        {
            statementForInsert.setInt(1, mark.getStudentId());
            statementForInsert.setInt(2, mark.getGroupId());
            statementForInsert.setInt(3, mark.getSubjectId());
            statementForInsert.setDate(4, Date.valueOf(mark.getDateOfMark()));
            statementForInsert.setInt(5, mark.getMark());
            if (statementForInsert.executeUpdate() > 0) {
                log.info("Оценка успешно добавлена");
                return mark;
            } else {
                log.error("Оценка не добавлена");
                return null;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return null;
        }
    }

    @Override
    public Optional<Mark> getMarkByID(int id) {
        log.debug("Попытка найти оценку по id");
        ResultSet set = null;
        try (Connection connection = pool.getConnection();
            PreparedStatement statementForFind = connection.prepareStatement(findMarkByID))
        {
            statementForFind.setInt(1, id);
            set = statementForFind.executeQuery();
            if (set.next()) {
                log.info("Оценка найдена");
                return Optional.of(new Mark()
                        .withId(set.getInt(1))
                        .withStudentId(set.getInt(2))
                        .withGroupId(set.getInt(3))
                        .withSubjectId(set.getInt(4))
                        .withDateOfMark(set.getDate(5).toLocalDate())
                        .withMark(set.getInt(6)));
            } else {
                log.error("Оценка не найдена");
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return Optional.empty();
        } finally {
            closeResource(set);
        }
    }

    @Override
    public List<Mark> getAllMarks() {
        log.debug("Попытка получения всех оценок");
        List<Mark> marks = new ArrayList<>();
        try (Connection connection = pool.getConnection();
            PreparedStatement statementForFind = connection.prepareStatement(findAllMarks);
            ResultSet set = statementForFind.executeQuery())
        {
            while (set.next()) {
                marks.add(new Mark()
                        .withId(set.getInt(1))
                        .withStudentId(set.getInt(2))
                        .withGroupId(set.getInt(3))
                        .withSubjectId(set.getInt(4))
                        .withDateOfMark(set.getDate(5).toLocalDate())
                        .withMark(set.getInt(6)));
                log.info("Оценка добавлена");
            }
            return marks;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return marks;
        }
    }

    @Override
    public boolean updateSubjectMarkById(int id, Subject newSubject) {
        log.debug("Попытка найти оценку по ID");
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, обновления не произошло");
            return false;
        } else {
            try (Connection connection = pool.getConnection();
                PreparedStatement statementForUpdate = connection.prepareStatement(updateSubjectOfMarkByID))
            {
                statementForUpdate.setInt(1, newSubject.getId());
                statementForUpdate.setInt(2, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Оценка обновлена");
                    return true;
                } else {
                    log.error("Ошибка обновления");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
            }
        }
    }

    @Override
    public boolean updateDateOfMarkById(int id, LocalDate newDateOfMark) {
        log.debug("Попытка найти оценку по ID");
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, обновления не произошло");
            return false;
        } else {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(updateDateOfMarkByID))
            {
                statementForUpdate.setDate(1, Date.valueOf(newDateOfMark));
                statementForUpdate.setInt(2, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Оценка обновлена");
                    return true;
                } else {
                    log.error("Ошибка обновления");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
            }
        }
    }

    @Override
    public boolean updateGroupWhereMarkWasGiven(int id, Group newGroup) {
        log.debug("Попытка найти оценку по ID");
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, обновления не произошло");
            return false;
        } else {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(updateGroupOfMarkByID))
            {
                statementForUpdate.setInt(1, newGroup.getId());
                statementForUpdate.setInt(2, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Оценка обновлена");
                    return true;
                } else {
                    log.error("Ошибка обновления");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
            }
        }
    }

    @Override
    public boolean updateMarkById(int id, int newMark) {
        log.debug("Попытка найти оценку по ID");
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, обновления не произошло");
            return false;
        } else {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(updateCountOfMarkByID))
            {
                statementForUpdate.setInt(1, newMark);
                statementForUpdate.setInt(2, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Оценка обновлена");
                    return true;
                } else {
                    log.error("Ошибка обновления");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
            }
        }
    }

    @Override
    public boolean deleteMarkById(int id) {
        log.debug("Попытка найти оценку по ID");
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, уделания не произошло");
            return false;
        } else {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(deleteMarkByID))
            {
                statementForUpdate.setInt(1, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Оценка удалена");
                    return true;
                } else {
                    log.error("Ошибка удаления");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
            }
        }
    }

    private void closeResource(AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
