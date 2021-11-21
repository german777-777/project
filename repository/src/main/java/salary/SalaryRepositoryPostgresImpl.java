package salary;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import group.GroupRepositoryPostgresImpl;
import secondary.Salary;
import users.Teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SalaryRepositoryPostgresImpl implements SalaryRepository {
    private static volatile SalaryRepositoryPostgresImpl instance;
    private final ComboPooledDataSource pool;

    private SalaryRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    public static SalaryRepositoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (GroupRepositoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new SalaryRepositoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

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
    public boolean updateSalaryById(int id, int newSalary) {
        return false;
    }

    @Override
    public boolean updateTeacherReceivedSalaryById(int id, Teacher teacher) {
        return false;
    }

    @Override
    public boolean updateDateOfSalaryById(int id, LocalDate newDateOfSalary) {
        return false;
    }

    @Override
    public boolean deleteSalaryById(int id) {
        return false;
    }


}
