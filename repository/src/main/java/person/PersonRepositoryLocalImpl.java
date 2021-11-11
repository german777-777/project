package person;

import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import users.Person;

import java.time.LocalDate;
import java.util.*;

@Slf4j
public class PersonRepositoryLocalImpl implements PersonRepository {
    private static int ID = 0;
    private final Map<Integer, Person> personMap = new HashMap<>();
    private static volatile PersonRepositoryLocalImpl instance;
    private PersonRepositoryLocalImpl() {
    }

    public static PersonRepositoryLocalImpl getInstance() {
        if (instance == null) {
            synchronized (PersonRepositoryLocalImpl.class) {
                if (instance == null) {
                    instance = new PersonRepositoryLocalImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Person createPerson(Person person) {
        log.debug("Попытка найти пользователя в репозитории");
        Optional<Person> personOptional = personMap.values()
                .stream()
                .filter(prs -> prs.getId() == ID)
                .filter(prs -> person.getFirstName().equals(prs.getFirstName()))
                .filter(prs -> person.getLastName().equals(prs.getLastName()))
                .filter(prs -> person.getPatronymic().equals(prs.getPatronymic()))
                .filter(prs -> person.getCredentials().equals(prs.getCredentials()))
                .findAny();
        if (personOptional.isEmpty()) {
            log.info("Добавлен новый пользователь");
            personMap.put(ID++, person);
            return person;
        }
        log.error("Переданный пользователь уже существует");
        return null;
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        log.debug("Попытка найти пользователя по ID");
        Optional<Person> optionalPerson = personMap.values()
                .stream()
                .filter(person -> id == person.getId())
                .findAny();
        if (optionalPerson.isPresent()) {
            log.info("Берём пользователя из репозитория");
            return optionalPerson;
        }
        log.error("Пользователь не найден");
        return Optional.empty();
    }

    @Override
    public Optional<Person> getPersonByName(String firstName, String lastName, String patronymic) {
        log.debug("Попытка найти пользователя по имени, фамилии и отчеству");
        Optional<Person> optionalPerson = personMap.values()
                .stream()
                .filter(person -> firstName.equals(person.getFirstName()))
                .filter(person -> lastName.equals(person.getLastName()))
                .filter(person -> patronymic.equals(person.getPatronymic()))
                .findAny();
        if (optionalPerson.isPresent()) {
            log.info("Берём пользователя из репозитория");
            return optionalPerson;
        }
        log.error("Пользователь не найден");
        return Optional.empty();
    }

    @Override
    public Optional<Person> getPersonByCredentials(String login, String password) {
        log.debug("Попытка найти пользователя по логину и паролю");
        Optional<Person> optionalPerson = personMap.values()
                .stream()
                .filter(person -> login.equals(person.getCredentials().getLogin()))
                .filter(person -> password.equals(person.getCredentials().getPassword()))
                .findAny();
        if (optionalPerson.isPresent()) {
            log.info("Берём пользователя из репозитория");
            return optionalPerson;
        }
        log.error("Пользователь не найден");
        return Optional.empty();
    }

    @Override
    public List<Person> getAllPersons() {
        log.info("Берём всех пользователей из репозитория");
        return new ArrayList<>(personMap.values());
    }

    @Override
    public Optional<Person> updatePersonNameById(int id, String newFirstName, String newLastName, String newPatronymic) {
        log.debug("Попытка взять пользователя по ID");
        Optional<Person> optionalPerson = getOptionalPersonFromMapById(personMap, id);
        if (optionalPerson.isPresent()) {
            log.info("Изменение пользователя в репозитории");
            Person personFromOptional = optionalPerson.get();
            personFromOptional.setFirstName(newFirstName);
            personFromOptional.setLastName(newLastName);
            personFromOptional.setPatronymic(newPatronymic);
            personMap.put(id, personFromOptional);
            return optionalPerson;
        }
        log.error("Пользователь не найден, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Person> updateDateOfBirthById(int id, LocalDate newDateOfBirth) {
        log.debug("Попытка взять пользователя по ID");
        Optional<Person> optionalPerson = getOptionalPersonFromMapById(personMap, id);
        if (optionalPerson.isPresent()) {
            log.info("Изменение пользователя в репозитории");
            Person personFromOptional = optionalPerson.get();
            personFromOptional.setDateOfBirth(newDateOfBirth);
            personMap.put(id, personFromOptional);
            return optionalPerson;
        }
        log.error("Пользователь не найден, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Person> updateCredentialByPersonId(int id, Credentials newCredential) {
        log.debug("Попытка взять пользователя по ID");
        Optional<Person> optionalPerson = getOptionalPersonFromMapById(personMap, id);
        if (optionalPerson.isPresent()) {
            log.info("Изменение пользователя в репозитории");
            Person personFromOptional = optionalPerson.get();
            personFromOptional.setCredentials(newCredential);
            personMap.put(id, personFromOptional);
            return optionalPerson;
        }
        log.error("Пользователь не найден, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Person> deletePersonById(int id) {
        log.debug("Попытка взять пользователя по ID");
        Optional<Person> optionalPerson = getOptionalPersonFromMapById(personMap, id);
        if (optionalPerson.isPresent()) {
            log.info("Удаление пользователя в репозитории");
            personMap.remove(id);
            return optionalPerson;
        }
        log.error("Пользователь не найден, удаления не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Person> deletePersonByName(String firstName, String lastName, String patronymic) {
        log.debug("Попытка взять пользователя по имени, фамилии и отчеству");
        Optional<Person> optionalPerson = personMap.values()
                .stream()
                .filter(per -> firstName.equals(per.getFirstName()))
                .filter(per -> lastName.equals(per.getLastName()))
                .filter(per -> patronymic.equals(per.getPatronymic()))
                .findAny();
        if (optionalPerson.isPresent()) {
            log.info("Удаление пользователя в репозитории");
            Person personFromOptional = optionalPerson.get();
            personMap.remove(personFromOptional.getId());
            return optionalPerson;
        }
        log.error("Пользователь не найден, удаления не произошло");
        return Optional.empty();
    }

    private Optional<Person> getOptionalPersonFromMapById(Map<Integer, Person> personMap, int id) {
        return personMap.values()
                .stream()
                .filter(per -> id == per.getId())
                .findAny();
    }
}
