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
    Optional<Person> updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic);
    Optional<Person> updateDateOfBirthById(int id, LocalDate newDateOfBirth);
    Optional<Person> updateCredentialByPersonId(int id, Credentials newCredential);

    //Delete
    Optional<Person> deletePersonById(int id);
    Optional<Person> deletePersonByName(String firstName, String lastName, String patronymic);
}
