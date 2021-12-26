package users;

import credentials.Credentials;
import lombok.*;
import role.Role;
import secondary.Mark;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Student extends Person {
    private final Role role = Role.STUDENT;
    List<Mark> marks = new ArrayList<>();

    public Student withId(int id) {
        setId(id);
        return this;
    }

    public Student withFirstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public Student withLastName(String lastName) {
        setLastName(lastName);
        return this;
    }

    public Student withPatronymic(String patronymic) {
        setPatronymic(patronymic);
        return this;
    }

    public Student withDateOfBirth(LocalDate dateOfBirth) {
        setDateOfBirth(dateOfBirth);
        return this;
    }

    public Student withCredentials(Credentials credentials) {
        setCredentials(credentials);
        return this;
    }

    public Student withMarks(List<Mark> marks) {
        setMarks(marks);
        return this;
    }
}
