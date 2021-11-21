package group;

import secondary.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    //Create
    Group createGroup(Group group);

    //Read
    Optional<Group> getGroupById(int id);
    Optional<Group> getGroupByName(String name);
    List<Group> getAllGroups();

    //Update
    boolean updateGroupNameById(int id, String newName);

    //Delete
    boolean deleteGroupById(int id);
}
