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
    Optional<Group> updateGroupNameById(int id, String newName);

    //Delete
    Optional<Group> deleteGroupById(int id);
}
