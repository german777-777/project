package users;

import credentials.Credentials;
import entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import role.Role;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "persons")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({
        @NamedQuery(name = "getPersonByID", query = "from Person p where p.id = :id"),
        @NamedQuery(name = "getPersonByNames", query = "from Person p where p.firstName = :firstName and p.lastName = :lastName and p.patronymic = :patronymic"),
        @NamedQuery(name = "getPersonByCredentials", query = "from Person p where p.credentials.login = :login and p.credentials.password = :password"),
})
public abstract class Person extends AbstractEntity {

    @Transient
    private Role role;

    @Column(name = "first_name")
    protected String firstName;

    @Column(name = "last_name")
    protected String lastName;

    @Column(name = "patronymic")
    protected String patronymic;

    @Column(name = "date_of_birth")
    protected LocalDate dateOfBirth;

    @OneToOne(targetEntity = Credentials.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "credential_id")
    protected Credentials credentials;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Person person = (Person) o;
        return firstName.equals(person.firstName) && lastName.equals(person.lastName) && patronymic.equals(person.patronymic) && dateOfBirth.equals(person.dateOfBirth) && credentials.equals(person.credentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, patronymic, dateOfBirth, credentials);
    }
}
