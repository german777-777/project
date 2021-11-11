package subject;

import lombok.extern.slf4j.Slf4j;
import secondary.Subject;

import java.util.*;

@Slf4j
public class SubjectRepositoryLocalImpl implements SubjectRepository {
    private final Map<Integer, Subject> subjectMap = new HashMap<>();
    private static volatile SubjectRepositoryLocalImpl instance;

    private SubjectRepositoryLocalImpl() {
    }

    public static SubjectRepositoryLocalImpl getInstance() {
        if (instance == null) {
            synchronized (SubjectRepositoryLocalImpl.class) {
                if (instance == null) {
                    instance = new SubjectRepositoryLocalImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Subject createSubject(Subject subject) {
        log.debug("Попытка найти предмет по имени");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(subj -> subject.getName().equals(subj.getName()))
                .findAny();
        if (optionalSubject.isEmpty()) {
            log.info("Добавлен новый предмет");
            subjectMap.put(subject.getId(), subject);
            return subject;
        }
        log.error("Переданные предмет уже существует");
        return null;

    }

    @Override
    public Optional<Subject> getSubjectById(int id) {
        log.debug("Попытка найти предмет по ID");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(subject -> id == subject.getId())
                .findAny();
        if (optionalSubject.isPresent()) {
            log.info("Берём предмет из репозитория");
            return optionalSubject;
        }
        log.error("Предмет не найден");
        return Optional.empty();
    }

    @Override
    public Optional<Subject> getSubjectByName(String name) {
        log.debug("Попытка найти предмет по имени");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(subject -> name.equals(subject.getName()))
                .findAny();
        if (optionalSubject.isPresent()) {
            log.info("Берём предмт из репозитория");
            return optionalSubject;
        }
        log.error("Предмет не найден");
        return Optional.empty();
    }

    @Override
    public List<Subject> getAllSubjects() {
        log.info("Берём все предметы");
        return new ArrayList<>(subjectMap.values());
    }

    @Override
    public Optional<Subject> updateSubjectNameById(int id, String newName) {
        log.debug("Попытка взять предмет по ID");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(sb -> id == sb.getId())
                .findAny();
        if (optionalSubject.isPresent()) {
            log.info("Изменение предмета в репозитории");
            Subject subjectFromOptional = optionalSubject.get();
            subjectFromOptional.setName(newName);
            subjectMap.put(id, subjectFromOptional);
            return optionalSubject;
        }
        log.error("Предмет не найден, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Subject> updateSubjectNameByName(String oldName, String newName) {
        log.debug("Попытка взять предмет по имени");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(sb -> oldName.equals(sb.getName()))
                .findAny();
        if (optionalSubject.isPresent()) {
            log.info("Изменение предмета в репозитории");
            Subject subjectFromOptional = optionalSubject.get();
            subjectFromOptional.setName(newName);
            subjectMap.put(subjectFromOptional.getId(), subjectFromOptional);
            return optionalSubject;
        }
        log.error("Предмет не найден, изменений не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Subject> deleteSubjectById(int id) {
        log.debug("Попытка взять предмет по ID");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(sb -> id == sb.getId())
                .findAny();
        if (optionalSubject.isPresent()) {
            log.info("Удаление прдмета в репозитории");
            subjectMap.remove(id);
            return optionalSubject;
        }
        log.error("Предмет не найден, удаления не произошло");
        return Optional.empty();
    }

    @Override
    public Optional<Subject> deleteSubjectByName(String name) {
        log.debug("Попытка взять предмет по имени");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(sb -> name.equals(sb.getName()))
                .findAny();
        if (optionalSubject.isPresent()) {
            log.info("Удаление прдмета в репозитории");
            Subject subjectFromOptional = optionalSubject.get();
            subjectMap.remove(subjectFromOptional.getId());
            return optionalSubject;
        }
        log.error("Предмет не найден, удаления не произошло");
        return Optional.empty();
    }
}
