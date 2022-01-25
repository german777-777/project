package users;

import credentials.Credentials;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import role.Role;
import secondary.Salary;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@DiscriminatorValue("Учитель")
@Entity
public class Teacher extends Person {

    @Transient
    private final Role role = Role.TEACHER;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    @ToString.Exclude
    private Set<Salary> salaries = new HashSet<>();

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

    public Teacher withSalaries(Set<Salary> salaries) {
        setSalaries(salaries);
        return this;
    }

    public void addSalary(Salary salary) {
        this.salaries.add(salary);
    }

    public void removeSalary(Salary salary) {
        this.salaries.remove(salary);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return role == teacher.role && salaries.equals(teacher.salaries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role, salaries);
    }
}
