package salary;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import group.GroupRepositoryPostgresImpl;
import lombok.extern.slf4j.Slf4j;
import secondary.Salary;
import users.Teacher;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static constants.Queries.*;

@Slf4j
public class SalaryRepositoryPostgresImpl implements SalaryRepository {
    private static volatile SalaryRepositoryPostgresImpl instance;
    private final ComboPooledDataSource pool;

    private SalaryRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    public static SalaryRepositoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (GroupRepositoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new SalaryRepositoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

    @Override
    public Salary createSalary(Salary salary) {
        Connection con = null;
        PreparedStatement stForInsertSalary = null;
        PreparedStatement stForFindTeacher = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForInsertSalary = con.prepareStatement(putSalary);
            stForFindTeacher = con.prepareStatement(findPersonByCredentials);
            save = con.setSavepoint();

            int teacherId = foundTeacherId(stForFindTeacher, (Teacher) salary.getTeacher());
            if (teacherId > 0) {
                stForInsertSalary.setInt(1, teacherId);
                stForInsertSalary.setDate(2, Date.valueOf(salary.getDateOfSalary()));
                stForInsertSalary.setInt(3, salary.getSalary());
                if (stForInsertSalary.executeUpdate() > 0) {
                    log.info("Зарплата успешно добавлена");
                    con.commit();
                    return salary;
                } else {
                    log.error("Ошибка добавления");
                    con.rollback(save);
                    return null;
                }
            } else {
                log.error("Не найден Teacher");
                con.rollback(save);
                return null;
            }
        } catch (SQLException e) {
            log.error("Ошибка добавления: SQLException");
            myRollback(con, save);
            return null;
        } finally {
            closeResource(stForFindTeacher);
            closeResource(stForInsertSalary);
            closeResource(con);
        }
    }

