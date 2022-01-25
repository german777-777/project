package mark;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import group.GroupRepositoryPostgresImpl;
import lombok.extern.slf4j.Slf4j;
import secondary.Group;
import secondary.Mark;
import secondary.Subject;
import users.Person;
import users.Student;
import users.Teacher;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

            st.setInt(1, mark.getStudent().getId());
            st.setInt(2, mark.getGroup().getId());
            st.setInt(3, mark.getSubject().getId());
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
        log.debug("Попытка найти оценку по ID");
        ResultSet setForMark = null;
        ResultSet setForStudent = null;
        ResultSet setForGroup = null;
        ResultSet setForSubject = null;
        try (Connection con = pool.getConnection();
             PreparedStatement stForFindMark = con.prepareStatement(findMarkByID);
             PreparedStatement stForFindStudentToMark = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectToMark = con.prepareStatement(findSubjectByID);

             PreparedStatement stForFindStudentToGroup = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectToGroup = con.prepareStatement(findSubjectByID);

             PreparedStatement stForFindGroup = con.prepareStatement(findGroupByID);

             PreparedStatement stForFindAllStudents = con.prepareStatement(findGroupWithStudentsByID);
             PreparedStatement stForFindAllSubjects = con.prepareStatement(findSubjectsByGroupID))
        {
            stForFindMark.setInt(1, id);
            setForMark = stForFindMark.executeQuery();
            if (setForMark.next()) {
                log.info("Оценка найдена");
                stForFindStudentToMark.setInt(1, setForMark.getInt(2));
                setForStudent = stForFindStudentToMark.executeQuery();
                if (setForStudent.next()) {
                    log.info("Студент найден (для оценки)");
                    stForFindGroup.setInt(1, setForMark.getInt(3));
                    setForGroup = stForFindGroup.executeQuery();
                    if (setForGroup.next()) {
                        log.info("Группа найдена (для оценки)");
                        stForFindSubjectToMark.setInt(1, setForMark.getInt(4));
                        setForSubject = stForFindSubjectToMark.executeQuery();
                        if (setForSubject.next()) {
                            log.info("Предмет найден (для оценки)");
                            return Optional.of(new Mark()
                                    .withId(setForMark.getInt(1))
                                    .withStudent(createStudentFromSet(setForStudent))
                                    .withGroup(createGroupFromSet(setForGroup, stForFindAllStudents, stForFindAllSubjects, stForFindStudentToGroup, stForFindSubjectToGroup))
                                    .withSubject(createSubjectFromSet(setForSubject))
                                    .withDateOfMark(setForMark.getDate(5).toLocalDate())
                                    .withMark(setForMark.getInt(6)));
                        } else {
                            log.error("Не найден предмет, по которому выставлялась оценка");
                            return Optional.empty();
                        }
                    } else {
                        log.error("Не найдена группа, в которой выставлялась оценка");
                        return Optional.empty();
                    }
                } else {
                    log.error("Не найден студент, которому принадлежит оценка");
                    return Optional.empty();
                }
            } else {
                log.error("Оценка не найдена");
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return Optional.empty();
        } finally {
            closeResource(setForMark);
            closeResource(setForMark);
            closeResource(setForStudent);
            closeResource(setForGroup);
            closeResource(setForSubject);
        }
    }

    @Override
    public List<Mark> getAllMarks() {
        log.debug("Попытка получения всех оценок");
        List<Mark> marks = new ArrayList<>();
        ResultSet setForMark = null;
        ResultSet setForStudent = null;
        ResultSet setForGroup = null;
        ResultSet setForSubject = null;

        try (Connection con = pool.getConnection();
             PreparedStatement stForAllMarks = con.prepareStatement(findAllMarks);

             PreparedStatement stForFindStudentToMark = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectToMark = con.prepareStatement(findSubjectByID);

             PreparedStatement stForFindStudentToGroup = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectToGroup = con.prepareStatement(findSubjectByID);

             PreparedStatement stForFindGroup = con.prepareStatement(findGroupByID);

             PreparedStatement stForFindAllStudents = con.prepareStatement(findGroupWithStudentsByID);
             PreparedStatement stForFindAllSubjects = con.prepareStatement(findSubjectsByGroupID))
        {
            setForMark = stForAllMarks.executeQuery();
            while (setForMark.next()) {
                log.info("Оценка найдена");
                stForFindStudentToMark.setInt(1, setForMark.getInt(2));
                setForStudent = stForFindStudentToMark.executeQuery();
                if (setForStudent.next()) {
                    log.info("Студент найден (для оценки)");
                    stForFindGroup.setInt(1, setForMark.getInt(3));
                    setForGroup = stForFindGroup.executeQuery();
                    if (setForGroup.next()) {
                        log.info("Группа найдена (для оценки)");
                        stForFindSubjectToMark.setInt(1, setForMark.getInt(4));
                        setForSubject = stForFindSubjectToMark.executeQuery();
                        if (setForSubject.next()) {
                            log.info("Предмет найден (для оценки)");
                            marks.add(new Mark()
                                    .withId(setForMark.getInt(1))
                                    .withStudent(createStudentFromSet(setForStudent))
                                    .withGroup(createGroupFromSet(setForGroup, stForFindAllStudents, stForFindAllSubjects, stForFindStudentToGroup, stForFindSubjectToGroup))
                                    .withSubject(createSubjectFromSet(setForSubject))
                                    .withDateOfMark(setForMark.getDate(5).toLocalDate())
                                    .withMark(setForMark.getInt(6)));
                        } else {
                            log.error("Не найден предмет, по которому выставлялась оценка");
                        }
                    } else {
                        log.error("Не найдена группа, в которой выставлялась оценка");
                    }
                } else {
                    log.error("Не найден студент, которому принадлежит оценка");
                }
            }
            return marks;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return marks;
        } finally {
            closeResource(setForMark);
            closeResource(setForStudent);
            closeResource(setForGroup);
            closeResource(setForSubject);
        }
    }

    @Override
    public boolean updateSubjectMarkById(int id, Subject newSubject) {
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, обновления не произошло (не обновлён предмет)");
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
                    log.info("Оценка обновлена (обновлён предмет)");
                    con.commit();
                    return true;
                } else {
                    log.error("Оценка не обновлена (не обновлён предмет)");
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
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, обновления не произошло (не обновлена дата оценки)");
            return false;
        } else {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateDateOfMarkByID);
                save = con.setSavepoint();

                st.setDate(1, Date.valueOf(newDateOfMark));
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Оценка обновлена (обновлена дата оценки)");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка обновления (не обновлена дата оценки)");
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
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, обновления не произошло (не обновлена группа)");
            return false;
        } else {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(updateGroupOfMarkByID);
                save = con.setSavepoint();

                st.setInt(1, newGroup.getId());
                st.setInt(2, id);
                if (st.executeUpdate() > 0) {
                    log.info("Оценка обновлена (обновлена группа)");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка обновления (не обновлена группа)");
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
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, обновления не произошло (сама оценка не обновлена)");
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
                    log.info("Оценка обновлена (сама оценка обновлена)");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка обновления (сама оценка не обновлена)");
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
        Optional<Mark> optionalMark = getMarkByID(id);
        if (optionalMark.isEmpty()) {
            log.error("Оценка не найдена, удаления не произошло");
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

    private Student createStudentFromSet(ResultSet setForStudent) throws SQLException {
        return new Student()
                .withId(setForStudent.getInt(1))
                .withFirstName(setForStudent.getString(2))
                .withLastName(setForStudent.getString(3))
                .withPatronymic(setForStudent.getString(4))
                .withDateOfBirth(setForStudent.getDate(5).toLocalDate())
                .withCredentials(new Credentials()
                        .withId(setForStudent.getInt(7))
                        .withLogin(setForStudent.getString(8))
                        .withPassword(setForStudent.getString(9)));
    }

    private Group createGroupFromSet(ResultSet setForGroup,
                                     PreparedStatement stForFindAllStudents,
                                     PreparedStatement stForFindAllSubjects,
                                     PreparedStatement stForFindStudentToGroup,
                                     PreparedStatement stForFindSubjectToGroup) throws SQLException {
        return new Group()
                .withId(setForGroup.getInt(1))
                .withName(setForGroup.getString(2))
                .withTeacher(new Teacher()
                        .withId(setForGroup.getInt(3))
                        .withFirstName(setForGroup.getString(4))
                        .withLastName(setForGroup.getString(5))
                        .withPatronymic(setForGroup.getString(6))
                        .withDateOfBirth(setForGroup.getDate(7).toLocalDate())
                        .withCredentials(new Credentials()
                                .withId(setForGroup.getInt(8))
                                .withLogin(setForGroup.getString(9))
                                .withPassword(setForGroup.getString(10))))
                .withStudents(getAllStudents(stForFindAllStudents, setForGroup.getInt(1), stForFindStudentToGroup))
                .withSubjects(getAllSubjects(stForFindAllSubjects, setForGroup.getInt(1), stForFindSubjectToGroup));
    }

    private Subject createSubjectFromSet(ResultSet setForSubject) throws SQLException {
        return new Subject()
                .withId(setForSubject.getInt(1))
                .withName(setForSubject.getString(2));
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
