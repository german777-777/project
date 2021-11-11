package salary;

import secondary.Salary;
import users.Teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryRepository {
    //Create
    Salary createSalary(Salary salary);

    //Read
    Optional<Salary> getSalaryByTeacherId(int teacherId);
    Optional<Salary> getSalaryByDateOfSalary(LocalDate dateOfSalary);
    List<Salary> getAllSalaries();

    //Update
    Optional<Salary> updateSalaryById(int id, int newSalary);
    Optional<Salary> updateTeacherReceivedSalaryById(int id, Teacher teacher);
    Optional<Salary> updateDateOfSalaryById(int id, LocalDate newDateOfSalary);

    //Delete
    Optional<Salary> deleteSalaryById(int id);
}
