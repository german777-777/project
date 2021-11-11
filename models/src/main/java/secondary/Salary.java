package secondary;

import entity.AbstractEntity;
import lombok.*;
import users.Teacher;

import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Salary extends AbstractEntity {
    // для Teacher-a: кому конкретно принадлежит зарплата
    private Teacher teacher;

    // дата выдачи зарплаты
    private LocalDate dateOfSalary;

    // сама зарплата
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

    public Salary withTeacher(Teacher teacher) {
        setTeacher(teacher);
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
}
