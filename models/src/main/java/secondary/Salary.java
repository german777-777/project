package secondary;

import entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import users.Person;
import users.Teacher;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "salaries")
@Entity
@NamedQueries({
        @NamedQuery(name = "getSalaryByID", query = "from Salary s where s.id = :id"),
        @NamedQuery(name = "getSalariesByTeacherID", query = "from Salary s where s.teacher.id = :teacherID"),
        @NamedQuery(name = "getSalariesByDate", query = "from Salary s where s.dateOfSalary = :dateOfSalary")
})
public class Salary extends AbstractEntity {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Teacher teacher;

    @Column(name = "date_of_salary")
    private LocalDate dateOfSalary;

    @Column(name = "count")
    private int salary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Salary salary1 = (Salary) o;
        return salary == salary1.salary && teacher.equals(salary1.teacher) && dateOfSalary.equals(salary1.dateOfSalary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), teacher, dateOfSalary, salary);
    }

    public Salary withId(int id) {
        setId(id);
        return this;
    }

    public Salary withDateOfSalary(LocalDate dateOfSalary) {
        setDateOfSalary(dateOfSalary);
        return this;
    }

    public Salary withSalary(int salary) {
        setSalary(salary);
        return this;
    }

    public Salary withTeacher(Teacher teacher) {
        setTeacher(teacher);
        return this;
    }


}
