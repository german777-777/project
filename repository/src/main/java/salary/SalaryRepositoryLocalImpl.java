package salary;

import lombok.extern.slf4j.Slf4j;
import secondary.Salary;
import users.Teacher;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SalaryRepositoryLocalImpl implements SalaryRepository {
    private static final Map<Integer, Salary> salaryMap = new HashMap<>();
    private static int ID = 0;
    private static volatile SalaryRepositoryLocalImpl instance;

    private SalaryRepositoryLocalImpl() {
    }

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
            ID++;
            log.info("Добавлена новая зарплата");
            salaryMap.put(ID, salary.withId(ID));
            return salary;
        }
        log.error("Переданная зарплата уже существует");
        return null;
    }

    @Override
    public Optional<Salary> getSalaryByID(int salaryID) {
        log.debug("Попытка взять зарплату по ID");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(salary -> salaryID == salary.getId())
                .findAny();
        if (optionalSalary.isPresent()) {
            log.info("Найдена зарплата");
            return optionalSalary;
        } else {
            log.error("Зарплата не найдена");
            return Optional.empty();
        }
    }

    @Override
    public List<Salary> getSalariesByTeacherId(int teacherId) {
        log.debug("Попытка взять зарплаты по ID учителя");
        List<Salary> salaries = salaryMap.values()
                .stream()
                .filter(salary -> teacherId == salary.getTeacher().getId())
                .collect(Collectors.toList());
        log.info("Берём зарплаты из репозитория");
        return salaries;
    }

    @Override
    public List<Salary> getSalariesByDateOfSalary(LocalDate dateOfSalary) {
        log.debug("Попытка взять зарплаты по дате выдачи");
        List<Salary> salaries = salaryMap.values()
                .stream()
                .filter(salary -> dateOfSalary.equals(salary.getDateOfSalary()))
                .collect(Collectors.toList());
        log.info("Берём зарплаты из репозитория");
        return salaries;
    }

    @Override
    public List<Salary> getAllSalaries() {
        log.info("Берём все зарплаты");
        return new ArrayList<>(salaryMap.values());
    }

    @Override
    public boolean updateSalaryById(int id, int newSalary) {
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
            return salaryMap.containsValue(salaryFromOptional);
        }
        log.error("Зарплата не найдена, изменений не произошло");
        return false;
    }

    @Override
    public boolean updateTeacherReceivedSalaryById(int id, Teacher newTeacher) {
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
            return salaryMap.containsValue(salaryFromOptional);
        }
        log.error("Зарплата не найдена, изменений не произошло");
        return false;
    }

    @Override
    public boolean updateDateOfSalaryById(int id, LocalDate newDateOfSalary) {
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
            return salaryMap.containsValue(salaryFromOptional);
        }
        log.error("Зарплата не найдена, изменений не произошло");
        return false;
    }

    @Override
    public boolean deleteSalaryById(int id) {
        log.debug("Попытка взять зарплату по ID");
        Optional<Salary> optionalSalary = salaryMap.values()
                .stream()
                .filter(salary -> id == salary.getId())
                .findAny();
        if (optionalSalary.isPresent()) {
            log.info("Удаление зарплаты в репозитории");
            Salary salaryFromOptional = optionalSalary.get();
            return salaryMap.remove(id, salaryFromOptional);
        }
        log.error("Зарплата не найдена, удаления не произошло");
        return false;
    }
}
