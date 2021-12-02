package group;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import secondary.Group;
import secondary.Subject;
import users.Person;
import users.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static constants.Queries.findAllGroups;
import static constants.Queries.findGroupByID;
import static constants.Queries.findGroupByName;
import static constants.Queries.findPersonByName;
import static constants.Queries.findSubjectByName;
import static constants.Queries.putGroup;
import static constants.Queries.putStudentAndGroupID;
import static constants.Queries.putSubjectAndGroupID;
import static constants.Queries.updateGroupNameByID;
import static constants.Queries.updateGroupTeacherByID;

@Slf4j
public class GroupRepositoryPostgresImpl implements GroupRepository {
    private static volatile GroupRepositoryPostgresImpl instance;
    private final ComboPooledDataSource pool;

    private GroupRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    public static GroupRepositoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (GroupRepositoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new GroupRepositoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

    @Override
    public Group createGroup(Group group) {
        log.debug("Попытка найти группу в репозитории");
        Connection con = null;
        PreparedStatement stForInsertGroup = null;
        PreparedStatement stForFindTeacherId = null;
        PreparedStatement stForFindSubjectId = null;
        PreparedStatement stForInsertStudents = null;
        PreparedStatement stForInsertSubjects = null;
        Savepoint save = null;

        if (!isGroupFind(group)) {
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                stForInsertGroup = con.prepareStatement(putGroup);
                stForFindTeacherId = con.prepareStatement(findPersonByName);
                stForFindSubjectId = con.prepareStatement(findSubjectByName);
                stForInsertStudents = con.prepareStatement(putStudentAndGroupID);
                stForInsertSubjects = con.prepareStatement(putSubjectAndGroupID);
                save = con.setSavepoint();

                int teacherId = findTeacherId(group, stForFindTeacherId);

                if (teacherId != 0) {
                    stForInsertGroup.setInt(1, teacherId);
                    stForInsertGroup.setString(2, group.getName());
                    if (stForInsertGroup.executeUpdate() > 0) {
                        log.info("Группа добавлена, продолжение создания");
                        con.commit();
                    } else {
                        log.error("Группа не добавлена, создание прекращено");
                        con.rollback(save);
                        return null;
                    }
                } else {
                    log.error("Учитель для группы не найден, создание прекращено");
                    con.rollback(save);
                    return null;
                }

                Optional<Group> optionalGroup = getGroupByName(group.getName());

                if (!group.getStudents().isEmpty()) {
                    List<Integer> studentsId = new ArrayList<>();
                    for (Person student : group.getStudents()) {
                        int studentId = findStudentId(student, stForFindTeacherId);
                        if (studentId != 0) {
                            studentsId.add(studentId);
                        }
                    }
                    if (optionalGroup.isPresent()) {
                        int groupId = optionalGroup.get().getId();

                        for (Integer studentId : studentsId) {
                            stForInsertStudents.setInt(1, studentId);
                            stForInsertStudents.setInt(2, groupId);
                            if (stForInsertStudents.executeUpdate() > 0) {
                                log.info("Студент добавлен в группу, продолжение создания");
                                con.commit();
                            } else {
                                log.error("Студент не добавлен в группу, создание прекращено");
                                con.rollback(save);
                                return null;
                            }
                        }
                    }
                }

                if (!group.getSubjects().isEmpty()) {
                    List<Integer> subjectsId = new ArrayList<>();
                    for (Subject subject : group.getSubjects()) {
                        int subjectId = findSubjectId(subject, stForFindSubjectId);
                        if (subjectId != 0) {
                            subjectsId.add(subjectId);
                        }
                    }

                    if (optionalGroup.isPresent()) {
                        int groupId = optionalGroup.get().getId();

                        for (Integer subjectId : subjectsId) {
                            stForInsertSubjects.setInt(1, subjectId);
                            stForInsertSubjects.setInt(2, groupId);
                            if (stForInsertSubjects.executeUpdate() > 0) {
                                log.info("Предмет добавлен в группу, продолжение создания");
                                con.commit();
                            } else {
                                log.error("Предмет не добавлен в группу, создание прекращено");
                                con.rollback(save);
                                return null;
                            }
                        }
                    }
                }
                return group;
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return null;
            } finally {
                closeResource(stForInsertGroup);
                closeResource(stForFindTeacherId);
                closeResource(stForFindSubjectId);
                closeResource(stForInsertStudents);
                closeResource(stForInsertSubjects);
                closeResource(con);
            }
        } else {
            log.error("Группа с таким названием уже существует, создания не произошло");
            return null;
        }
    }

