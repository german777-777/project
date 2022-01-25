package group;

import helper.EntityManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import secondary.Group;
import secondary.Subject;
import users.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class GroupRepositoryJpaImpl implements GroupRepository {

    private final SessionFactory factory;
    private static volatile GroupRepositoryJpaImpl instance;

    private GroupRepositoryJpaImpl(SessionFactory factory) {
        this.factory = factory;
    }

    public static GroupRepositoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (GroupRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new GroupRepositoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }


    @Override
    public Group createGroup(Group group) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            manager.persist(group);
            if (manager.contains(group)) {
                log.info("Добавлена новая группа");
                transaction.commit();
                return group;
            } else {
                log.error("Не добавлена новая группа");
                transaction.rollback();
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка добавления группы: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Group> getGroupById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Group> result = Optional.empty();
        try {
            transaction.begin();
            Group groupFromQuery = manager
                    .createNamedQuery("getGroupByID", Group.class)
                    .setParameter("id", id)
                    .getSingleResult();
            result = Optional.ofNullable(groupFromQuery);
            if (result.isPresent()) {
                log.info("Группа найдена по ID");
            } else {
                log.error("Не найдена группа по ID");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения группы по ID: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Group> getGroupByName(String name) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Group> result = Optional.empty();
        try {
            transaction.begin();
            Group groupFromQuery = manager
                    .createNamedQuery("getGroupByName", Group.class)
                    .setParameter("name", name)
                    .getSingleResult();
            result = Optional.ofNullable(groupFromQuery);
            if (result.isPresent()) {
                log.info("Группа найдена по названию");
            } else {
                log.error("Не найдена группа по названию");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения группы по названию: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            TypedQuery<Group> query = manager.createQuery("from Group ", Group.class);
            groups = query.getResultList();
            if (!groups.isEmpty()) {
                log.info("Все группы найдены");
            } else {
                log.error("Группы не найдены");
            }
            transaction.commit();
            return groups;
        } catch (Exception e) {
            log.error("Ошибка нахождения групп: " + e.getMessage());
            return groups;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateGroupNameById(int id, String newName) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Group result;
        try {
            transaction.begin();
            TypedQuery<Group> query = manager
                    .createNamedQuery("getGroupByID", Group.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setName(newName);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Название группы обновлено по ID группы");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Название группы не обновлено по ID группы");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Группа не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления названия группы по ID группы: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateGroupTeacherById(int id, Person newTeacher) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Group result;
        try {
            transaction.begin();
            TypedQuery<Group> query = manager
                    .createNamedQuery("getGroupByID", Group.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setTeacher(newTeacher);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Учитель группы обновлён по ID группы");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Учитель группы не обновлён по ID группы");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Группа не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления учителя группы по ID группы: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateStudentsAdd(int id, Person newStudent) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Group result;
        try {
            transaction.begin();
            TypedQuery<Group> query = manager
                    .createNamedQuery("getGroupByID", Group.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.addStudent(newStudent);
                manager.merge(result);
                if (manager.contains(result) && result.getStudents().contains(newStudent)) {
                    log.info("Список студентов обновлён по ID группы");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Список студентов не обновлён по ID группы");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Группа не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления списка студентов по ID группы: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateStudentsRemove(int id, Person removableStudent) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Group result;
        try {
            transaction.begin();
            TypedQuery<Group> query = manager
                    .createNamedQuery("getGroupByID", Group.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.removeStudent(removableStudent);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Список студентов обновлён по ID группы");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Список студентов не обновлён по ID группы");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Группа не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления списка студентов по ID группы: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateSubjectsAdd(int id, Subject newSubject) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Group result;
        try {
            transaction.begin();
            TypedQuery<Group> query = manager
                    .createNamedQuery("getGroupByID", Group.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.addSubject(newSubject);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Список предметов обновлён по ID группы, предмет добавлен");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Список предметов не обновлён по ID группы, предмет не добавлен");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Группа не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления списка предметов по ID группы: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateSubjectsRemove(int id, Subject removableSubject) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Group result;
        try {
            transaction.begin();
            TypedQuery<Group> query = manager
                    .createNamedQuery("getGroupByID", Group.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.removeSubject(removableSubject);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Список предметов обновлён по ID группы, предмет удалён");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Список предметов не обновлён по ID группы, предмет не удалён");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Группа не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления списка предметов по ID группы: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deleteGroupById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            Group groupForDelete = manager
                    .createNamedQuery("getGroupByID", Group.class)
                    .setParameter("id", id)
                    .getSingleResult();
            manager.remove(groupForDelete);
            result = !manager.contains(groupForDelete);
            if (result) {
                log.info("Группа удалена по ID");
                transaction.commit();
            } else {
                log.error("Группа не удалена по ID");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления группы по ID: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }
}
