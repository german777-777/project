package subject;

import secondary.Subject;

import java.util.List;
import java.util.Optional;

public class SubjectRepositoryPostgresImpl implements SubjectRepository {
    @Override
    public Subject createSubject(Subject subject) {
        return null;
    }

    @Override
    public Optional<Subject> getSubjectById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Subject> getSubjectByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<Subject> getAllSubjects() {
        return null;
    }

    @Override
    public boolean updateSubjectNameById(int id, String newName) {
        return false;
    }

    @Override
    public boolean updateSubjectNameByName(String oldName, String newName) {
        return false;
    }

    @Override
    public boolean deleteSubjectById(int id) {
        return false;
    }

    @Override
    public boolean deleteSubjectByName(String name) {
        return false;
    }


}
