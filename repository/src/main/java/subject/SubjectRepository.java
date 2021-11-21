package subject;

import secondary.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository {
    //Create
    Subject createSubject(Subject subject);

    //Read
    Optional<Subject> getSubjectById(int id);
    Optional<Subject> getSubjectByName(String name);
    List<Subject> getAllSubjects();

    //Update
    boolean updateSubjectNameById(int id, String newName);
    boolean updateSubjectNameByName(String oldName, String newName);

    //Delete
    boolean deleteSubjectById(int id);
    boolean deleteSubjectByName(String name);
}
