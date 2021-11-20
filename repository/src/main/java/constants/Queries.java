package constants;

public final class Queries {
    // запросы к таблице Credentials

    // проверка, есть ли Credentials в БД по логину и паролю
    //language=SQL
    public static final String checkCredentialsByLoginAndPassword = "SELECT * FROM credentials WHERE EXISTS(SELECT 1 FROM credentials WHERE login = ? AND password = ?);";

    // вставка Credentials в БД
    //language=SQL
    public static final String putCredentials = "INSERT INTO credentials (login, password) VALUES (?, ?);";

    // "взятие" Credentials по ID
    //language=SQL
    public static final String findCredentialsByID = "SELECT * FROM credentials WHERE id = ?;";

    // "взятие" Credentials по логину и паролю
    //language=SQL
    public static final String findCredentialsByLoginAndPassword = "SELECT * FROM credentials WHERE login = ? AND password = ?";

    // "взятие" всех Credentials
    //language=SQL
    public static final String findAllCredentials = "SELECT * FROM credentials";

    // обновление логина и паролю у Credentials
    //language=SQL
    public static final String updateCredentials = "UPDATE credentials SET login = ? AND password = ? WHERE id = ?";

    // удаление Credentials по ID
    //language=SQL
    public static final String deleteCredentialsByID = "DELETE FROM credentials WHERE id = ?";

    // удаление Credentials по логину и паролю
    //language=SQL
    public static final String deleteCredentialsByLoginAndPassword = "DELETE FROM credentials WHERE login = ? AND password = ?";



    // запросы к таблице Person

}
