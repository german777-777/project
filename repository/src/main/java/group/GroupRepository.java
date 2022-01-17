package group;

import secondary.Group;
import secondary.Subject;
import users.Person;
import users.Student;

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
    boolean updateStudentsAdd(int id, Person newStudent);
    boolean updateStudentsRemove(int id, Person removableStudent);
    boolean updateSubjectsAdd(int id, Subject newSubject);
    boolean updateSubjectsRemove(int id, Subject removableSubject);

    //Delete
    boolean deleteGroupById(int id);
}
