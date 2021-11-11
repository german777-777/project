package users;

import credentials.Credentials;
import lombok.*;
import role.Role;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class Admin extends Person {
    private final Role role = Role.ADMIN;

    public Admin withId(int id) {
        setId(id);
        return this;
    }

    public Admin withFirstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public Admin withLastName(String lastName) {
        setFirstName(lastName);
        return this;
    }

    public Admin withPatronymic(String patronymic) {
        setFirstName(patronymic);
        return this;
    }

    public Admin withDateOfBirth(LocalDate dateOfBirth) {
        setDateOfBirth(dateOfBirth);
        return this;
    }

    public Admin withCredentials(Credentials credentials) {
        setCredentials(credentials);
        return this;
    }

}
