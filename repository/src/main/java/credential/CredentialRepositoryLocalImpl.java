package credential;

import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CredentialRepositoryLocalImpl implements CredentialRepository {
    private static int ID = 0;
    private final Map<Integer, Credentials> credentialsMap = new HashMap<>();
    private static volatile CredentialRepositoryLocalImpl instance;

    private CredentialRepositoryLocalImpl() {
    }

    public static CredentialRepositoryLocalImpl getInstance() {
        if (instance == null) {
            synchronized (CredentialRepositoryLocalImpl.class) {
                if (instance == null) {
                    instance = new CredentialRepositoryLocalImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Credentials createCredential(Credentials credentials) {
        log.debug("Попытка найти в репозитории учётные данные");
        Optional<Credentials> optionalCredential = credentialsMap.values()
                .stream()
                .filter(cred -> cred.getId() == ID)
                .filter(cred -> cred.getLogin().equals(credentials.getLogin()))
                .filter(cred -> cred.getPassword().equals(credentials.getPassword()))
                .findAny();
        if (optionalCredential.isEmpty()) {
            ID++;
            log.info("Добавлены новые учётные данные");
            credentialsMap.put(ID, credentials.withId(ID));
            return credentials;
        }
        log.error("Переданные учётные данные уже существуют");
        return null;
    }

    @Override
    public Optional<Credentials> getCredentialById(int id) {
        log.debug("Попытка взять учётные данные по ID");
        Optional<Credentials> optionalCredential = credentialsMap.values()
                .stream()
                .filter(credential -> id == credential.getId())
                .findAny();
        if (optionalCredential.isPresent()) {
            log.info("Берём учётные данные из репозитория");
            return optionalCredential;
        }
        log.error("Учётные данные не найдены");
        return Optional.empty();
    }

    @Override
    public Optional<Credentials> getCredentialByLoginAndPassword(String login, String password) {
        log.debug("Попытка взять учётные данные по логину и паролю");
        Optional<Credentials> optionalCredential = credentialsMap.values()
                .stream()
                .filter(credential -> login.equals(credential.getLogin()))
                .filter(credential -> password.equals(credential.getPassword()))
                .findAny();
        if (optionalCredential.isPresent()) {
            log.info("Берём учётные данные из репозитория");
            return optionalCredential;
        }
        log.error("Учётные данные не найдены");
        return Optional.empty();
    }

    @Override
    public List<Credentials> getAllCredentials() {
        log.info("Берём все учётные данные");
        return new ArrayList<>(credentialsMap.values());
    }

    @Override
    public Optional<Credentials> updateCredentialById(int id, String newLogin, String newPassword) {
        log.debug("Попытка взять учётные данных по ID");
        Optional<Credentials> optionalCredential = credentialsMap.values()
                .stream()
                .filter(cred -> id == cred.getId())
                .findAny();

        if (optionalCredential.isPresent()) {
            log.info("Изменение учётных данных в репозитории");
            Credentials credentialFromOptional = optionalCredential.get();
            credentialFromOptional.setLogin(newLogin);
            credentialFromOptional.setPassword(newPassword);
            credentialsMap.put(id, credentialFromOptional);
            return optionalCredential;
        }
        log.error("Учётные данные не найдены, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Credentials> deleteCredentialById(int id) {
        log.debug("Попытка взять учётные данные по ID");
        Optional<Credentials> optionalCredential = credentialsMap.values()
                .stream()
                .filter(cred -> id == cred.getId())
                .findAny();
        if (optionalCredential.isPresent()) {
            log.info("Удаление учётных данных из репозитория");
            credentialsMap.remove(id);
        }
        log.error("Учётные данные не найдены, удаления не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Credentials> deleteCredentialByLoginAndPassword(String login, String password) {
        log.debug("Попытка взять учётные данные по логину и паролю");
        Optional<Credentials> optionalCredential = credentialsMap.values()
                .stream()
                .filter(cred -> login.equals(cred.getLogin()))
                .filter(cred -> password.equals(cred.getPassword()))
                .findAny();
        if (optionalCredential.isPresent()) {
            log.info("Удаление учётных данных из репозитория");
            credentialsMap.remove(optionalCredential.get().getId());
        }
        log.error("Учётные данные не найдены, удаления не произошло");
        return Optional.empty();
    }
}
