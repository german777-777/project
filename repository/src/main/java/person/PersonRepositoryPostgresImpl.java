package person;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import users.Admin;
import users.Person;
import users.Student;
import users.Teacher;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static constants.Queries.*;

@Slf4j
public class PersonRepositoryPostgresImpl implements PersonRepository {
    private static volatile PersonRepositoryPostgresImpl instance;
    private final ComboPooledDataSource pool;

    private PersonRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    public static PersonRepositoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (PersonRepositoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new PersonRepositoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

    @Override
    public Person createPerson(Person person) {
        log.debug("Попытка найти пользователя");
        Connection connection = null;
        PreparedStatement statementForInsertCredentials = null;
        PreparedStatement statementForInsertPerson = null;
        Savepoint firstSavePoint = null;
        if (!isPersonFind(person)) {
            try {
                connection = pool.getConnection();
                statementForInsertCredentials = connection.prepareStatement(putCredentials);
                statementForInsertPerson = connection.prepareStatement(putPerson);
                connection.setAutoCommit(false);
                firstSavePoint = connection.setSavepoint();

                statementForInsertCredentials.setString(1, person.getCredentials().getLogin());
                statementForInsertCredentials.setString(2, person.getCredentials().getPassword());
                if (statementForInsertCredentials.executeUpdate() > 0) {
                    log.debug("Учётные данные вставлены, продолжение создания");
                    if (isInsertPerson(statementForInsertPerson, person)) {
                        log.info("Пользователь успешно добавлен");
                        connection.commit();
                        return person;
                    } else {
                        log.error("Ошибка вставки пользователя");
                        connection.rollback(firstSavePoint);
                        return null;
                    }
                } else {
                    log.error("Ошибка вставки учётных данных");
                    connection.rollback(firstSavePoint);
                    return null;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(connection, firstSavePoint);
                return null;
            } finally {
                closeResource(statementForInsertCredentials);
                closeResource(statementForInsertPerson);
                closeResource(connection);
            }
        } else {
            return null;
        }
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet setForPerson = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFindPerson = connection.prepareStatement(findPersonByID)) {
            statementForFindPerson.setInt(1, id);
            setForPerson = statementForFindPerson.executeQuery();
            if (setForPerson.next()) {
                log.info("Пользователь найден");
                return findRoleAndReturnPerson(setForPerson);
            } else {
                log.error("Пользователь не найден");
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return Optional.empty();
        } finally {
            closeResource(setForPerson);
        }
    }

    @Override
    public Optional<Person> getPersonByName(String firstName, String lastName, String patronymic) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet setForPerson = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForPerson = connection.prepareStatement(findPersonByName)) {
            statementForPerson.setString(1, firstName);
            statementForPerson.setString(2, lastName);
            statementForPerson.setString(3, patronymic);
            setForPerson = statementForPerson.executeQuery();
            if (setForPerson.next()) {
                log.info("Пользователь найден");
                return findRoleAndReturnPerson(setForPerson);
            } else {
                log.error("Пользователь не найден");
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return Optional.empty();
        } finally {
            closeResource(setForPerson);
        }
    }

    @Override
    public Optional<Person> getPersonByCredentials(String login, String password) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet set = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findPersonByCredentials)) {
            statementForFind.setString(1, login);
            statementForFind.setString(2, password);
            set = statementForFind.executeQuery();
            if (set.next()) {
                log.info("Пользователь найден");
                return findRoleAndReturnPerson(set);
            } else {
                log.error("Не найдены учётные данные пользователя, поиск прекращён");
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
    public List<Person> getAllPersons() {
        List<Person> allPersons = new ArrayList<>();
        log.info("Берём всех пользователей из репозитория");
        ResultSet setForPerson = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForPerson = connection.prepareStatement(findAllPersons)) {
            setForPerson = statementForPerson.executeQuery();
            while (setForPerson.next()) {
                Optional<Person> optionalPerson = findRoleAndReturnPerson(setForPerson);
                if (optionalPerson.isPresent()) {
                    Person person = optionalPerson.get();
                    allPersons.add(person);
                } else {
                    log.error("Пользователь не найден");
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        } finally {
            closeResource(setForPerson);
        }
        return allPersons;
    }

    @Override
    public boolean updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic) {
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForUpdate = connection.prepareStatement(updatePersonNameByID)) {
            statementForUpdate.setString(1, newFirstName);
            statementForUpdate.setString(2, newLastName);
            statementForUpdate.setString(3, newPatronymic);
            statementForUpdate.setInt(4, id);
            if (statementForUpdate.executeUpdate() > 0) {
                log.info("Изменение ФИО пользователя в репозитории");
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return false;
        }
    }

    @Override
    public boolean updateDateOfBirthById(int id, LocalDate newDateOfBirth) {
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForUpdate = connection.prepareStatement(updatePersonDateOfBirthByID)) {
            statementForUpdate.setDate(1, Date.valueOf(newDateOfBirth));
            statementForUpdate.setInt(2, id);
            if (statementForUpdate.executeUpdate() > 0) {
                log.info("Изменение даты рождения пользователя в репозитории");
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return false;
        }

    }

    @Override
    public boolean updateCredentialByPersonId(int id, Credentials newCredential) {
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForUpdate = connection.prepareStatement(updatePersonCredentialsByID)) {
            statementForUpdate.setString(1, newCredential.getLogin());
            statementForUpdate.setString(2, newCredential.getPassword());
            statementForUpdate.setInt(3, id);
            if (statementForUpdate.executeUpdate() > 0) {
                log.info("Изменение учётных данных пользователя в репозитории");
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return false;
        }
    }

    @Override
    public boolean deletePersonById(int id) {
        Connection connection = null;
        PreparedStatement statementForDeletePerson = null;
        PreparedStatement statementForDeleteCredentials = null;
        Savepoint firstSavePoint = null;
        try {
            connection = pool.getConnection();
            statementForDeletePerson = connection.prepareStatement(deletePersonByID);
            statementForDeleteCredentials = connection.prepareStatement(deleteCredentialsByLoginAndPassword);
            connection.setAutoCommit(false);
            firstSavePoint = connection.setSavepoint();

            Optional<Person> optionalPerson = getPersonById(id);
            if (optionalPerson.isPresent()) {
                log.info("Попытка удалить пользователя из репозитория");
                statementForDeletePerson.setInt(1, id);
                if (statementForDeletePerson.executeUpdate() > 0) {
                    log.info("Пользователь успешно удалён");
                } else {
                    log.error("Пользователь не найден, удаления не произошло");
                    connection.rollback(firstSavePoint);
                    return false;
                }
                log.info("Попытка удаления учётных данных пользователя из репозитория");
                Person person = optionalPerson.get();
                statementForDeleteCredentials.setString(1, person.getCredentials().getLogin());
                statementForDeleteCredentials.setString(2, person.getCredentials().getPassword());
                if (statementForDeleteCredentials.executeUpdate() > 0) {
                    log.info("Учётные данные пользователя удалены, удаление пользователя завершено");
                    connection.commit();
                    return true;
                } else {
                    log.error("Учётные данные пользователя не удалены, удаление прервано");
                    connection.rollback(firstSavePoint);
                    return false;
                }

            } else {
                log.error("Пользователь не найден, удаления не произошло");
                connection.rollback(firstSavePoint);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(connection, firstSavePoint);
            return false;
        } finally {
            closeResource(statementForDeletePerson);
            closeResource(statementForDeleteCredentials);
            closeResource(connection);
        }
    }

    @Override
    public boolean deletePersonByName(String firstName, String lastName, String patronymic) {
        Connection connection = null;
        PreparedStatement statementForDeletePerson = null;
        PreparedStatement statementForDeleteCredentials = null;
        Savepoint firstSavePoint = null;
        try {
            connection = pool.getConnection();
            statementForDeletePerson = connection.prepareStatement(deletePersonByName);
            statementForDeleteCredentials = connection.prepareStatement(deleteCredentialsByLoginAndPassword);
            connection.setAutoCommit(false);
            firstSavePoint = connection.setSavepoint();

            Optional<Person> optionalPerson = getPersonByName(firstName, lastName, patronymic);
            if (optionalPerson.isPresent()) {
                log.info("Попытка удалить пользователя из репозитория");
                statementForDeletePerson.setString(1, firstName);
                statementForDeletePerson.setString(2, lastName);
                statementForDeletePerson.setString(3, patronymic);
                if (statementForDeletePerson.executeUpdate() > 0) {
                    log.info("Пользователь успешно удалён");
                } else {
                    log.error("Пользователь не найден, удаления не произошло");
                    connection.rollback(firstSavePoint);
                    return false;
                }
                log.info("Попытка удаления учётных данных пользователя из репозитория");
                Person person = optionalPerson.get();
                statementForDeleteCredentials.setString(1, person.getCredentials().getLogin());
                statementForDeleteCredentials.setString(2, person.getCredentials().getPassword());
                if (statementForDeleteCredentials.executeUpdate() > 0) {
                    log.info("Учётные данные пользователя удалены, удаление пользователя завершено");
                    connection.commit();
                    return true;
                } else {
                    log.error("Учётные данные пользователя не удалены, удаление прервано");
                    connection.rollback(firstSavePoint);
                    return false;
                }

            } else {
                log.error("Пользователь не найден, удаления не произошло");
                connection.rollback(firstSavePoint);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(connection, firstSavePoint);
            return false;
        } finally {
            closeResource(statementForDeletePerson);
            closeResource(statementForDeleteCredentials);
            closeResource(connection);
        }
    }


    private Optional<Person> returnAdmin(ResultSet setForPerson) throws SQLException {
        return Optional.of(new Admin()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(setForPerson.getDate(5).toLocalDate())
                .withCredentials(new Credentials()
                        .withId(setForPerson.getInt(7))
                        .withLogin(setForPerson.getString(8))
                        .withPassword(setForPerson.getString(9))));
    }

    private Optional<Person> returnTeacher(ResultSet setForPerson) throws SQLException {
        return Optional.of(new Teacher()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(setForPerson.getDate(5).toLocalDate())
                .withCredentials(new Credentials()
                        .withId(setForPerson.getInt(7))
                        .withLogin(setForPerson.getString(8))
                        .withPassword(setForPerson.getString(9))));
    }

    private Optional<Person> returnStudent(ResultSet setForPerson) throws SQLException {
        return Optional.of(new Student()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(setForPerson.getDate(5).toLocalDate())
                .withCredentials(new Credentials()
                        .withId(setForPerson.getInt(7))
                        .withLogin(setForPerson.getString(8))
                        .withPassword(setForPerson.getString(9))));
    }

    private Optional<Person> findRoleAndReturnPerson(ResultSet setForPerson) throws SQLException {
        switch (setForPerson.getString(6)) {
            case "Админ":
                log.info("Пользователь - Admin");
                return returnAdmin(setForPerson);
            case "Учитель":
                log.info("Пользователь - Teacher");
                return returnTeacher(setForPerson);
            case "Студент":
                log.info("Пользователь - Student");
                return returnStudent(setForPerson);
        }
        return Optional.empty();
    }

    private boolean isInsertPerson(PreparedStatement statementForInsertPerson, Person person) throws SQLException {
        statementForInsertPerson.setString(1, person.getFirstName());
        statementForInsertPerson.setString(2, person.getLastName());
        statementForInsertPerson.setString(3, person.getPatronymic());
        statementForInsertPerson.setDate(4, Date.valueOf(person.getDateOfBirth()));
        statementForInsertPerson.setInt(5, getCredentialID(person.getCredentials().getLogin(), person.getCredentials().getPassword()));
        statementForInsertPerson.setString(6, person.getRole().getRoleString());
        return statementForInsertPerson.executeUpdate() > 0;
    }

    private boolean isPersonFind(Person person) {
        Optional<Person> optionalPerson = getPersonByName(person.getFirstName(), person.getLastName(), person.getPatronymic());
        if (optionalPerson.isPresent()) {
            log.error("Пользователь с таким ФИО уже существует");
            return true;
        } else {
            optionalPerson = getPersonByCredentials(person.getCredentials().getLogin(), person.getCredentials().getPassword());
            if (optionalPerson.isPresent()) {
                log.error("Пользователь с такими учётными данными уже существует");
                return true;
            }
        }
        return false;
    }

    private int getCredentialID(String login, String password) {
        ResultSet set = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findCredentialsByLoginAndPassword)) {
            statementForFind.setString(1, login);
            statementForFind.setString(2, password);
            set = statementForFind.executeQuery();
            if (set.next()) {
                return set.getInt(1);
            } else {
                log.error("Учётные данные не найдены");
                return 0;
            }
        } catch (SQLException e) {
            log.error("Учётные данные не найдены");
            return 0;
        } finally {
            closeResource(set);
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
