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
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        log.debug("Попытка добавить оценку");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(putMark);
            save = con.setSavepoint();

            st.setInt(1, mark.getStudentId());
            st.setInt(2, mark.getGroupId());
            st.setInt(3, mark.getSubjectId());
            st.setDate(4, Date.valueOf(mark.getDateOfMark()));
            st.setInt(5, mark.getMark());
            if (st.executeUpdate() > 0) {
                log.info("Оценка успешно добавлена");
                con.commit();
                return mark;
            } else {
                log.error("Оценка не добавлена");
                con.rollback(save);
                return null;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return null;
        } finally {
            closeResource(st);
            closeResource(con);
        }
    }

    @Override
    public Optional<Mark> getMarkByID(int id) {
        log.debug("Попытка найти оценку по id");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findMarkByID)) {
            st.setInt(1, id);
            set = st.executeQuery();
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
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllMarks)) {
            set = st.executeQuery();
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
        } finally {
            closeResource(set);
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
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateSubjectOfMarkByID);
                save = con.setSavepoint();

                st.setInt(1, newSubject.getId());
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Оценка обновлена");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка обновления");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(st);
                closeResource(con);
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
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateSubjectOfMarkByID);
                save = con.setSavepoint();

                st.setDate(1, Date.valueOf(newDateOfMark));
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Оценка обновлена");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка обновления");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(st);
                closeResource(con);
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
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateSubjectOfMarkByID);
                save = con.setSavepoint();

                st.setInt(1, newGroup.getId());
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Оценка обновлена");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка обновления");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(st);
                closeResource(con);
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
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateCountOfMarkByID);
                save = con.setSavepoint();

                st.setInt(1, newMark);
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Оценка обновлена");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка обновления");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(st);
                closeResource(con);
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
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(deleteMarkByID);
                save = con.setSavepoint();

                st.setInt(1, id);
                if (st.executeUpdate() > 0) {
                    log.info("Оценка удалена");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка удаления");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(st);
                closeResource(con);
            }
        }
    }


    private void myRollback(Connection con, Savepoint save) {
        try {
            if (con != null) {
                con.rollback(save);
            }
        } catch (SQLException ex) {
            log.error("Rollback не удался");
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
