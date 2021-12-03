package subject;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import group.GroupRepositoryPostgresImpl;
import lombok.extern.slf4j.Slf4j;
import secondary.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static constants.Queries.*;

@Slf4j
public class SubjectRepositoryPostgresImpl implements SubjectRepository {
    private static volatile SubjectRepositoryPostgresImpl instance;
    private final ComboPooledDataSource pool;

    private SubjectRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    public static SubjectRepositoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (GroupRepositoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new SubjectRepositoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

    @Override
    public Subject createSubject(Subject subject) {
        log.info("Попытка найти предмет в репозитории");
        Optional<Subject> optionalSubject = getSubjectByName(subject.getName());
        if (optionalSubject.isPresent()) {
            log.error("Ошибка добавления: такой предмет уже существует");
            return null;
        } else {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            log.info("Такого предмета не существует, вносим в таблицу");
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(putSubject);
                save = con.setSavepoint();

                st.setString(1, subject.getName());
                if (st.executeUpdate() > 0) {
                    log.info("Предмет успешно добавлен");
                    con.commit();
                    return subject;
                } else {
                    log.error("Ошибка добавления");
                    con.rollback(save);
                    return null;
                }
            } catch (SQLException e) {
                log.error("Ошибка добавления: SQLException");
                myRollback(con, save);
                return null;
            } finally {
                closeResource(st);
                closeResource(con);
            }
        }
    }

