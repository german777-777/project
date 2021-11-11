package credential;

import credentials.Credentials;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository {
    //Create
    Credentials createCredential(Credentials credentials);

    //Read
    Optional<Credentials> getCredentialById(int id);
    Optional<Credentials> getCredentialByLoginAndPassword(String login, String password);
    List<Credentials> getAllCredentials();

    //Update
    Optional<Credentials> updateCredentialById(int id, String newLogin, String newPassword);

    //Delete
    Optional<Credentials> deleteCredentialById(int id);
    Optional<Credentials> deleteCredentialByLoginAndPassword(String login, String password);


}
