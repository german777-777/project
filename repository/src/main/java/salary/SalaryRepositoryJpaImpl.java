package salary;

import helper.EntityManagerHelper;
import jdk.jfr.Percentage;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import secondary.Salary;
import users.Person;
import users.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SalaryRepositoryJpaImpl implements SalaryRepository {

    private final SessionFactory factory;
    private static volatile SalaryRepositoryJpaImpl instance;

    private SalaryRepositoryJpaImpl(SessionFactory factory) {
        this.factory = factory;
    }
    public static SalaryRepositoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (SalaryRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new SalaryRepositoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }

    @Override
    public Salary createSalary(Salary salary) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            manager.persist(salary);
            if (manager.contains(salary)) {
                log.info("Добавлена новая зарплата");
                transaction.commit();
                return salary;
            } else {
                log.error("Не добавлена новая зарплата");
                transaction.rollback();
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка добавления зарплаты: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Salary> getSalaryByID(int salaryID) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Salary> result = Optional.empty();
        try {
            transaction.begin();
            Salary salaryFromQuery = manager
                    .createNamedQuery("getSalaryByID", Salary.class)
                    .setParameter("id", salaryID)
                    .getSingleResult();
            result = Optional.ofNullable(salaryFromQuery);
            if (result.isPresent()) {
                log.info("Зарплата найдена по ID");
            } else {
                log.error("Зарплата не найдена по ID");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения зарплаты по ID: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<Salary> getSalariesByTeacherId(int teacherId) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        List<Salary> result = new ArrayList<>();
        try {
            transaction.begin();
            TypedQuery<Salary> query = manager
                    .createNamedQuery("getSalariesByTeacherID", Salary.class)
                    .setParameter("teacherID", teacherId);
            result = query.getResultList();
            if (!result.isEmpty()) {
                log.info("Зарплаты найдены по ID учителя");
            } else {
                log.error("Зарплаты не найдены по ID учителя");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения зарплат по ID учителя: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<Salary> getSalariesByDateOfSalary(LocalDate dateOfSalary) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        List<Salary> result = new ArrayList<>();
        try {
            transaction.begin();
            TypedQuery<Salary> query = manager
                    .createNamedQuery("getSalariesByDate", Salary.class)
                    .setParameter("dateOfSalary", dateOfSalary);
            result = query.getResultList();
            if (!result.isEmpty()) {
                log.info("Зарплаты найдены по дате");
            } else {
                log.error("Зарплаты не найдены по дате");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения зарплат по дате: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<Salary> getAllSalaries() {
        List<Salary> salaries = new ArrayList<>();
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            TypedQuery<Salary> query = manager.createQuery("from Salary ", Salary.class);
            salaries = query.getResultList();
            if (!salaries.isEmpty()) {
                log.info("Все зарплаты найдены");
            } else {
                log.error("Зарплаты не найдены");
            }
            transaction.commit();
            return salaries;
        } catch (Exception e) {
            log.error("Ошибка нахождения зарплат: " + e.getMessage());
            return salaries;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateSalaryById(int id, int newSalary) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Salary result;
        try {
            transaction.begin();
            TypedQuery<Salary> query = manager
                    .createNamedQuery("getSalaryByID", Salary.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setSalary(newSalary);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Сумма зарплаты обновлена по ID зарплаты");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Сумма зарплаты не обновлена по ID зарплаты");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Зарплата не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления суммы зарплаты по ID зарплаты: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateTeacherReceivedSalaryById(int id, Teacher teacher) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Salary result;
        try {
            transaction.begin();
            TypedQuery<Salary> query = manager
                    .createNamedQuery("getSalaryByID", Salary.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setTeacher(teacher);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Учитель, получивший зарплату, обновлён по ID зарплаты");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Учитель, получивший зарплату, не обновлён по ID зарплаты");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Зарплата не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления учителя, получившего зарплату, по ID зарплаты: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateDateOfSalaryById(int id, LocalDate newDateOfSalary) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Salary result;
        try {
            transaction.begin();
            TypedQuery<Salary> query = manager
                    .createNamedQuery("getSalaryByID", Salary.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setDateOfSalary(newDateOfSalary);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Дата получения зарплаты обновлена по ID зарплаты");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Дата получения зарплаты не обновлена по ID зарплаты");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Зарплата не найдена по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления даты получения зарплаты по ID зарплаты: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deleteSalaryById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            Salary salaryForDelete = manager
                    .createNamedQuery("getSalaryByID", Salary.class)
                    .setParameter("id", id)
                    .getSingleResult();
            manager.remove(salaryForDelete);
            result = !manager.contains(salaryForDelete);
            if (result) {
                log.info("Зарплата удалена по ID");
                transaction.commit();
            } else {
                log.error("Зарплата не удалена по ID");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления зарплаты по ID: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }
}
