package secondary;

import entity.AbstractEntity;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Subject extends AbstractEntity {
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subject subject = (Subject) o;
        return name.equals(subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    public Subject withId(int id) {
        setId(id);
        return this;
    }

    public Subject withName(String name) {
        setName(name);
        return this;
    }
}
