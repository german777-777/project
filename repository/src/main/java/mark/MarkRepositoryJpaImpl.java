package mark;

import helper.EntityManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import secondary.Group;
import secondary.Mark;
import secondary.Subject;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MarkRepositoryJpaImpl implements MarkRepository {

    private final SessionFactory factory;
    private static volatile MarkRepositoryJpaImpl instance;

    private MarkRepositoryJpaImpl(SessionFactory factory) {
        this.factory = factory;
    }
    public static MarkRepositoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (MarkRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new MarkRepositoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }

    @Override
    public Mark createMark(Mark mark) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            mark.getStudent().addMark(mark);
            manager.persist(mark);
            if (manager.contains(mark)) {
                log.info("Добавлена новая оценка");
                transaction.commit();
                return mark;
            } else {
                log.error("Не добавлена новая оценка");
                transaction.rollback();
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка добавления оценки: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Mark> getMarkByID(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Mark> result = Optional.empty();
        try {
            transaction.begin();
            Mark markFromQuery = manager
                    .createNamedQuery("getMarkByID", Mark.class)
                    .setParameter("id", id)
                    .getSingleResult();
            result = Optional.ofNullable(markFromQuery);
            if (result .isPresent()) {
                log.info("Оценка найдена по ID");
            } else {
                log.error("Не найдена оценка по ID");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения оценки по ID: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<Mark> getAllMarks() {
        List<Mark> marks = new ArrayList<>();
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            TypedQuery<Mark> query = manager.createQuery("from Mark ", Mark.class);
            marks = query.getResultList();
            if (!marks.isEmpty()) {
                log.info("Все оценки найдены");
            } else {
                log.error("Оценки не найдены");
            }
            transaction.commit();
            return marks;
        } catch (Exception e) {
            log.error("Ошибка нахождения оценок: " + e.getMessage());
            return marks;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateSubjectMarkById(int id, Subject newSubject) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Mark result;
        try {
            transaction.begin();
            TypedQuery<Mark> query = manager
                    .createNamedQuery("getMarkByID", Mark.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setSubject(newSubject);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Предмет оценки обновлён по ID оценки");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Предмет оценки не обновлён по ID оценки");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Оценка не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления предмета оценки по ID оценки: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateDateOfMarkById(int id, LocalDate newDateOfMark) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Mark result;
        try {
            transaction.begin();
            TypedQuery<Mark> query = manager
                    .createNamedQuery("getMarkByID", Mark.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setDateOfMark(newDateOfMark);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Дата выставления оценки обновлена по ID оценки");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Дата выставления оценки не обновлена по ID оценки");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Оценка не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления даты выставления оценки по ID оценки: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateGroupWhereMarkWasGiven(int id, Group newGroup) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Mark result;
        try {
            transaction.begin();
            TypedQuery<Mark> query = manager
                    .createNamedQuery("getMarkByID", Mark.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setGroup(newGroup);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Группа оценки обновлена по ID оценки");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Группа оценки не обновлена по ID оценки");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Оценка не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления группы оценки по ID оценки: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateMarkById(int id, int newMark) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Mark result;
        try {
            transaction.begin();
            TypedQuery<Mark> query = manager
                    .createNamedQuery("getMarkByID", Mark.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setMark(newMark);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Значение оценки обновлено по ID оценки");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Значение оценки не обновлено по ID оценки");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Оценка не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления значения оценки по ID оценки: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deleteMarkById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            Mark markForDelete = manager
                    .createNamedQuery("getMarkByID", Mark.class)
                    .setParameter("id", id)
                    .getSingleResult();
            manager.remove(markForDelete);
            result = !manager.contains(markForDelete);
            if (result) {
                log.info("Оценка удалена по ID");
                transaction.commit();
            } else {
                log.error("Оценка не удалена по ID");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления оценки по ID: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }
}
