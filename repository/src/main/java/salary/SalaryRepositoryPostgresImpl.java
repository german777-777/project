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
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForInsert = connection.prepareStatement(putSalary))
        {
            statementForInsert.setInt(1, salary.getTeacher().getId());
            statementForInsert.setDate(2, Date.valueOf(salary.getDateOfSalary()));
            statementForInsert.setInt(3, salary.getSalary());
            if (statementForInsert.executeUpdate() > 0) {
                log.info("Зарплата успешно добавлена");
                return salary;
            } else {
                log.error("Ошибка добавления");
                return null;
            }
        } catch (SQLException e) {
            log.error("Ошибка добавления: SQLException");
            return null;
        }
    }

    @Override
    public Optional<Salary> getSalaryByID(int salaryID) {
        log.info("Попытка взять зарплату по ID");
        ResultSet set = null;
        try (Connection connection = pool.getConnection();
            PreparedStatement statementForFind = connection.prepareStatement(findSalaryByID))
        {
            statementForFind.setInt(1, salaryID);
            set = statementForFind.executeQuery();
            if (set.next()) {
                log.info("Найдена зарплата");
                return Optional.of(new Salary()
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
                        .withSalary(set.getInt(11)));
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
        try (Connection connection = pool.getConnection();
            PreparedStatement statementForFind = connection.prepareStatement(findSalariesByTeacherID))
        {
            statementForFind.setInt(1, teacherId);
            set = statementForFind.executeQuery();
            while (set.next()) {
                salaries.add(new Salary()
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
                        .withSalary(set.getInt(11)));
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
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findSalariesByDate))
        {
            statementForFind.setDate(1, Date.valueOf(dateOfSalary));
            set = statementForFind.executeQuery();
            while (set.next()) {
                salaries.add(new Salary()
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
                        .withSalary(set.getInt(11)));
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
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findAllSalaries))
        {
            set = statementForFind.executeQuery();
            while (set.next()) {
                salaries.add(new Salary()
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
                        .withSalary(set.getInt(11)));
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
            try (Connection connection = pool.getConnection();
                PreparedStatement statementForUpdate = connection.prepareStatement(updateSalaryByID))
            {
                statementForUpdate.setInt(1, newSalary);
                statementForUpdate.setInt(2, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Зарплата успешно обновлена");
                    return true;
                } else {
                    log.error("Зарплата не обновлена");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Зарплата не найдена, изменения не произошло");
                return false;
            }
        }
    }

    @Override
    public boolean updateTeacherReceivedSalaryById(int id, Teacher teacher) {
        log.debug("Попытка найти зарплату по ID");
        Optional<Salary> optionalSalary = getSalaryByID(id);
        if (optionalSalary.isEmpty()) {
            log.error("Зарплата не найдена, изменений не произошло");
            return false;
        } else {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(updateTeacherReceivedBySalaryID))
            {
                statementForUpdate.setInt(1, teacher.getId());
                statementForUpdate.setInt(2, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Зарплата успешно обновлена");
                    return true;
                } else {
                    log.error("Зарплата не обновлена");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Зарплата не найдена, изменения не произошло");
                return false;
            }
        }
    }

    @Override
    public boolean updateDateOfSalaryById(int id, LocalDate newDateOfSalary) {
        log.debug("Попытка найти зарплату по ID");
        Optional<Salary> optionalSalary = getSalaryByID(id);
        if (optionalSalary.isEmpty()) {
            log.error("Зарплата не найдена, изменений не произошло");
            return false;
        } else {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(updateDateOfSalary))
            {
                statementForUpdate.setDate(1, Date.valueOf(newDateOfSalary));
                statementForUpdate.setInt(2, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Зарплата успешно обновлена");
                    return true;
                } else {
                    log.error("Зарплата не обновлена");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Зарплата не найдена, изменения не произошло");
                return false;
            }
        }
    }

    @Override
    public boolean deleteSalaryById(int id) {
        log.debug("Попытка найти зарплату по ID");
        Optional<Salary> optionalSalary = getSalaryByID(id);
        if (optionalSalary.isEmpty()) {
            log.error("Зарплата не найдена, изменений не произошло");
            return false;
        } else {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statementForUpdate = connection.prepareStatement(deleteSalaryByID))
            {
                statementForUpdate.setInt(1, id);
                if (statementForUpdate.executeUpdate() > 0) {
                    log.info("Зарплата успешно удалена");
                    return true;
                } else {
                    log.error("Зарплата не удалена");
                    return false;
                }
            } catch (SQLException e) {
                log.error("Зарплата не найдена, удаления не произошло");
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
