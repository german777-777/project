package constants;

public final class Queries {
    // запросы к таблице Credentials

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

    // вставка Person в БД
    //language=SQL
    public static final String putPerson = "INSERT INTO person (first_name, last_name, patronymic, date_of_birth, credential_id, role) VALUES (?, ?, ?, ?, ?, ?)";

    // "взятие" Person по ID
    //language=SQL
    public static final String findPersonByID = "SELECT * FROM person WHERE id = ?";

    // "взятие" Person по Credentials
    //language=SQL
    public static final String findPersonByCredentials = "SELECT * FROM person WHERE credential_id = ?";

    // "взятие" Person по ФИО
    //language=SQL
    public static final String findPersonByName = "SELECT * FROM person WHERE first_name = ? AND last_name = ? AND patronymic = ?";


}
