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
    public Optional<Subject> updateSubjectNameById(int id, String newName) {
        return Optional.empty();
    }

    @Override
    public Optional<Subject> updateSubjectNameByName(String oldName, String newName) {
        return Optional.empty();
    }

    @Override
    public Optional<Subject> deleteSubjectById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Subject> deleteSubjectByName(String name) {
        return Optional.empty();
    }
}
