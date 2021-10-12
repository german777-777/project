package by.academy.privacy_data;

public final class PrivacyData {
    private String login;
    private String password;

    public PrivacyData(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
