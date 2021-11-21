package credentials;

import entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class Credentials extends AbstractEntity {
    private String login;
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Credentials that = (Credentials) o;
        return login.equals(that.login) && password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), login, password);
    }

    public Credentials withId(int id) {
        setId(id);
        return this;
    }

    public Credentials withLogin(String login) {
        setLogin(login);
        return this;
    }

    public Credentials withPassword(String password) {
        setPassword(password);
        return this;
    }
}
