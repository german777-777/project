package person;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import users.Admin;
import users.Person;
import users.Student;
import users.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
        log.debug("Попытка найти пользователя в репозитории");
        return null;
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet setForPerson = null;
        ResultSet setForCredentials = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFindPerson = connection.prepareStatement(findPersonByID);
             PreparedStatement statementForFindCredentials = connection.prepareStatement(findCredentialsByID)) {
            statementForFindPerson.setInt(1, id);
            setForPerson = statementForFindPerson.executeQuery();
            if (setForPerson.next()) {
                log.info("Пользователь найден");
                statementForFindCredentials.setInt(1, setForPerson.getInt(6));
                setForCredentials = statementForFindCredentials.executeQuery();
                if (setForCredentials.next()) {
                    return findRoleAndReturnPerson(setForPerson, setForCredentials);
                } else {
                    log.error("Не найдены учётные данные пользователя, поиск прекращён");
                    return Optional.empty();
                }
            } else {
                log.error("Пользователь не найден");
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        } finally {
            closeResource(setForPerson);
            closeResource(setForCredentials);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Person> getPersonByName(String firstName, String lastName, String patronymic) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet setForPerson = null;
        ResultSet setForCredentials = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForPerson = connection.prepareStatement(findPersonByName);
             PreparedStatement statementForCredentials = connection.prepareStatement(findCredentialsByID)) {
            statementForPerson.setString(1, firstName);
            statementForPerson.setString(2, lastName);
            statementForPerson.setString(3, patronymic);
            setForPerson = statementForPerson.executeQuery();
            if (setForPerson.next()) {
                log.info("Пользователь найден");
                statementForCredentials.setInt(1, setForPerson.getInt(6));
                setForCredentials = statementForCredentials.executeQuery();
                if (setForCredentials.next()) {
                    return findRoleAndReturnPerson(setForPerson, setForCredentials);
                } else {
                    log.error("Не найдены учётные данные пользователя, поиск прекращён");
                    return Optional.empty();
                }
            } else {
                log.error("Пользователь не найден");
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        } finally {
            closeResource(setForPerson);
            closeResource(setForCredentials);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Person> getPersonByCredentials(String login, String password) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet setForPerson = null;
        ResultSet setForCredentials = null;
        try (Connection connection = pool.getConnection();
            PreparedStatement statementForPerson = connection.prepareStatement(findPersonByCredentials);
            PreparedStatement statementForCredentials = connection.prepareStatement(findCredentialsByLoginAndPassword))
        {
            statementForCredentials.setString(1, login);
            statementForCredentials.setString(2, password);
            setForCredentials = statementForCredentials.executeQuery();
            if (setForCredentials.next()) {
                statementForPerson.setInt(1, setForCredentials.getInt(1));
                setForPerson = statementForPerson.executeQuery();
                if (setForPerson.next()) {
                    return findRoleAndReturnPerson(setForPerson, setForCredentials);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResource(setForPerson);
            closeResource(setForCredentials);
        }
        return Optional.empty();
    }

    @Override
    public List<Person> getAllPersons() {
        return null;
    }

    @Override
    public boolean updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic) {
        return false;
    }

    @Override
    public boolean updateDateOfBirthById(int id, LocalDate newDateOfBirth) {
        return false;
    }

    @Override
    public boolean updateCredentialByPersonId(int id, Credentials newCredential) {
        return false;
    }

    @Override
    public boolean deletePersonById(int id) {
        return false;
    }

    @Override
    public boolean deletePersonByName(String firstName, String lastName, String patronymic) {
        return false;
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

    private Optional<Person> returnAdmin(ResultSet setForPerson, ResultSet setForCredentials) throws SQLException {
        return Optional.of(new Admin()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(LocalDate.parse(setForPerson.getDate(5).toString()))
                .withCredentials(new Credentials()
                        .withId(setForCredentials.getInt(1))
                        .withLogin(setForCredentials.getString(2))
                        .withPassword(setForCredentials.getString(3))));
    }

    private Optional<Person> returnTeacher(ResultSet setForPerson, ResultSet setForCredentials) throws SQLException {
        return Optional.of(new Teacher()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(LocalDate.parse(setForPerson.getDate(5).toString()))
                .withCredentials(new Credentials()
                        .withId(setForCredentials.getInt(1))
                        .withLogin(setForCredentials.getString(2))
                        .withPassword(setForCredentials.getString(3))));
    }

    private Optional<Person> returnStudent(ResultSet setForPerson, ResultSet setForCredentials) throws SQLException {
        return Optional.of(new Student()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(LocalDate.parse(setForPerson.getDate(5).toString()))
                .withCredentials(new Credentials()
                        .withId(setForCredentials.getInt(1))
                        .withLogin(setForCredentials.getString(2))
                        .withPassword(setForCredentials.getString(3))));
    }

    private Optional<Person> findRoleAndReturnPerson(ResultSet setForPerson, ResultSet setForCredentials) throws SQLException {
        switch (setForPerson.getString(7)) {
            case "Админ":
                log.info("Пользователь - Admin");
                return returnAdmin(setForPerson, setForCredentials);
            case "Учитель":
                log.info("Пользователь - Teacher");
                return returnTeacher(setForPerson, setForCredentials);
            case "Студент":
                log.info("Пользователь - Student");
                return returnStudent(setForPerson, setForCredentials);
        }
        return Optional.empty();
    }
}