    @Override
    public Optional<Subject> getSubjectById(int id) {
        log.debug("Попытка взять предмет по ID");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSubjectByID))
        {
            st.setInt(1, id);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Берём предмет");
                return Optional.of(new Subject()
                        .withId(set.getInt(1))
                        .withName(set.getString(2)));
            } else {
                log.error("Предмет не найден");
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
    public Optional<Subject> getSubjectByName(String name) {
        log.debug("Попытка взять предмет по названию");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSubjectByName))
        {
            st.setString(1, name);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Берём предмет");
                return Optional.of(new Subject()
                        .withId(set.getInt(1))
                        .withName(set.getString(2)));
            } else {
                log.error("Предмет не найден");
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
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        log.debug("Попытка взять все предметы");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllSubjects))
        {
            set = st.executeQuery();
            while (set.next()) {
                log.info("Берём предмет");
                subjects.add(new Subject()
                        .withId(set.getInt(1))
                        .withName(set.getString(2)));
            }
            return subjects;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return subjects;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public boolean updateSubjectNameById(int id, String newName) {
        log.info("Попытка найти предмет по ID");
        Optional<Subject> optionalSubject = getSubjectById(id);
        if (optionalSubject.isPresent()) {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateSubjectNameByID);
                save = con.setSavepoint();

                st.setString(1, newName);
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Изменение предмета прошло успешно");
                    con.commit();
                    return true;
                } else {
                    log.error("Предмет не найден, изменений не произошло");
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
        } else {
            log.error("Предмет не найден, изменений не произошло");
            return false;
        }
    }

    @Override
    public boolean updateSubjectNameByName(String oldName, String newName) {
        log.info("Попытка найти предмет по названию");
        Optional<Subject> optionalSubject = getSubjectByName(oldName);
        if (optionalSubject.isPresent()) {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateSubjectNameByName);
                save = con.setSavepoint();

                st.setString(1, newName);
                st.setString(2, oldName);
                if (st.executeUpdate() > 0) {
                    log.info("Изменение предмета в репозитории");
                    con.commit();
                    return true;
                } else {
                    log.error("Предмет не найден, изменений не произошло");
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
        } else {
            log.error("Предмет не найден, изменений не произошло");
            return false;
        }
    }

    @Override
    public boolean deleteSubjectById(int id) {
        log.info("Попытка найти предмет по названию");
        Optional<Subject> optionalSubject = getSubjectById(id);
        if (optionalSubject.isPresent()) {
            Connection con = null;
            PreparedStatement stForDeleteSubject = null;
            PreparedStatement stForDeleteSubjectFromGroup = null;
            PreparedStatement stForDeleteSubjectFromMarks = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                stForDeleteSubject = con.prepareStatement(deleteSubjectByID);
                stForDeleteSubjectFromGroup = con.prepareStatement(deleteSubjectFromGroupByID);
                stForDeleteSubjectFromMarks = con.prepareStatement(deleteMarksBySubjectID);
                save = con.setSavepoint();

                stForDeleteSubjectFromGroup.setInt(1, id);
                if (stForDeleteSubjectFromGroup.executeUpdate() > 0) {
                    log.info("Предмет удалён из группы");
                    con.commit();
                } else {
                    log.error("Предмет не удалён из группы, удаление прервано");
                    con.rollback(save);
                    return false;
                }

                stForDeleteSubjectFromMarks.setInt(1, id);
                if (stForDeleteSubjectFromMarks.executeUpdate() > 0) {
                    log.info("Оценки удалены");
                    con.commit();
                } else {
                    log.error("Оценки не удалены, удаление прервано");
                    con.rollback(save);
                    return false;
                }

                stForDeleteSubject.setInt(1, id);
                if (stForDeleteSubject.executeUpdate() > 0) {
                    log.info("Удаление предмета в репозитории");
                    con.commit();
                    return true;
                } else {
                    log.error("Предмет не найден, удаления не произошло");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(stForDeleteSubjectFromGroup);
                closeResource(stForDeleteSubjectFromMarks);
                closeResource(stForDeleteSubject);
                closeResource(con);
            }
        } else {
            log.error("Предмет не найден, удаления не произошло");
            return false;
        }
    }

    @Override
    public boolean deleteSubjectByName(String name) {
        log.info("Попытка найти предмет по названию");
        Optional<Subject> optionalSubject = getSubjectByName(name);
        if (optionalSubject.isPresent()) {
            Subject subject = optionalSubject.get();
            Connection con = null;
            PreparedStatement stForDeleteSubject = null;
            PreparedStatement stForDeleteSubjectFromGroup = null;
            PreparedStatement stForDeleteSubjectFromMarks = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                stForDeleteSubject = con.prepareStatement(deleteSubjectByID);
                stForDeleteSubjectFromGroup = con.prepareStatement(deleteSubjectFromGroupByID);
                stForDeleteSubjectFromMarks = con.prepareStatement(deleteMarksBySubjectID);
                save = con.setSavepoint();

                stForDeleteSubjectFromGroup.setInt(1, subject.getId());
                if (stForDeleteSubjectFromGroup.executeUpdate() > 0) {
                    log.info("Предмет удалён из группы");
                    con.commit();
                } else {
                    log.error("Предмет не удалён из группы, удаление прервано");
                    con.rollback(save);
                    return false;
                }

                stForDeleteSubjectFromMarks.setInt(1, subject.getId());
                if (stForDeleteSubjectFromMarks.executeUpdate() > 0) {
                    log.info("Оценки удалены");
                    con.commit();
                } else {
                    log.error("Оценки не удалены, удаление прервано");
                    con.rollback(save);
                    return false;
                }

                stForDeleteSubject.setInt(1, subject.getId());
                if (stForDeleteSubject.executeUpdate() > 0) {
                    log.info("Удаление предмета в репозитории");
                    con.commit();
                    return true;
                } else {
                    log.error("Предмет не найден, удаления не произошло");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(stForDeleteSubjectFromGroup);
                closeResource(stForDeleteSubjectFromMarks);
                closeResource(stForDeleteSubject);
                closeResource(con);
            }
        } else {
            log.error("Предмет не найден, удаления не произошло");
            return false;
        }
    }

    private void myRollback(Connection connection, Savepoint firstSavePoint) {
        try {
            if (connection != null) {
                connection.rollback(firstSavePoint);
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
