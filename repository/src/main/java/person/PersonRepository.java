package person;

import credentials.Credentials;
import users.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonRepository {
    //Create
    Person createPerson(Person person);

    //Read
    Optional<Person> getPersonById(int id);
    Optional<Person> getPersonByName(String firstName, String lastName, String patronymic);
    Optional<Person> getPersonByCredentials(String login, String password);
    List<Person> getAllPersons();

    //Update
    boolean updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic);
    boolean updateDateOfBirthById(int id, LocalDate newDateOfBirth);
    boolean updateCredentialByPersonId(int id, Credentials newCredential);

    //Delete
    boolean deletePersonById(int id);
    boolean deletePersonByName(String firstName, String lastName, String patronymic);
}