    @Override
    public Optional<Salary> getSalaryByID(int salaryID) {
        log.info("Попытка взять зарплату по ID");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSalaryByID)) {
            st.setInt(1, salaryID);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Найдена зарплата");
                return Optional.of(createSalaryFromSet(set));
            } else {
                log.error("Зарплата не найдена");
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
    public List<Salary> getSalariesByTeacherId(int teacherId) {
        log.debug("Попытка взять зарплаты по ID учителя");
        List<Salary> salaries = new ArrayList<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSalariesByTeacherID)) {
            st.setInt(1, teacherId);
            set = st.executeQuery();
            while (set.next()) {
                salaries.add(createSalaryFromSet(set));
                log.info("Найдена зарплата");
            }
            return salaries;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return salaries;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public List<Salary> getSalariesByDateOfSalary(LocalDate dateOfSalary) {
        log.debug("Попытка взять зарплаты по дате получения");
        List<Salary> salaries = new ArrayList<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSalariesByDate)) {
            st.setDate(1, Date.valueOf(dateOfSalary));
            set = st.executeQuery();
            while (set.next()) {
                salaries.add(createSalaryFromSet(set));
                log.info("Найдена зарплата");
            }
            return salaries;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return salaries;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public List<Salary> getAllSalaries() {
        log.debug("Попытка взять все зарплаты");
        List<Salary> salaries = new ArrayList<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllSalaries)) {
            set = st.executeQuery();
            while (set.next()) {
                salaries.add(createSalaryFromSet(set));
                log.info("Найдена зарплата");
            }
            return salaries;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return salaries;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public boolean updateSalaryById(int id, int newSalary) {
        log.debug("Попытка найти зарплату по ID");
        Optional<Salary> optionalSalary = getSalaryByID(id);
        if (optionalSalary.isEmpty()) {
            log.error("Зарплата не найдена, изменений не произошло");
            return false;
        } else {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateSalaryByID);
                save = con.setSavepoint();

                st.setInt(1, newSalary);
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Зарплата успешно обновлена (обновлён размер зарплаты)");
                    con.commit();
                    return true;
                } else {
                    log.error("Зарплата не обновлена (не обновлён размер зарплаты)");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Зарплата не найдена, изменения не произошло (не обновлён размер зарплаты)");
                myRollback(con ,save);
                return false;
            } finally {
                closeResource(st);
                closeResource(con);
            }
        }
    }

    @Override
    public boolean updateTeacherReceivedSalaryById(int id, Teacher teacher) {
        Optional<Salary> optionalSalary = getSalaryByID(id);
        if (optionalSalary.isEmpty()) {
            log.error("Зарплата не найдена, изменений не произошло");
            return false;
        } else {
            Connection con = null;
            PreparedStatement stForUpdateSalary = null;
            PreparedStatement stForFindTeacher = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                stForUpdateSalary = con.prepareStatement(updateTeacherReceivedBySalaryID);
                stForFindTeacher = con.prepareStatement(findPersonByCredentials);
                save = con.setSavepoint();

                int teacherId = foundTeacherId(stForFindTeacher, teacher);
                if (teacherId > 0) {
                    stForUpdateSalary.setInt(1, teacherId);
                    stForUpdateSalary.setInt(2, id);
                    if (stForUpdateSalary.executeUpdate() > 0) {
                        log.info("Зарплата успешно обновлена (обновлён учитель)");
                        con.commit();
                        return true;
                    } else {
                        log.error("Зарплата не обновлена (не обновлён учитель)");
                        con.rollback(save);
                        return false;
                    }
                } else {
                    log.error("Не найден учитель");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Зарплата не найдена, изменения не произошло (не обновлён учитель)");
                myRollback(con ,save);
                return false;
            } finally {
                closeResource(stForFindTeacher);
                closeResource(stForUpdateSalary);
                closeResource(con);
            }
        }
    }

    @Override
    public boolean updateDateOfSalaryById(int id, LocalDate newDateOfSalary) {
        Optional<Salary> optionalSalary = getSalaryByID(id);
        if (optionalSalary.isEmpty()) {
            log.error("Зарплата не найдена, изменений не произошло");
            return false;
        } else {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateDateOfSalary);
                save = con.setSavepoint();

                st.setDate(1, Date.valueOf(newDateOfSalary));
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Зарплата успешно обновлена (обновлена дата получения)");
                    con.commit();
                    return true;
                } else {
                    log.error("Зарплата не обновлена (не обновлена дата получения)");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Зарплата не найдена, изменения не произошло (не обновлена дата получения)");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(st);
                closeResource(con);
            }
        }
    }

    @Override
    public boolean deleteSalaryById(int id) {
        Optional<Salary> optionalSalary = getSalaryByID(id);
        if (optionalSalary.isEmpty()) {
            log.error("Зарплата не найдена, удаления не произошло");
            return false;
        } else {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(deleteSalaryByID);
                save = con.setSavepoint();

                st.setInt(1, id);
                if (st.executeUpdate() > 0) {
                    log.info("Зарплата успешно удалена");
                    con.commit();
                    return true;
                } else {
                    log.error("Зарплата не удалена");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Зарплата не найдена, удаления не произошло");
                myRollback(con ,save);
                return false;
            } finally {
                closeResource(st);
                closeResource(con);
            }
        }
    }

    private int foundTeacherId(PreparedStatement stForFindTeacher, Teacher teacher) throws SQLException {
        stForFindTeacher.setString(1, teacher.getCredentials().getLogin());
        stForFindTeacher.setString(2, teacher.getCredentials().getPassword());
        ResultSet set = stForFindTeacher.executeQuery();
        if (set.next()) {
            return set.getInt(1);
        } else {
            return 0;
        }
    }

    public Salary createSalaryFromSet(ResultSet set) throws SQLException {
        return new Salary()
                .withId(set.getInt(1))
                .withTeacher(new Teacher()
                        .withId(set.getInt(2))
                        .withFirstName(set.getString(3))
                        .withLastName(set.getString(4))
                        .withPatronymic(set.getString(5))
                        .withDateOfBirth(set.getDate(6).toLocalDate())
                        .withCredentials(new Credentials()
                                .withId(set.getInt(7))
                                .withLogin(set.getString(8))
                                .withPassword(set.getString(9))))
                .withDateOfSalary(set.getDate(10).toLocalDate())
                .withSalary(set.getInt(11));
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
