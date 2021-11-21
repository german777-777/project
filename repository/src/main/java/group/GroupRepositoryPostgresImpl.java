package group;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import secondary.Group;

import java.util.List;
import java.util.Optional;

public class GroupRepositoryPostgresImpl implements GroupRepository {
    private static volatile GroupRepositoryPostgresImpl instance;
    private final ComboPooledDataSource pool;

    private GroupRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    public static GroupRepositoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (GroupRepositoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new GroupRepositoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

    @Override
    public Group createGroup(Group group) {
        return null;
    }

    @Override
    public Optional<Group> getGroupById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Group> getGroupByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<Group> getAllGroups() {
        return null;
    }

    @Override
    public boolean updateGroupNameById(int id, String newName) {
        return false;
    }

    @Override
    public boolean deleteGroupById(int id) {
        return false;
    }
}
