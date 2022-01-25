package person;

import credentials.Credentials;
import helper.EntityManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import users.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PersonRepositoryJpaImpl implements PersonRepository {

    private final SessionFactory factory;
    private static volatile PersonRepositoryJpaImpl instance;

    private PersonRepositoryJpaImpl(SessionFactory factory) {
        this.factory = factory;
    }
    public static PersonRepositoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (PersonRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new PersonRepositoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }

    @Override
    public Person createPerson(Person person) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            manager.persist(person);
            if (manager.contains(person)) {
                log.info("Добавлен новый пользователь");
                transaction.commit();
                return person;
            } else {
                log.error("Не добавлен новый пользователь");
                transaction.rollback();
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка добавления пользователя: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Person> result = Optional.empty();
        try {
            transaction.begin();
            Person personFromQuery = manager
                    .createNamedQuery("getPersonByID", Person.class)
                    .setParameter("id", id)
                    .getSingleResult();
            result = Optional.ofNullable(personFromQuery);
            if (result.isPresent()) {
                log.info("Пользователь найден по ID");
            } else {
                log.error("Пользователь не найден по ID");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения пользователя по ID: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Person> getPersonByName(String firstName, String lastName, String patronymic) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Person> result = Optional.empty();
        try {
            transaction.begin();
            Person personFromQuery = manager
                    .createNamedQuery("getPersonByNames", Person.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .setParameter("patronymic", patronymic)
                    .getSingleResult();
            result = Optional.ofNullable(personFromQuery);
            if (result.isPresent()) {
                log.info("Пользователь найден по ФИО");
            } else {
                log.error("Пользователь не найден по ФИО");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения пользователя по ФИО: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Person> getPersonByCredentials(String login, String password) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Person> result = Optional.empty();
        try {
            transaction.begin();
            Person personFromQuery = manager
                    .createNamedQuery("getPersonByCredentials", Person.class)
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .getSingleResult();
            result = Optional.ofNullable(personFromQuery);
            if (result.isPresent()) {
                log.info("Пользователь найден по логину и паролю");
            } else {
                log.error("Пользователь не найден по логину и паролю");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения пользователя по логину и паролю: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            TypedQuery<Person> query = manager.createQuery("from Person ", Person.class);
            persons = query.getResultList();
            if (!persons.isEmpty()) {
                log.info("Все пользователи найдены");
            } else {
                log.error("Пользователи не найдены");
            }
            transaction.commit();
            return persons;
        } catch (Exception e) {
            log.error("Ошибка нахождения пользователей: " + e.getMessage());
            return persons;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Person result;
        try {
            transaction.begin();
            TypedQuery<Person> query = manager
                    .createNamedQuery("getPersonByID", Person.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setFirstName(newFirstName);
                result.setLastName(newLastName);
                result.setPatronymic(newPatronymic);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("ФИО пользователя обновлено по ID пользователя");
                    transaction.commit();
                    return true;
                } else {
                    log.error("ФИО пользователя не обновлено по ID пользователя");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Пользователь не найден по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления ФИО пользователя по ID пользователя: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateDateOfBirthById(int id, LocalDate newDateOfBirth) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Person result;
        try {
            transaction.begin();
            TypedQuery<Person> query = manager
                    .createNamedQuery("getPersonByID", Person.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setDateOfBirth(newDateOfBirth);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Дата рождения пользователя обновлена по ID пользователя");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Дата рождения пользователя не обновлена по ID пользователя");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Пользователь не найден по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления даты рождения пользователя по ID пользователя: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateCredentialByPersonId(int id, Credentials newCredential) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Person result;
        try {
            transaction.begin();
            TypedQuery<Person> query = manager
                    .createNamedQuery("getPersonByID", Person.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setCredentials(newCredential);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Учётные данные пользователя обновлены по ID пользователя");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Учётные данные пользователя не обновлены по ID пользователя");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Пользователь не найден по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления учётных данных пользователя по ID пользователя: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deletePersonById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            Person personForDelete = manager
                    .createNamedQuery("getPersonByID", Person.class)
                    .setParameter("id", id)
                    .getSingleResult();
            manager.remove(personForDelete);
            result = !manager.contains(personForDelete);
            if (result) {
                log.info("Пользователь удалён по ID");
                transaction.commit();
            } else {
                log.error("Пользователь не удалён по ID");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления пользователя по ID: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deletePersonByName(String firstName, String lastName, String patronymic) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            Person personForDelete = manager
                    .createNamedQuery("getPersonByNames", Person.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .setParameter("patronymic", patronymic)
                    .getSingleResult();
            manager.remove(personForDelete);
            result = !manager.contains(personForDelete);
            if (result) {
                log.info("Пользователь удалён по ФИО");
                transaction.commit();
            } else {
                log.error("Пользователь не удалён по ФИО");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления пользователя по ФИО: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }
}
