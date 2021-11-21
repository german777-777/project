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

    // обновление логина и пароля у Credentials
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
    public static final String putPerson = "INSERT INTO person (first_name, last_name, patronymic, date_of_birth, credential_id, role) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    // "взятие" Person по ID
    //language=SQL
    public static final String findPersonByID = "SELECT p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, p.role, c.id, c.login, c.password " +
            "FROM person p LEFT OUTER JOIN credentials c on p.credential_id = c.id WHERE p.id = ?";

    // "взятие" Person по Credentials
    //language=SQL
    public static final String findPersonByCredentials = "SELECT p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, p.role, c.id, c.login, c.password " +
            "FROM person p LEFT OUTER JOIN credentials c on p.credential_id = c.id WHERE p.credential_id = ?";

    // "взятие" Person по ФИО
    //language=SQL
    public static final String findPersonByName = "SELECT p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, p.role, c.id, c.login, c.password " +
            "FROM person p LEFT OUTER JOIN credentials c on p.credential_id = c.id " +
            "WHERE first_name = ? AND last_name = ? AND patronymic = ?";

    // "взятие" всех Persons и их Credentials
    //language=SQL
    public static final String findAllPersons = "SELECT p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, p.role, c.id, c.login, c.password " +
            "FROM person p LEFT OUTER JOIN credentials c on p.credential_id = c.id";

    // обновление ФИО по ID Person
    //language=SQL
    public static final String updatePersonNameByID = "UPDATE person " +
            "SET first_name = ?, last_name = ?, patronymic = ? WHERE id = ?";

    // обновление даты рождения Person по ID
    //language=SQL
    public static final String updatePersonDateOfBirthByID = "UPDATE person " +
            "SET date_of_birth = ? WHERE id = ?";

    // обновление Credentials Person по ID
    //language=SQL
    public static final String updatePersonCredentialsByID = "UPDATE credentials c SET login = ?, password = ? " +
            "FROM person p WHERE p.credential_id = c.id AND p.id = ?";

    // удаление Person по ID
    //language=SQL
    public static final String deletePersonByID = "DELETE FROM person WHERE id = ?";

    // удаление Person по ФИО
    //language=SQL
    public static final String deletePersonByName = "DELETE FROM person WHERE first_name = ? AND last_name = ? AND patronymic = ?";


}
