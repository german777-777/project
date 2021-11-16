package group;

import secondary.Group;

import java.util.List;
import java.util.Optional;

public class GroupRepositoryPostgresImpl implements GroupRepository {
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
