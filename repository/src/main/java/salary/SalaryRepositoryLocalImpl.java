package salary;

import lombok.extern.slf4j.Slf4j;
import secondary.Salary;
import users.Teacher;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SalaryRepositoryLocalImpl implements SalaryRepository {
    private static final Map<Integer, Salary> salaryMap = new HashMap<>();
    private static int ID = 0;
    private static volatile SalaryRepositoryLocalImpl instance;

    private SalaryRepositoryLocalImpl(){}

    public static SalaryRepositoryLocalImpl getInstance() {
        if (instance == null) {
            synchronized (SalaryRepositoryLocalImpl.class) {
                if (instance == null) {
                    instance = new SalaryRepositoryLocalImpl();
                }
            }
        }
        return instance;
    }
    @Override
    public Salary createSalary(Salary salary) {
        log.debug("Попытка найти зарплату по учителю и дате зарплаты");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(sal -> sal.getId() == ID)
                .filter(sal -> sal.getTeacher().equals(salary.getTeacher()))
                .filter(sal -> sal.getDateOfSalary().equals(salary.getDateOfSalary()))
                .findAny();
        if (optionalSalary.isEmpty()) {
            log.info("Добавлена новая зарплата");
            salaryMap.put(ID++, salary);
            return salary;
        }
        log.error("Переданная зарплата уже существует");
        return null;
    }

    @Override
    public Optional<Salary> getSalaryByTeacherId(int teacherId) {
        log.debug("Попытка взять зарплату по ID учителя");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(salary -> teacherId == salary.getTeacher().getId())
                .findAny();
        if (optionalSalary.isPresent()) {
            log.info("Берём зарплату из репозитория");
            return optionalSalary;
        }
        log.error("Зарплата не найдена");
        return Optional.empty();
    }

    @Override
    public Optional<Salary> getSalaryByDateOfSalary(LocalDate dateOfSalary) {
        log.debug("Попытка взять зарплату по дате выдачи");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(salary -> dateOfSalary.equals(salary.getDateOfSalary()))
                .findAny();
        if (optionalSalary.isPresent()) {
            log.info("Берём зарплату из репозитория");
            return optionalSalary;
        }
        log.error("Зарплата не найдена");
        return Optional.empty();
    }

    @Override
    public List<Salary> getAllSalaries() {
        log.info("Берём все зарплаты");
        return null;
    }

    @Override
    public Optional<Salary> updateSalaryById(int id, int newSalary) {
        log.debug("Попытка взять зарплату по ID");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(salary -> id == salary.getId())
                .findAny();
        if (optionalSalary.isPresent()) {
            log.info("Изменение зарплаты в репозитории");
            Salary salaryFromOptional = optionalSalary.get();
            salaryFromOptional.setSalary(newSalary);
            salaryMap.put(id, salaryFromOptional);
            return optionalSalary;
        }
        log.error("Зарплата не найдена, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Salary> updateTeacherReceivedSalaryById(int id, Teacher newTeacher) {
        log.debug("Попытка взять зарплату по ID");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(salary -> id == salary.getId())
                .findAny();
        if (optionalSalary.isPresent()) {
            log.info("Изменение зарплаты в репозитории");
            Salary salaryFromOptional = optionalSalary.get();
            salaryFromOptional.setTeacher(newTeacher);
            salaryMap.put(id, salaryFromOptional);
            return optionalSalary;
        }
        log.error("Зарплата не найдена, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Salary> updateDateOfSalaryById(int id, LocalDate newDateOfSalary) {
        log.debug("Попытка взять зарплату по ID");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(salary -> id == salary.getId())
                .findAny();
        if (optionalSalary.isPresent()) {
            log.info("Изменение зарплаты в репозитории");
            Salary salaryFromOptional = optionalSalary.get();
            salaryFromOptional.setDateOfSalary(newDateOfSalary);
            salaryMap.put(id, salaryFromOptional);
            return optionalSalary;
        }
        log.error("Зарплата не найдена, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Salary> deleteSalaryById(int id) {
        log.debug("Попытка взять зарплату по ID");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(salary -> id == salary.getId())
                .findAny();
        if (optionalSalary.isPresent()) {
            log.info("Удаление зарплаты в репозитории");
            salaryMap.remove(id);
        }
        log.error("Зарплата не найдена, удаления не произошло");
        return Optional.empty();
    }
}
