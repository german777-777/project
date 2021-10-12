package by.academy.users;

import by.academy.privacy_data.PrivacyData;
import java.util.Objects;

public class Admin {
    private final int id;
    private String fio;
    private int age;
    private PrivacyData loginAndPassword;


    public Admin(String fio, int age, PrivacyData loginAndPassword) {
        this.fio = fio;
        this.age = age;
        this.loginAndPassword = loginAndPassword;
        id = this.hashCode();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return id == admin.id && age == admin.age && fio.equals(admin.fio) && loginAndPassword.equals(admin.loginAndPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fio, age, loginAndPassword);
    }

    @Override
    public String toString() {
        return "Админ: " + fio + " ,возраст: " + age ;
    }
}
