package salary;

import secondary.Salary;
import users.Teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SalaryRepositoryPostgresImpl implements SalaryRepository {
    @Override
    public Salary createSalary(Salary salary) {
        return null;
    }

    @Override
    public Optional<Salary> getSalaryByTeacherId(int teacherId) {
        return Optional.empty();
    }

    @Override
    public Optional<Salary> getSalaryByDateOfSalary(LocalDate dateOfSalary) {
        return Optional.empty();
    }

    @Override
    public List<Salary> getAllSalaries() {
        return null;
    }

    @Override
    public Optional<Salary> updateSalaryById(int id, int newSalary) {
        return Optional.empty();
    }

    @Override
    public Optional<Salary> updateTeacherReceivedSalaryById(int id, Teacher teacher) {
        return Optional.empty();
    }

    @Override
    public Optional<Salary> updateDateOfSalaryById(int id, LocalDate newDateOfSalary) {
        return Optional.empty();
    }

    @Override
    public Optional<Salary> deleteSalaryById(int id) {
        return Optional.empty();
    }
}
