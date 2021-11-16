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
    public boolean updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic) {
        return false;
    }

    @Override
    public boolean updateDateOfBirthById(int id, LocalDate newDateOfBirth) {
        return false;
    }

    @Override
    public boolean updateCredentialByPersonId(int id, Credentials newCredential) {
        return false;
    }

    @Override
    public boolean deletePersonById(int id) {
        return false;
    }

    @Override
    public boolean deletePersonByName(String firstName, String lastName, String patronymic) {
        return false;
    }


}
