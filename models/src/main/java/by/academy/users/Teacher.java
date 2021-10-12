package by.academy.users;

import by.academy.privacy_data.PrivacyData;
import by.academy.secondary_models.Group;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Teacher {
    private final int id;
    private String fio;
    private int age;
    private PrivacyData loginAndPassword;

    private Group group;
    private List<BigDecimal> salaries;

    public Teacher(String fio, int age, PrivacyData loginAndPassword) {
        this.fio = fio;
        this.age = age;
        this.loginAndPassword = loginAndPassword;
        id = hashCode();
    }

    public int getId() {
        return id;
    }

    public String getFio() {
        return fio;
    }

    public int getAge() {
        return age;
    }

    public PrivacyData getLoginAndPassword() {
        return loginAndPassword;
    }

    public Group getGroup() {
        return group;
    }

    public List<BigDecimal> getSalaries() {
        return salaries;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setSalaries(List<BigDecimal> salaries) {
        this.salaries = salaries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return age == teacher.age && fio.equals(teacher.fio) && loginAndPassword.equals(teacher.loginAndPassword) && group.equals(teacher.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fio, age, loginAndPassword, group);
    }

    @Override
    public String toString() {
        String result;
        if (group == null) {
            result = "Преподаватель: " + fio + " возраст, " + age + ". Не ведёт ни у какой группы";
        } else {
            result = "Преподаватель: " + fio + " возраст, " + age + ". Ведёт у группы: " + group;
        }
        return result;
    }
}
