package group;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import secondary.Group;
import secondary.Subject;
import users.Person;
import users.Student;
import users.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static constants.Queries.deleteGroupById;
import static constants.Queries.deleteGroupWithStudentsByID;
import static constants.Queries.deleteGroupWithSubjectsByID;
import static constants.Queries.deleteMarksByGroupID;
import static constants.Queries.deleteStudentFromGroupByID;
import static constants.Queries.deleteSubjectFromGroupByID;
import static constants.Queries.findAllGroups;
import static constants.Queries.findGroupByID;
import static constants.Queries.findGroupByName;
import static constants.Queries.findGroupWithStudentsByID;
import static constants.Queries.findMarksByGroupID;
import static constants.Queries.findPersonByID;
import static constants.Queries.findPersonByName;
import static constants.Queries.findSubjectByID;
import static constants.Queries.findSubjectByName;
import static constants.Queries.findSubjectsByGroupID;
import static constants.Queries.putGroup;
import static constants.Queries.putStudentAndGroupID;
import static constants.Queries.updateGroupNameByID;
import static constants.Queries.updateGroupTeacherByID;
import static constants.Queries.putSubjectAndGroupID;

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

                if (group.getTeacher() != null) {
                    int teacherId = findTeacherId(group, stForFindTeacherId);
                    stForInsertGroup.setInt(1, teacherId);
                } else {
                    stForInsertGroup.setInt(1, 0);
                }

                stForInsertGroup.setString(2, group.getName());
                if (stForInsertGroup.executeUpdate() > 0) {
                    log.info("Группа добавлена");
                    con.commit();
                } else {
                    log.error("Группа не добавлена");
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
            log.error("Создания не произошло");
            return null;
        }
    }

    @Override
    public Optional<Group> getGroupById(int id) {
        log.debug("Попытка найти группу по ID");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findGroupByID);
             PreparedStatement stForFindAllStudents = con.prepareStatement(findGroupWithStudentsByID);
             PreparedStatement stForFindAllSubjects = con.prepareStatement(findSubjectsByGroupID);
             PreparedStatement stForFindStudentById = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectById = con.prepareStatement(findSubjectByID)) {
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
                                        .withPassword(set.getString(10))))
                        .withStudents(getAllStudents(stForFindAllStudents, set.getInt(1), stForFindStudentById))
                        .withSubjects(getAllSubjects(stForFindAllSubjects, set.getInt(1), stForFindSubjectById)));
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
             PreparedStatement st = con.prepareStatement(findGroupByName);
             PreparedStatement stForFindAllStudents = con.prepareStatement(findGroupWithStudentsByID);
             PreparedStatement stForFindAllSubjects = con.prepareStatement(findSubjectsByGroupID);
             PreparedStatement stForFindStudentById = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectById = con.prepareStatement(findSubjectByID)) {
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
                                        .withPassword(set.getString(10))))
                        .withStudents(getAllStudents(stForFindAllStudents, set.getInt(1), stForFindStudentById))
                        .withSubjects(getAllSubjects(stForFindAllSubjects, set.getInt(1), stForFindSubjectById)));
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
             PreparedStatement st = con.prepareStatement(findAllGroups);
             PreparedStatement stForFindAllStudents = con.prepareStatement(findGroupWithStudentsByID);
             PreparedStatement stForFindAllSubjects = con.prepareStatement(findSubjectsByGroupID);
             PreparedStatement stForFindStudentById = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectById = con.prepareStatement(findSubjectByID)) {
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
                                        .withPassword(set.getString(10))))
                        .withStudents(getAllStudents(stForFindAllStudents, set.getInt(1), stForFindStudentById))
                        .withSubjects(getAllSubjects(stForFindAllSubjects, set.getInt(1), stForFindSubjectById)));
            }
            return groups;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return groups;
        } finally {
            closeResource(set);
        }
    }

    private Set<Subject> getAllSubjects(PreparedStatement stForFindAllSubjectsInGroup, int groupId, PreparedStatement stForFindSubjectById) throws SQLException {
        Set<Subject> subjects = new HashSet<>();

        stForFindAllSubjectsInGroup.setInt(1, groupId);
        ResultSet setForGroup = stForFindAllSubjectsInGroup.executeQuery();

        while (setForGroup.next()) {
            stForFindSubjectById.setInt(1, setForGroup.getInt(1));

            ResultSet setForSubjects = stForFindSubjectById.executeQuery();
            if (setForSubjects.next()) {
                subjects.add(new Subject()
                        .withId(setForSubjects.getInt(1))
                        .withName(setForSubjects.getString(2)));
            }
        }
        return subjects;
    }

    private Set<Person> getAllStudents(PreparedStatement stForFindAllStudentsInGroup, int groupId, PreparedStatement stForFindStudentById) throws SQLException {
        Set<Person> students = new HashSet<>();

        stForFindAllStudentsInGroup.setInt(1, groupId);
        ResultSet setForGroup = stForFindAllStudentsInGroup.executeQuery();
        ResultSet setForStudents;

        while (setForGroup.next()) {
            stForFindStudentById.setInt(1, setForGroup.getInt(1));

            setForStudents = stForFindStudentById.executeQuery();
            if (setForStudents.next()) {
                students.add(new Student()
                        .withId(setForStudents.getInt(1))
                        .withFirstName(setForStudents.getString(2))
                        .withLastName(setForStudents.getString(3))
                        .withPatronymic(setForStudents.getString(4))
                        .withDateOfBirth(setForStudents.getDate(5).toLocalDate())
                        .withCredentials(new Credentials()
                                .withId(setForStudents.getInt(7))
                                .withLogin(setForStudents.getString(8))
                                .withPassword(setForStudents.getString(9))));
            }

        }
        return students;
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
                log.info("Название группы обновлено");
                con.commit();
                return true;
            } else {
                log.error("Название не обновлено, обновления группы не произошло");
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
                    log.error("Учитель не обновлён, обновления группы не произошло");
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
    public boolean updateStudentsAdd(int id, Person newStudent) {
        Connection con = null;
        PreparedStatement stForFindStudent = null;
        PreparedStatement stForUpdate = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForFindStudent = con.prepareStatement(findPersonByName);
            stForUpdate = con.prepareStatement(putStudentAndGroupID);
            save = con.setSavepoint();

            stForFindStudent.setString(1, newStudent.getFirstName());
            stForFindStudent.setString(2, newStudent.getLastName());
            stForFindStudent.setString(3, newStudent.getPatronymic());

            int studentId = findStudentId(newStudent, stForFindStudent);

            if (studentId > 0) {
                stForUpdate.setInt(1, studentId);
                stForUpdate.setInt(2, id);

                if (stForUpdate.executeUpdate() > 0) {
                    log.info("Студент успешно добавлен к группе");
                    con.commit();
                    return true;
                } else {
                    log.error("Студент не добавлен к группе");
                    con.rollback(save);
                    return false;
                }
            } else {
                log.error("Студент не найден, добавления не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForFindStudent);
            closeResource(stForUpdate);
            closeResource(con);
        }
    }

    @Override
    public boolean updateStudentsRemove(int id, Person removableStudent) {
        Connection con = null;
        PreparedStatement stForFindStudent = null;
        PreparedStatement stForUpdate = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForFindStudent = con.prepareStatement(findPersonByName);
            stForUpdate = con.prepareStatement(deleteStudentFromGroupByID);
            save = con.setSavepoint();

            stForFindStudent.setString(1, removableStudent.getFirstName());
            stForFindStudent.setString(2, removableStudent.getLastName());
            stForFindStudent.setString(3, removableStudent.getPatronymic());

            int studentId = findStudentId(removableStudent, stForFindStudent);
            if (studentId > 0) {
                stForUpdate.setInt(1, studentId);
                if (stForUpdate.executeUpdate() > 0) {
                    log.info("Студент успешно удалён из группы");
                    con.commit();
                    return true;
                } else {
                    log.error("Студент не удалён из группы");
                    con.rollback(save);
                    return false;
                }
            } else {
                log.error("Студент не найден, удаления не произошло");
                con.rollback(save);
                return false;
            }

        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForFindStudent);
            closeResource(stForUpdate);
            closeResource(con);
        }
    }

    @Override
    public boolean updateSubjectsAdd(int id, Subject newSubject) {
        Connection con = null;
        PreparedStatement stForFindSubject = null;
        PreparedStatement stForUpdate = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForFindSubject = con.prepareStatement(findSubjectByName);
            stForUpdate = con.prepareStatement(putSubjectAndGroupID);
            save = con.setSavepoint();

            stForFindSubject.setString(1, newSubject.getName());

            int subjectId = findSubjectId(newSubject, stForFindSubject);

            if (subjectId > 0) {
                stForUpdate.setInt(1, subjectId);
                stForUpdate.setInt(2, id);

                if (stForUpdate.executeUpdate() > 0) {
                    log.info("Предмет успешно добавлен к группе");
                    con.commit();
                    return true;
                } else {
                    log.error("Предмет не добавлен к группе");
                    con.rollback(save);
                    return false;
                }
            } else {
                log.error("Предмет не найден, добавления не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForFindSubject);
            closeResource(stForUpdate);
            closeResource(con);
        }
    }

    @Override
    public boolean updateSubjectsRemove(int id, Subject removableSubject) {
        Connection con = null;
        PreparedStatement stForFindSubject = null;
        PreparedStatement stForUpdate = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForFindSubject = con.prepareStatement(findSubjectByName);
            stForUpdate = con.prepareStatement(deleteSubjectFromGroupByID);
            save = con.setSavepoint();

            stForFindSubject.setString(1, removableSubject.getName());

            int subjectId = findSubjectId(removableSubject, stForFindSubject);
            if (subjectId > 0) {
                stForUpdate.setInt(1, subjectId);
                if (stForUpdate.executeUpdate() > 0) {
                    log.info("Предмет успешно удалён из группы");
                    con.commit();
                    return true;
                } else {
                    log.error("Предмет не удалён из группы");
                    con.rollback(save);
                    return false;
                }
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
            closeResource(stForFindSubject);
            closeResource(stForUpdate);
            closeResource(con);
        }
    }

    @Override
    public boolean deleteGroupById(int id) {
        Connection con = null;
        PreparedStatement stForFindStudentWithThisGroup = null;
        PreparedStatement stForFindMarksWithThisGroup = null;
        PreparedStatement stForFindSubjectsWithThisGroup = null;
        PreparedStatement stForDeleteStudentsWithThisGroup = null;
        PreparedStatement stForDeleteMarksWithThisGroup = null;
        PreparedStatement stForDeleteSubjectsWithThisGroup = null;
        PreparedStatement stForDeleteGroup = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForFindStudentWithThisGroup = con.prepareStatement(findGroupWithStudentsByID);
            stForFindMarksWithThisGroup = con.prepareStatement(findMarksByGroupID);
            stForFindSubjectsWithThisGroup = con.prepareStatement(findSubjectsByGroupID);
            stForDeleteStudentsWithThisGroup = con.prepareStatement(deleteGroupWithStudentsByID);
            stForDeleteMarksWithThisGroup = con.prepareStatement(deleteMarksByGroupID);
            stForDeleteSubjectsWithThisGroup = con.prepareStatement(deleteGroupWithSubjectsByID);
            stForDeleteGroup = con.prepareStatement(deleteGroupById);
            save = con.setSavepoint();

            Optional<Group> optionalGroup = getGroupById(id);
            if (optionalGroup.isPresent()) {

                if (isGroupHasStudents(stForFindStudentWithThisGroup, id)) {
                    if (isStudentsHadDeleted(stForDeleteStudentsWithThisGroup, id)) {
                        log.info("Студенты удалены");
                        con.commit();
                    } else {
                        log.error("Студенты не удалены, удаление группы прервано");
                        con.rollback(save);
                        return false;
                    }
                }

                if (isGroupHasMarks(stForFindMarksWithThisGroup, id)) {
                    if (isMarksHadDeleted(stForDeleteMarksWithThisGroup, id)) {
                        log.info("Оценки удалены");
                        con.commit();
                    } else {
                        log.error("Оценки не удалены, удаление группы прервано");
                        con.rollback(save);
                        return false;
                    }
                }

                if (isGroupHasSubjects(stForFindSubjectsWithThisGroup, id)) {
                    if (isSubjectsHadDeleted(stForDeleteSubjectsWithThisGroup, id)) {
                        log.info("Предметы удалены");
                        con.commit();
                    } else {
                        log.error("Предметы не удалены, удаление группы прервано");
                        con.rollback(save);
                        return false;
                    }
                }

                if (isGroupHasDeleted(stForDeleteGroup, id)) {
                    log.info("Группа полностью удалена, удаление завершено");
                    con.commit();
                    return true;
                } else {
                    log.error("Группа не удалена, удаление прервано");
                    con.rollback(save);
                    return false;
                }

            } else {
                log.error("Группа не найдена, удаления не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForFindStudentWithThisGroup);
            closeResource(stForFindMarksWithThisGroup);
            closeResource(stForFindSubjectsWithThisGroup);
            closeResource(stForDeleteStudentsWithThisGroup);
            closeResource(stForDeleteMarksWithThisGroup);
            closeResource(stForDeleteSubjectsWithThisGroup);
            closeResource(stForDeleteGroup);
            closeResource(con);
        }
    }

    // метод удаления самой группы

    private boolean isGroupHasDeleted(PreparedStatement stForDeleteGroup, int groupId) throws SQLException {
        log.info("Попытка удалить группу");
        stForDeleteGroup.setInt(1, groupId);
        return stForDeleteGroup.executeUpdate() > 0;
    }

    // метод для удаления предметов из этой группы

    private boolean isSubjectsHadDeleted(PreparedStatement stForDeleteSubjects, int groupId) throws SQLException {
        log.info("Попытка удаления предметов из этой группы");
        stForDeleteSubjects.setInt(1, groupId);
        return stForDeleteSubjects.executeUpdate() > 0;
    }

    // метод для проверки на наличие предметов в этой группе

    private boolean isGroupHasSubjects(PreparedStatement stForFindSubjects, int groupId) throws SQLException {
        log.info("Проверка, есть ли в группе предметы");
        stForFindSubjects.setInt(1, groupId);
        return stForFindSubjects.executeQuery().next();
    }

    // метод для удаления оценок из этой группы

    private boolean isMarksHadDeleted(PreparedStatement stForDeleteMarks, int groupId) throws SQLException {
        log.info("Попытка удаления оценок из этой группы");
        stForDeleteMarks.setInt(1, groupId);
        return stForDeleteMarks.executeUpdate() > 0;
    }

    // метод для проверки выставления в этой группе оценок

    private boolean isGroupHasMarks(PreparedStatement stForFindMarks, int groupId) throws SQLException {
        log.info("Проверка, есть ли в группе оценки");
        stForFindMarks.setInt(1, groupId);
        return stForFindMarks.executeQuery().next();
    }

    // метод дял удаления студентов из этой группы

    private boolean isStudentsHadDeleted(PreparedStatement stForDeleteStudents, int groupId) throws SQLException {
        log.info("Попытка удаления студентов из этой группы");
        stForDeleteStudents.setInt(1, groupId);
        return stForDeleteStudents.executeUpdate() > 0;
    }

    // метод для проверки есть ли в этой группе студенты

    private boolean isGroupHasStudents(PreparedStatement stForFindStudents, int groupId) throws SQLException {
        log.info("Проверка, есть ли у группы студенты");
        stForFindStudents.setInt(1, groupId);
        return stForFindStudents.executeQuery().next();
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
