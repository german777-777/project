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
    boolean updateCredentialById(int id, String newLogin, String newPassword);

    //Delete
    boolean deleteCredentialById(int id);
    boolean deleteCredentialByLoginAndPassword(String login, String password);


}
