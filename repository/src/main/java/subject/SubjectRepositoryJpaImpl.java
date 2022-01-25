package subject;

import helper.EntityManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import secondary.Subject;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SubjectRepositoryJpaImpl implements SubjectRepository {

    private final SessionFactory factory;
    private static volatile SubjectRepositoryJpaImpl instance;

    private SubjectRepositoryJpaImpl(SessionFactory factory) {
        this.factory = factory;
    }
    public static SubjectRepositoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (SubjectRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new SubjectRepositoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }

    @Override
    public Subject createSubject(Subject subject) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            manager.persist(subject);
            if (manager.contains(subject)) {
                log.info("Добавлен новый предмет");
                transaction.commit();
                return subject;
            } else {
                log.error("Не добавлен новый предмет");
                transaction.rollback();
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка добавления предмета: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Subject> getSubjectById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Subject> result = Optional.empty();
        try {
            transaction.begin();
            Subject subjectFromQuery = manager
                    .createNamedQuery("getSubjectByID", Subject.class)
                    .setParameter("id", id)
                    .getSingleResult();
            result = Optional.ofNullable(subjectFromQuery);
            if (result.isPresent()) {
                log.info("Предмет найден по ID");
            } else {
                log.error("Предмет не найден по ID");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения предмета по ID: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Subject> getSubjectByName(String name) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Subject> result = Optional.empty();
        try {
            Subject subjectFromQuery = manager
                    .createNamedQuery("getSubjectByName", Subject.class)
                    .setParameter("name", name)
                    .getSingleResult();
            result = Optional.ofNullable(subjectFromQuery);
            if (result.isPresent()) {
                log.info("Предмет найден по названию");
            } else {
                log.error("Предмет не найден по названию");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения предмета по названию: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            TypedQuery<Subject> query = manager.createQuery("from Subject ", Subject.class);
            subjects = query.getResultList();
            if (!subjects.isEmpty()) {
                log.info("Все предметы найдены");
            } else {
                log.error("Предметы не найдены, не существует ни одного предмета");
            }
            transaction.commit();
            return subjects;
        } catch (Exception e) {
            log.error("Ошибка нахождения предметов: " + e.getMessage());
            return subjects;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateSubjectNameById(int id, String newName) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Subject result;
        try {
            transaction.begin();
            TypedQuery<Subject> query = manager
                    .createNamedQuery("getSubjectByID", Subject.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setName(newName);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Название предмета обновлено по ID предмета");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Название предмета не обновлено по ID предмета");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Предмет не найден по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления названия предмета по ID предмета: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateSubjectNameByName(String oldName, String newName) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Subject result;
        try {
            transaction.begin();
            TypedQuery<Subject> query = manager
                    .createNamedQuery("getSubjectByName", Subject.class)
                    .setParameter("name", oldName);
            result = query.getSingleResult();
            if (result != null) {
                result.setName(newName);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Название предмета обновлено по старому названию предмета");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Название предмета не обновлено по старому названию предмета");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Предмет не найден по названию");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления названия предмета по старому названию предмета: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deleteSubjectById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            Subject subjectForDelete = manager
                    .createNamedQuery("getSubjectByID", Subject.class)
                    .setParameter("id", id)
                    .getSingleResult();
            manager.remove(subjectForDelete);
            result = !manager.contains(subjectForDelete);
            if (result) {
                log.info("Предмет удалён по ID");
                transaction.commit();
            } else {
                log.error("Предмет не удалён по ID");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления предмета по ID: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deleteSubjectByName(String name) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            Subject subjectForDelete = manager
                    .createNamedQuery("getSubjectByName", Subject.class)
                    .setParameter("name", name)
                    .getSingleResult();
            manager.remove(subjectForDelete);
            result = !manager.contains(subjectForDelete);
            if (result) {
                log.info("Предмет удалён по названию");
                transaction.commit();
            } else {
                log.error("Предмет не удалён по названию");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления предмета по названию: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }
}
