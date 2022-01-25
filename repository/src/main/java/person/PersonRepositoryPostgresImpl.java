package person;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import users.Admin;
import users.Person;
import users.Student;
import users.Teacher;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static constants.Queries.deleteCredentialsByLoginAndPassword;
import static constants.Queries.deleteMarksByStudentID;
import static constants.Queries.deletePersonByID;
import static constants.Queries.deleteSalaryByTeacherID;
import static constants.Queries.deleteStudentFromGroupByID;
import static constants.Queries.findAllPersons;
import static constants.Queries.findCredentialsByLoginAndPassword;
import static constants.Queries.findGroupByTeacherID;
import static constants.Queries.findMarksByStudentID;
import static constants.Queries.findPersonByCredentials;
import static constants.Queries.findPersonByID;
import static constants.Queries.findPersonByName;
import static constants.Queries.findSalariesByTeacherID;
import static constants.Queries.findStudentInGroupByID;
import static constants.Queries.putCredentials;
import static constants.Queries.putPerson;
import static constants.Queries.updatePersonCredentialsByID;
import static constants.Queries.updatePersonDateOfBirthByID;
import static constants.Queries.updatePersonNameByID;
import static constants.Queries.updateTeacherIdAsNull;

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
        Connection con = null;
        PreparedStatement stForInsertCred = null;
        PreparedStatement stForInsertPerson = null;
        Savepoint save = null;
        if (!isPersonFind(person)) {
            try {
                con = pool.getConnection();
                stForInsertCred = con.prepareStatement(putCredentials);
                stForInsertPerson = con.prepareStatement(putPerson);
                con.setAutoCommit(false);
                save = con.setSavepoint();
                stForInsertCred.setString(1, person.getCredentials().getLogin());
                stForInsertCred.setString(2, person.getCredentials().getPassword());
                if (stForInsertCred.executeUpdate() > 0) {
                    log.debug("Учётные данные созданы");
                    con.commit();
                    if (isInsertPerson(stForInsertPerson, person)) {
                        log.info("Пользователь успешно добавлен");
                        con.commit();
                        return person;
                    } else {
                        log.error("Ошибка создания пользователя");
                        con.rollback(save);
                        return null;
                    }
                } else {
                    log.error("Ошибка создания учётных данных");
                    con.rollback(save);
                    return null;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return null;
            } finally {
                closeResource(stForInsertCred);
                closeResource(stForInsertPerson);
                closeResource(con);
            }
        } else {
            log.error("Пользователь уже есть в системе");
            return null;
        }
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findPersonByID)) {
            st.setInt(1, id);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Пользователь найден");
                return findRoleAndReturnPerson(set);
            } else {
                log.error("Пользователь не найден");
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
    public Optional<Person> getPersonByName(String firstName, String lastName, String patronymic) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findPersonByName)) {
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setString(3, patronymic);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Пользователь найден");
                return findRoleAndReturnPerson(set);
            } else {
                log.error("Пользователь не найден");
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
    public Optional<Person> getPersonByCredentials(String login, String password) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findPersonByCredentials)) {
            st.setString(1, login);
            st.setString(2, password);
            set = st.executeQuery();
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
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllPersons)) {
            set = st.executeQuery();
            while (set.next()) {
                Optional<Person> optionalPerson = findRoleAndReturnPerson(set);
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
            closeResource(set);
        }
        return allPersons;
    }

    @Override
    public boolean updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updatePersonNameByID);
            save = con.setSavepoint();

            st.setString(1, newFirstName);
            st.setString(2, newLastName);
            st.setString(3, newPatronymic);
            st.setInt(4, id);
            if (st.executeUpdate() > 0) {
                log.info("Изменение ФИО пользователя в репозитории");
                con.commit();
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
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

    @Override
    public boolean updateDateOfBirthById(int id, LocalDate newDateOfBirth) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updatePersonDateOfBirthByID);
            save = con.setSavepoint();

            st.setDate(1, Date.valueOf(newDateOfBirth));
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Изменение даты рождения пользователя в репозитории");
                con.commit();
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
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

    @Override
    public boolean updateCredentialByPersonId(int id, Credentials newCredential) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updatePersonCredentialsByID);
            save = con.setSavepoint();

            st.setString(1, newCredential.getLogin());
            st.setString(2, newCredential.getPassword());
            st.setInt(3, id);
            if (st.executeUpdate() > 0) {
                log.info("Изменение учётных данных пользователя в репозитории");
                con.commit();
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
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

    @Override
    public boolean deletePersonById(int id) {
        Connection con = null;
        PreparedStatement stForFindSalaries = null;
        PreparedStatement stForFindTeacherInGroup = null;
        PreparedStatement stForFindMarks = null;
        PreparedStatement stForFindStudentInGroup = null;
        PreparedStatement stForDeletePerson = null;
        PreparedStatement stForDeleteCred = null;
        PreparedStatement stForDeleteSalaries = null;
        PreparedStatement stForSetTeacherNull = null;
        PreparedStatement stForDeleteMarks = null;
        PreparedStatement stForDeleteStudentFromGroup = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            // Statements для проверки
            stForFindMarks = con.prepareStatement(findMarksByStudentID);
            stForFindStudentInGroup = con.prepareStatement(findStudentInGroupByID);
            stForFindSalaries = con.prepareStatement(findSalariesByTeacherID);
            stForFindTeacherInGroup = con.prepareStatement(findGroupByTeacherID);
            // Statements для удаления пользователя и его учётных данных
            stForDeletePerson = con.prepareStatement(deletePersonByID);
            stForDeleteCred = con.prepareStatement(deleteCredentialsByLoginAndPassword);
            // Statements для удаления данных об учителе
            stForDeleteSalaries = con.prepareStatement(deleteSalaryByTeacherID);
            stForSetTeacherNull = con.prepareStatement(updateTeacherIdAsNull);
            // Statements для удаления данных об студенте
            stForDeleteMarks = con.prepareStatement(deleteMarksByStudentID);
            stForDeleteStudentFromGroup = con.prepareStatement(deleteStudentFromGroupByID);

            con.setAutoCommit(false);
            save = con.setSavepoint();

            Optional<Person> optionalPerson = getPersonById(id);
            if (optionalPerson.isPresent()) {
                Person person = optionalPerson.get();
                log.info("Начинается удаление пользователя");
                switch (person.getRole()) {
                    case TEACHER:
                        if (isTeacherHasSalaries(stForFindSalaries, person.getId())) {
                            if (isSalariesDeleted(stForDeleteSalaries, person.getId())) {
                                log.info("Зарплаты удалены");
                                con.commit();
                            } else {
                                log.error("Зарплаты не удалены, удаление прервано");
                                con.rollback(save);
                                return false;
                            }
                        }

                        if (isTeacherHasGroup(stForFindTeacherInGroup, person.getId())) {
                            if (isTeacherDeleteFromGroup(stForSetTeacherNull, person.getId())) {
                                log.info("Учитель удалён из группы");
                                con.commit();
                            } else {
                                log.error("Учитель не удалён из группы, удаление прервано");
                                con.rollback(save);
                                return false;
                            }
                        }

                        if (isPersonDeleted(stForDeletePerson, person)) {
                            log.info("Пользователь удалён");
                            con.commit();
                        } else {
                            log.error("Пользователь не удалён, удаления не произошло");
                            con.rollback(save);
                            return false;
                        }

                        if (isCredentialsDeleted(stForDeleteCred, person)) {
                            log.info("Учётные данные удалены, пользователь полностью удалён");
                            con.commit();
                            return true;
                        } else {
                            log.error("Учётные данные пользователя не удалены, удаления не произошло");
                            con.rollback(save);
                            return false;
                        }


                    case STUDENT:
                        if (isStudentHasMarks(stForFindMarks, person.getId())) {
                            if (isMarkDeleted(stForDeleteMarks, person.getId())) {
                                log.info("Оценки удалены");
                                con.commit();
                            } else {
                                log.error("Оценки не удалены, удаление прервано");
                                con.rollback(save);
                                return false;
                            }
                        }

                        if (isStudentInGroup(stForFindStudentInGroup, person.getId())) {
                            if (isStudentDeletedFromGroup(stForDeleteStudentFromGroup, person.getId())) {
                                log.info("Студент удалён из группы");
                                con.commit();
                            } else {
                                log.error("Студент не удалён из группы, удаление прервано");
                                con.rollback(save);
                                return false;
                            }
                        }

                        if (isPersonDeleted(stForDeletePerson, person)) {
                            log.info("Пользователь удалён");
                            con.commit();
                        } else {
                            log.error("Пользователь не удалён, удаления не произошло");
                            con.rollback(save);
                            return false;
                        }

                        if (isCredentialsDeleted(stForDeleteCred, person)) {
                            log.info("Учётные данные удалены, пользователь полностью удалён");
                            con.commit();
                            return true;
                        } else {
                            log.error("Учётные данные пользователя не удалены, удаления не произошло");
                            con.rollback(save);
                            return false;
                        }

                    default:
                        log.error("Пользователь не найден, не удалён");
                        return false;
                }
            } else {
                log.error("Пользователь не найден, удаления не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForFindSalaries);
            closeResource(stForFindTeacherInGroup);
            closeResource(stForFindMarks);
            closeResource(stForFindStudentInGroup);
            closeResource(stForDeleteMarks);
            closeResource(stForDeleteStudentFromGroup);
            closeResource(stForDeleteSalaries);
            closeResource(stForSetTeacherNull);
            closeResource(stForDeletePerson);
            closeResource(stForDeleteCred);
            closeResource(con);
        }
    }

    @Override
    public boolean deletePersonByName(String firstName, String lastName, String patronymic) {
        Connection con = null;
        PreparedStatement stForFindSalaries = null;
        PreparedStatement stForFindTeacherInGroup = null;
        PreparedStatement stForFindMarks = null;
        PreparedStatement stForFindStudentInGroup = null;
        PreparedStatement stForDeletePerson = null;
        PreparedStatement stForDeleteCred = null;
        PreparedStatement stForDeleteSalaries = null;
        PreparedStatement stForSetTeacherNull = null;
        PreparedStatement stForDeleteMarks = null;
        PreparedStatement stForDeleteStudentFromGroup = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            // Statements для проверки
            stForFindMarks = con.prepareStatement(findMarksByStudentID);
            stForFindStudentInGroup = con.prepareStatement(findStudentInGroupByID);
            stForFindSalaries = con.prepareStatement(findSalariesByTeacherID);
            stForFindTeacherInGroup = con.prepareStatement(findGroupByTeacherID);
            // Statements для удаления пользователя и его учётных данных
            stForDeletePerson = con.prepareStatement(deletePersonByID);
            stForDeleteCred = con.prepareStatement(deleteCredentialsByLoginAndPassword);
            // Statements для удаления данных об учителе
            stForDeleteSalaries = con.prepareStatement(deleteSalaryByTeacherID);
            stForSetTeacherNull = con.prepareStatement(updateTeacherIdAsNull);
            // Statements для удаления данных об студенте
            stForDeleteMarks = con.prepareStatement(deleteMarksByStudentID);
            stForDeleteStudentFromGroup = con.prepareStatement(deleteStudentFromGroupByID);
            con.setAutoCommit(false);
            save = con.setSavepoint();
            Optional<Person> optionalPerson = getPersonByName(firstName, lastName, patronymic);
            if (optionalPerson.isPresent()) {
                Person person = optionalPerson.get();
                log.info("Начинается удаление пользователя");
                switch (person.getRole()) {
                    case TEACHER:
                        if (isTeacherHasSalaries(stForFindSalaries, person.getId())) {
                            if (isSalariesDeleted(stForDeleteSalaries, person.getId())) {
                                log.info("Зарплаты удалены");
                                con.commit();
                            } else {
                                log.error("Зарплаты не удалены, удаление прервано");
                                con.rollback(save);
                                return false;
                            }
                        }
                        if (isTeacherHasGroup(stForFindTeacherInGroup, person.getId())) {
                            if (isTeacherDeleteFromGroup(stForSetTeacherNull, person.getId())) {
                                log.info("Учитель удалён из группы");
                                con.commit();
                            } else {
                                log.error("Учитель не удалён из группы, удаление прервано");
                                con.rollback(save);
                                return false;
                            }
                        }
                        if (isPersonDeleted(stForDeletePerson, person)) {
                            log.info("Пользователь удалён");
                            con.commit();
                        } else {
                            log.error("Пользователь не удалён, удаления не произошло");
                            con.rollback(save);
                            return false;
                        }
                        if (isCredentialsDeleted(stForDeleteCred, person)) {
                            log.info("Учётные данные удалены, пользователь полностью удалён");
                            con.commit();
                            return true;
                        } else {
                            log.error("Учётные данные пользователя не удалены, удаления не произошло");
                            con.rollback(save);
                            return false;
                        }

                    case STUDENT:
                        if (isStudentHasMarks(stForFindMarks, person.getId())) {
                            if (isMarkDeleted(stForDeleteMarks, person.getId())) {
                                log.info("Оценки удалены");
                                con.commit();
                            } else {
                                log.error("Оценки не удалены, удаление прервано");
                                con.rollback(save);
                                return false;
                            }
                        }
                        if (isStudentInGroup(stForFindStudentInGroup, person.getId())) {
                            if (isStudentDeletedFromGroup(stForDeleteStudentFromGroup, person.getId())) {
                                log.info("Студент удалён из группы");
                                con.commit();
                            } else {
                                log.error("Студент не удалён из группы, удаление прервано");
                                con.rollback(save);
                                return false;
                            }
                        }
                        if (isPersonDeleted(stForDeletePerson, person)) {
                            log.info("Пользователь удалён");
                            con.commit();
                        } else {
                            log.error("Пользователь не удалён, удаления не произошло");
                            con.rollback(save);
                            return false;
                        }
                        if (isCredentialsDeleted(stForDeleteCred, person)) {
                            log.info("Учётные данные удалены, пользователь полностью удалён");
                            con.commit();
                            return true;
                        } else {
                            log.error("Учётные данные пользователя не удалены, удаления не произошло");
                            con.rollback(save);
                            return false;
                        }
                    default:
                        log.error("Пользователь не найден, не удалён");
                        return false;
                }
            } else {
                log.error("Пользователь не найден, удаления не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForFindSalaries);
            closeResource(stForFindTeacherInGroup);
            closeResource(stForFindMarks);
            closeResource(stForFindStudentInGroup);
            closeResource(stForDeleteMarks);
            closeResource(stForDeleteStudentFromGroup);
            closeResource(stForDeleteSalaries);
            closeResource(stForSetTeacherNull);
            closeResource(stForDeletePerson);
            closeResource(stForDeleteCred);
            closeResource(con);
        }
    }

    // метод для проверки есть ли студент в группе

    private boolean isStudentInGroup(PreparedStatement stForFindStudentInGroup, int studentId) throws SQLException {
        log.info("Проверка, состоит ли студент в группах");
        stForFindStudentInGroup.setInt(1, studentId);
        return stForFindStudentInGroup.executeQuery().next();
    }

    // метод для проверки есть ли у студента оценки

    private boolean isStudentHasMarks(PreparedStatement stForFindMarks, int studentId) throws SQLException {
        log.info("Проверка, есть ли у студента оценки");
        stForFindMarks.setInt(1, studentId);
        return stForFindMarks.executeQuery().next();
    }

    // метод для проверки ведёт ли учитель у какой-либо группы

    private boolean isTeacherHasGroup(PreparedStatement stForFindTeacherInGroup, int teacherId) throws SQLException {
        log.info("Проверка, есть ли у учителя группа, которую он ведёт");
        stForFindTeacherInGroup.setInt(1, teacherId);
        return stForFindTeacherInGroup.executeQuery().next();
    }

    // метод для проверки есть ли у учителя зарплаты

    private boolean isTeacherHasSalaries(PreparedStatement stForFindSalaries, int teacherId) throws SQLException {
        log.info("Проверка, есть ли у учителя зарплаты");
        stForFindSalaries.setInt(1, teacherId);
        return stForFindSalaries.executeQuery().next();
    }

    // метод удаления Student из Group_Student

    private boolean isStudentDeletedFromGroup(PreparedStatement stForDeleteStudentFromGroup, int studentId) throws SQLException {
        log.info("Попытка удаления студента из группы");
        stForDeleteStudentFromGroup.setInt(1, studentId);
        return stForDeleteStudentFromGroup.executeUpdate() > 0;
    }

    // метод удаления Marks у Student

    private boolean isMarkDeleted(PreparedStatement stForDeleteMarks, int studentId) throws SQLException {
        log.info("Попытка удаления оценок студента");
        stForDeleteMarks.setInt(1, studentId);
        return stForDeleteMarks.executeUpdate() > 0;
    }

    // метод по вставлению teacherId у Group как null

    private boolean isTeacherDeleteFromGroup(PreparedStatement stForSetTeacherNull, int teacherId) throws SQLException {
        log.info("Попытка удаления учителя из группы");
        stForSetTeacherNull.setInt(1, teacherId);
        return stForSetTeacherNull.executeUpdate() > 0;
    }

    // метод для удаления Salaries у Teacher

    private boolean isSalariesDeleted(PreparedStatement stForDeleteSalaries, int teacherId) throws SQLException {
        log.info("Попытка удаления зарплат учителя");
        stForDeleteSalaries.setInt(1, teacherId);
        return stForDeleteSalaries.executeUpdate() > 0;
    }

    // метод по удалению самого Person

    private boolean isPersonDeleted(PreparedStatement stForDeletePerson, Person person) throws SQLException {
        log.info("Попытка удаления пользователя из репозитория");
        stForDeletePerson.setInt(1, person.getId());
        return stForDeletePerson.executeUpdate() > 0;
    }

    private boolean isCredentialsDeleted(PreparedStatement stForDeleteCred, Person person) throws SQLException {
        log.info("Попытка удаления учётных данных пользователя из репозитория");
        stForDeleteCred.setString(1, person.getCredentials().getLogin());
        stForDeleteCred.setString(2, person.getCredentials().getPassword());
        return stForDeleteCred.executeUpdate() > 0;
    }

    // методы для возврата и поиска определённого пользователя

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
        switch (setForPerson.getString("role")) {
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

    // метод для вставки пользователя

   private boolean isInsertPerson(PreparedStatement st, Person person) throws SQLException {
       st.setString(1, person.getFirstName());
       st.setString(2, person.getLastName());
       st.setString(3, person.getPatronymic());
       st.setDate(4, Date.valueOf(person.getDateOfBirth()));
       int credId = getCredentialID(person.getCredentials().getLogin(), person.getCredentials().getPassword());
       if (credId != 0) {
           st.setInt(5, credId);
       } else {
           log.error("Учётные данные не найдены");
           return false;
       }
       st.setString(6, person.getRole().getRoleString());
       return st.executeUpdate() > 0;
   }

   // метод для поиска пользователя

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

    // метод для поиска ID у Credentials

    private int getCredentialID(String login, String password) {
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findCredentialsByLoginAndPassword)) {
            st.setString(1, login);
            st.setString(2, password);
            set = st.executeQuery();
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

    // Rollback

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
