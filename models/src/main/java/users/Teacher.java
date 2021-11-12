package users;

import credentials.Credentials;
import lombok.*;
import role.Role;
import secondary.Mark;
import secondary.Salary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Teacher extends Person {
    private final Role role = Role.TEACHER;
    List<Salary> salaries = new ArrayList<>();

    public Teacher withId(int id) {
        setId(id);
        return this;
    }

    public Teacher withFirstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public Teacher withLastName(String lastName) {
        setLastName(lastName);
        return this;
    }

    public Teacher withPatronymic(String patronymic) {
        setPatronymic(patronymic);
        return this;
    }

    public Teacher withDateOfBirth(LocalDate dateOfBirth) {
        setDateOfBirth(dateOfBirth);
        return this;
    }

    public Teacher withCredentials(Credentials credentials) {
        setCredentials(credentials);
        return this;
    }

    public Teacher withSalaries(List<Salary> salaries) {
        setSalaries(salaries);
        return this;
    }
}
