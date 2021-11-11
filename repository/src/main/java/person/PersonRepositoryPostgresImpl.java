package person;

import credentials.Credentials;
import users.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PersonRepositoryPostgresImpl implements PersonRepository {
    @Override
    public Person createPerson(Person person) {
        return null;
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Person> getPersonByName(String firstName, String lastName, String patronymic) {
        return Optional.empty();
    }

    @Override
    public Optional<Person> getPersonByCredentials(String login, String password) {
        return Optional.empty();
    }

    @Override
    public List<Person> getAllPersons() {
        return null;
    }

    @Override
    public Optional<Person> updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic) {
        return Optional.empty();
    }

    @Override
    public Optional<Person> updateDateOfBirthById(int id, LocalDate newDateOfBirth) {
        return Optional.empty();
    }

    @Override
    public Optional<Person> updateCredentialByPersonId(int id, Credentials newCredential) {
        return Optional.empty();
    }

    @Override
    public Optional<Person> deletePersonById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Person> deletePersonByName(String firstName, String lastName, String patronymic) {
        return Optional.empty();
    }
}
