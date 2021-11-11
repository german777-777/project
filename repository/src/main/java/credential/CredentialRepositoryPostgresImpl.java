package credential;

import credentials.Credentials;

import java.util.List;
import java.util.Optional;

public class CredentialRepositoryPostgresImpl implements CredentialRepository {
    @Override
    public Credentials createCredential(Credentials credentials) {
        return null;
    }

    @Override
    public Optional<Credentials> getCredentialById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Credentials> getCredentialByLoginAndPassword(String login, String password) {
        return Optional.empty();
    }

    @Override
    public List<Credentials> getAllCredentials() {
        return null;
    }

    @Override
    public Optional<Credentials> updateCredentialById(int id, String newLogin, String password) {
        return Optional.empty();
    }

    @Override
    public Optional<Credentials> deleteCredentialById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Credentials> deleteCredentialByLoginAndPassword(String login, String password) {
        return Optional.empty();
    }
}
