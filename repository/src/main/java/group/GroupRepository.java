package group;

import secondary.Group;
import users.Person;

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
    boolean updateGroupTeacherById(int id, Person newTeacher);

    //Delete
    boolean deleteGroupById(int id);
}