    @Override
    public Optional<Group> getGroupById(int id) {
        log.debug("Попытка найти группу по ID");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
            PreparedStatement st = con.prepareStatement(findGroupByID)) {
            st.setInt(1, id);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Группа найдена");
                return Optional.of(new Group()
                        .withId(set.getInt(1))
                        .withName(set.getString(2))
                        .withTeacher(new Teacher()
                                .withId(set.getInt(3))
                                .withFirstName(set.getString(4))
                                .withLastName(set.getString(5))
                                .withPatronymic(set.getString(6))
                                .withDateOfBirth(set.getDate(7).toLocalDate())
                                .withCredentials(new Credentials()
                                        .withId(set.getInt(8))
                                        .withLogin(set.getString(9))
                                        .withPassword(set.getString(10)))));
            } else {
                log.error("Группа не найдена");
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
    public Optional<Group> getGroupByName(String name) {
        log.debug("Попытка найти группу по названию");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findGroupByName)) {
            st.setString(1, name);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Группа найдена");
                return Optional.of(new Group()
                        .withId(set.getInt(1))
                        .withName(set.getString(2))
                        .withTeacher(new Teacher()
                                .withId(set.getInt(3))
                                .withFirstName(set.getString(4))
                                .withLastName(set.getString(5))
                                .withPatronymic(set.getString(6))
                                .withDateOfBirth(set.getDate(7).toLocalDate())
                                .withCredentials(new Credentials()
                                        .withId(set.getInt(8))
                                        .withLogin(set.getString(9))
                                        .withPassword(set.getString(10)))));
            } else {
                log.error("Группа не найдена");
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
    public List<Group> getAllGroups() {
        log.debug("Попытка найти все группы");
        List<Group> groups = new ArrayList<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllGroups)) {
            set = st.executeQuery();
            while (set.next()) {
                log.info("Группа найдена");
                groups.add(new Group()
                        .withId(set.getInt(1))
                        .withName(set.getString(2))
                        .withTeacher(new Teacher()
                                .withId(set.getInt(3))
                                .withFirstName(set.getString(4))
                                .withLastName(set.getString(5))
                                .withPatronymic(set.getString(6))
                                .withDateOfBirth(set.getDate(7).toLocalDate())
                                .withCredentials(new Credentials()
                                        .withId(set.getInt(8))
                                        .withLogin(set.getString(9))
                                        .withPassword(set.getString(10)))));
            }
            return groups;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return groups;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public boolean updateGroupNameById(int id, String newName) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateGroupNameByID);
            save = con.setSavepoint();

            st.setString(1, newName);
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Навзание группы обновлено");
                con.commit();
                return true;
            } else {
                log.error("Группа не найдена, обновления не произошло");
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
    public boolean updateGroupTeacherById(int id, Person newTeacher) {
        Connection con = null;
        PreparedStatement stForFindTeacher = null;
        PreparedStatement stForUpdate = null;
        Savepoint save = null;
        ResultSet setForTeacher = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForFindTeacher = con.prepareStatement(findPersonByName);
            stForUpdate = con.prepareStatement(updateGroupTeacherByID);
            save = con.setSavepoint();

            stForFindTeacher.setString(1, newTeacher.getFirstName());
            stForFindTeacher.setString(2, newTeacher.getLastName());
            stForFindTeacher.setString(3, newTeacher.getPatronymic());

            setForTeacher = stForFindTeacher.executeQuery();
            if (setForTeacher.next()) {
                int teacherId = setForTeacher.getInt(1);
                stForUpdate.setInt(1, teacherId);
                stForUpdate.setInt(2, id);
                if (stForUpdate.executeUpdate() > 0) {
                    log.info("Учитель группы обновлён");
                    con.commit();
                    return true;
                } else {
                    log.error("Группа не найдена, обновления не произошло");
                    con.rollback(save);
                    return false;
                }
            } else {
                log.error("Новый учитель не найден, обновления не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(setForTeacher);
            closeResource(stForFindTeacher);
            closeResource(stForUpdate);
            closeResource(con);
        }
    }

    @Override
    public boolean deleteGroupById(int id) {
        return false;
    }

    // метод для поиска группы

    private boolean isGroupFind(Group group) {
        Optional<Group> optionalGroup = getGroupByName(group.getName());
        if (optionalGroup.isPresent()) {
            log.error("Группа с таким названием уже существует");
            return true;
        } else {
            log.info("Группа не найдена, начинаем создание");
            return false;
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

    // метод для поиска ID Teacher

    private int findTeacherId(Group group, PreparedStatement stForFindTeacherId) throws SQLException {
        stForFindTeacherId.setString(1, group.getTeacher().getFirstName());
        stForFindTeacherId.setString(2, group.getTeacher().getLastName());
        stForFindTeacherId.setString(3, group.getTeacher().getPatronymic());

        ResultSet setForTeacher = stForFindTeacherId.executeQuery();
        if (setForTeacher.next()) {
            return setForTeacher.getInt(1);
        } else {
            return 0;
        }
    }

    // метод для поиска ID Student

    private int findStudentId(Person student, PreparedStatement stForFindStudentId) throws SQLException {
        stForFindStudentId.setString(1, student.getFirstName());
        stForFindStudentId.setString(2, student.getLastName());
        stForFindStudentId.setString(3, student.getPatronymic());

        ResultSet setForStudent = stForFindStudentId.executeQuery();
        if (setForStudent.next()) {
            return setForStudent.getInt(1);
        } else {
            return 0;
        }
    }

    // метод для поиска ID Subject

    private int findSubjectId(Subject subject, PreparedStatement stForFindSubject) throws SQLException {
        stForFindSubject.setString(1, subject.getName());

        ResultSet setForSubject = stForFindSubject.executeQuery();
        if (setForSubject.next()) {
            return setForSubject.getInt(1);
        } else {
            return 0;
        }
    }
}
