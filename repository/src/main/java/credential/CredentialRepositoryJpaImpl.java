package credential;

import credentials.Credentials;
import helper.EntityManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CredentialRepositoryJpaImpl implements CredentialRepository {

    private final SessionFactory factory;
    private static volatile CredentialRepositoryJpaImpl instance;

    private CredentialRepositoryJpaImpl(SessionFactory factory) {
        this.factory = factory;
    }

    public static CredentialRepositoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (CredentialRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new CredentialRepositoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }

    @Override
    public Credentials createCredential(Credentials credentials) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            manager.persist(credentials);
            if (manager.contains(credentials)) {
                log.info("Добавлены новые учётные данные");
                transaction.commit();
                return credentials;
            } else {
                log.error("Не добавлены новые учётные данные");
                transaction.rollback();
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка добавления учётных данных: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Credentials> getCredentialById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Credentials> result = Optional.empty();
        try {
            transaction.begin();
            Credentials credentialsFromQuery = manager
                    .createNamedQuery("getCredentialsByID", Credentials.class)
                    .setParameter("id", id)
                    .getSingleResult();
            result = Optional.ofNullable(credentialsFromQuery);
            if (result.isPresent()) {
                log.info("Учётные данные найдены по ID");
            } else {
                log.error("Учётные данные не найдены по ID");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения учётных данных по ID: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public Optional<Credentials> getCredentialByLoginAndPassword(String login, String password) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Optional<Credentials> result = Optional.empty();
        try {
            transaction.begin();
            Credentials credentialsFromQuery = manager
                    .createNamedQuery("getCredentialsByLoginAndPassword", Credentials.class)
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .getSingleResult();
            result = Optional.ofNullable(credentialsFromQuery);
            if (result.isPresent()) {
                log.info("Учётные данные найдены по значениям логина и пароля");
            } else {
                log.error("Учётные данные не найдены по значениям логина и пароля");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения учётных данных по значениям логина и пароля: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<Credentials> getAllCredentials() {
        List<Credentials> credentials = new ArrayList<>();
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        try {
            transaction.begin();
            TypedQuery<Credentials> query = manager.createQuery("from Credentials ", Credentials.class);
            credentials = query.getResultList();
            if (!credentials.isEmpty()) {
                log.info("Все учётные данные найдены");
            } else {
                log.error("Учётные данные не найдены");
            }
            transaction.commit();
            return credentials;
        } catch (Exception e) {
            log.error("Ошибка нахождения учётных данных: " + e.getMessage());
            return credentials;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean updateCredentialById(int id, String newLogin, String newPassword) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        Credentials result;
        try {
            transaction.begin();
            TypedQuery<Credentials> query = manager
                    .createNamedQuery("getCredentialsByID", Credentials.class)
                    .setParameter("id", id);
            result = query.getSingleResult();
            if (result != null) {
                result.setLogin(newLogin);
                result.setPassword(newPassword);
                manager.merge(result);
                if (manager.contains(result)) {
                    log.info("Учётные данные обновлены по ID");
                    transaction.commit();
                    return true;
                } else {
                    log.error("Учётные данные не обновлены по ID");
                    transaction.rollback();
                    return false;
                }
            } else {
                log.error("Учётные данные не найдены по ID");
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка обновления учётных данных по ID: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deleteCredentialById(int id) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            result = manager.createNamedQuery("deleteCredentialsByID", Credentials.class)
                    .setParameter("id", id)
                    .executeUpdate() != 0;
            if (result) {
                log.info("Учётные данные удалены по ID");
                transaction.commit();
            } else {
                log.error("Учётные данные не удалены по ID");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления учётных данных по ID: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public boolean deleteCredentialByLoginAndPassword(String login, String password) {
        EntityManager manager = EntityManagerHelper.getInstance().getEntityManager(factory);
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            result = manager.createNamedQuery("deleteCredentialsByLoginAndPassword", Credentials.class)
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .executeUpdate() != 0;
            if (result) {
                log.info("Учётные данные удалены по логину и паролю");
                transaction.commit();
            } else {
                log.error("Учётные данные не удалены по логину и паролю");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления учётных данных по логину и паролю: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return result;
        } finally {
            manager.close();
        }
    }
}
