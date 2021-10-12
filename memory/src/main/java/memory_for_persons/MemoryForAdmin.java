package memory_for_persons;

import by.academy.users.Admin;

import java.util.HashMap;
import java.util.Map;

public final class MemoryForAdmin {
    private static final Map<Integer, Admin> adminMap = new HashMap<>(1);

    public static void put(Integer id, Admin admin) {
        adminMap.put(id, admin);
    }

    public static Map<Integer, Admin> getAdmin() {
        return adminMap;
    }
}
