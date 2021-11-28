package subject;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import group.GroupRepositoryPostgresImpl;
import lombok.extern.slf4j.Slf4j;
import secondary.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            log.info("Такого предмета не существует, вносим в таблицу");
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForInsert = connection.prepareStatement(putSubject)) {
                statementForInsert.setString(1, subject.getName());
                if (statementForInsert.executeUpdate() > 0) {
                    log.info("Предмет успешно добавлен");
                    return subject;
                } else {
                    log.error("Ошибка добавления");
                    return null;
                }
            } catch (SQLException e) {
                log.error("Ошибка добавления: SQLException");
                return null;
            }
        }
    }

    @Override
    public Optional<Subject> getSubjectById(int id) {
        log.debug("Попытка взять предмет по ID");
        ResultSet set = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findSubjectByID))
        {
            statementForFind.setInt(1, id);
            set = statementForFind.executeQuery();
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
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findSubjectByName))
        {
            statementForFind.setString(1, name);
            set = statementForFind.executeQuery();
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
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findAllSubjects))
        {
            set = statementForFind.executeQuery();
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
            try (Connection connection = pool.getConnection();
                PreparedStatement statementForUpdate = connection.prepareStatement(updateSubjectNameByID))
            {
                statementForUpdate.setString(1, newName);
                statementForUpdate.setInt(2, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Изменение предмета в репозитории");
                    return true;
                } else {
                    log.error("Предмет не найден, изменений не произошло");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
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
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(updateSubjectNameByName))
            {
                statementForUpdate.setString(1, newName);
                statementForUpdate.setString(2, oldName);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Изменение предмета в репозитории");
                    return true;
                } else {
                    log.error("Предмет не найден, изменений не произошло");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
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
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(deleteSubjectByID))
            {
                statementForUpdate.setInt(1, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Удаление предмета в репозитории");
                    return true;
                } else {
                    log.error("Предмет не найден, удаления не произошло");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
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
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(deleteSubjectByName))
            {
                statementForUpdate.setString(1, name);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Удаление предмета в репозитории");
                    return true;
                } else {
                    log.error("Предмет не найден, удаления не произошло");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                return false;
            }
        } else {
            log.error("Предмет не найден, удаления не произошло");
            return false;
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
