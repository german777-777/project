package credential;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static constants.Queries.putCredentials;
import static constants.Queries.findCredentialsByID;
import static constants.Queries.findCredentialsByLoginAndPassword;
import static constants.Queries.findAllCredentials;
import static constants.Queries.updateCredentials;
import static constants.Queries.deleteCredentialsByID;
import static constants.Queries.deleteCredentialsByLoginAndPassword;

@Slf4j
public class CredentialRepositoryPostgresImpl implements CredentialRepository {
    private static volatile CredentialRepositoryPostgresImpl instance;
    private final ComboPooledDataSource pool;

    private CredentialRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    public static CredentialRepositoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (CredentialRepositoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new CredentialRepositoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

    @Override
    public Credentials createCredential(Credentials credentials) {
        log.debug("Попытка найти в репозитории учётные данные");
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForInsert = connection.prepareStatement(putCredentials)) {
            Optional<Credentials> optionalCredential = getCredentialByLoginAndPassword(credentials.getLogin(), credentials.getPassword());
            if (optionalCredential.isEmpty()) {
                log.info("Таких учётных данных не существует, вносим в таблицу");
                statementForInsert.setString(1, credentials.getLogin());
                statementForInsert.setString(2, credentials.getPassword());
                if (statementForInsert.executeUpdate() > 0) {
                    log.info("Добавлены новые учётные данные");
                    return credentials;
                } else {
                    log.error("Ошибка добавления: SQLException");
                    return null;
                }
            } else {
                log.error("Ошибка добавления: такие учётные данные уже существуют");
                return null;
            }
        } catch (SQLException e) {
            log.error("Ошибка добавления: SQLException");
        }
        return null;
    }

    @Override
    public Optional<Credentials> getCredentialById(int id) {
        log.debug("Попытка взять учётные данные по ID");
        ResultSet set = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findCredentialsByID)) {
            statementForFind.setInt(1, id);
            set = statementForFind.executeQuery();
            if (set.next()) {
                log.info("Берём учётные данные");
                return Optional.of(new Credentials()
                        .withId(set.getInt(1))
                        .withLogin(set.getString(2))
                        .withPassword(set.getString(3)));
            } else {
                log.error("Учётные данные не найдены");
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
    public Optional<Credentials> getCredentialByLoginAndPassword(String login, String password) {
        log.debug("Попытка взять учётные данные по логину и паролю");
        ResultSet set = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findCredentialsByLoginAndPassword)) {
            statementForFind.setString(1, login);
            statementForFind.setString(2, password);
            set = statementForFind.executeQuery();
            if (set.next()) {
                log.info("Берём учётные данные из репозитория");
                return Optional.of(new Credentials()
                        .withId(set.getInt(1))
                        .withLogin(set.getString(2))
                        .withPassword(set.getString("password")));
            } else {
                log.error("Учётные данные не найдены");
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        } finally {
            closeResource(set);
        }
        return Optional.empty();
    }

    @Override
    public List<Credentials> getAllCredentials() {
        log.info("Берём все учётные данные");
        List<Credentials> credentials = new ArrayList<>();
        ResultSet set = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement statementForFind = connection.prepareStatement(findAllCredentials)) {
            set = statementForFind.executeQuery();
            if (set.next()) {
                credentials.add(new Credentials()
                        .withId(set.getInt(1))
                        .withLogin(set.getString("login"))
                        .withPassword(set.getString(3)));
            } else {
                log.error("Учётные данные не найдены");
            }
            return credentials;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        } finally {
            closeResource(set);
        }
        return null;
    }

    @Override
    public boolean updateCredentialById(int id, String newLogin, String newPassword) {
        try (Connection connection = pool.getConnection();
            PreparedStatement statementForUpdate = connection.prepareStatement(updateCredentials))
        {
            statementForUpdate.setString(1, newLogin);
            statementForUpdate.setString(2, newPassword);
            statementForUpdate.setInt(3, id);
            if (statementForUpdate.executeUpdate() > 0) {
                log.info("Изменение учётных данных в репозитории");
                return true;
            } else {
                log.error("Учётные данные не найдены, изменений не произошло");
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        }
        return false;
    }

    @Override
    public boolean deleteCredentialById(int id) {
        try (Connection connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(deleteCredentialsByID))
        {
            statement.setInt(1, id);
            if (statement.executeUpdate() > 0) {
                log.info("Удаление учётных данных из репозитория");
                return true;
            } else {
                log.error("Учётные данные не найдены, удаления не произошло");
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        }
        return false;
    }

    @Override
    public boolean deleteCredentialByLoginAndPassword(String login, String password) {
        try (Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteCredentialsByLoginAndPassword))
        {
            statement.setString(1, login);
            statement.setString(2, password);
            if (statement.executeUpdate() > 0) {
                log.info("Удаление учётных данных из репозитория");
                return true;
            } else {
                log.error("Учётные данные не найдены, удаления не произошло");
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        }
        return false;
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
