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
    Optional<Subject> updateSubjectNameById(int id, String newName);
    Optional<Subject> updateSubjectNameByName(String oldName, String newName);

    //Delete
    Optional<Subject> deleteSubjectById(int id);
    Optional<Subject> deleteSubjectByName(String name);
}
