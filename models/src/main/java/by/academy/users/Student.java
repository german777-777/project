package by.academy.users;

import by.academy.privacy_data.PrivacyData;
import by.academy.secondary_models.Group;
import by.academy.secondary_models.Theme;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Student {
    private final int id;
    private String fio;
    private int age;
    private PrivacyData loginAndPassword;

    private List<Group> groups;
    private Map<Theme, Integer> marks;

    public Student(String fio, int age, PrivacyData loginAndPassword) {
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

    public List<Group> getGroups() {
        return groups;
    }

    public Map<Theme, Integer> getMarks() {
        return marks;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void setMarks(Map<Theme, Integer> marks) {
        this.marks = marks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id && age == student.age && fio.equals(student.fio) && loginAndPassword.equals(student.loginAndPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fio, age, loginAndPassword);
    }

    @Override
    public String toString() {
        return "Студент: " + fio + ",возраст: " + age;
    }
}
