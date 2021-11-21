package subject;

import lombok.extern.slf4j.Slf4j;
import secondary.Subject;

import java.util.*;

@Slf4j
public class SubjectRepositoryLocalImpl implements SubjectRepository {
    private final Map<Integer, Subject> subjectMap = new HashMap<>();
    private static int ID = 0;
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
            ID++;
            log.info("Добавлен новый предмет");
            subjectMap.put(ID, subject.withId(ID));
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
    public boolean updateSubjectNameById(int id, String newName) {
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
            return subjectMap.containsValue(subjectFromOptional);
        }
        log.error("Предмет не найден, изменений не произошло");
        return false;
    }

    @Override
    public boolean updateSubjectNameByName(String oldName, String newName) {
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
            return subjectMap.containsValue(subjectFromOptional);
        }
        log.error("Предмет не найден, изменений не произошло");
        return false;
    }

    @Override
    public boolean deleteSubjectById(int id) {
        log.debug("Попытка взять предмет по ID");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(sb -> id == sb.getId())
                .findAny();
        if (optionalSubject.isPresent()) {
            log.info("Удаление прдмета в репозитории");
            Subject subjectFromOptional = optionalSubject.get();
            return subjectMap.remove(id, subjectFromOptional);
        }
        log.error("Предмет не найден, удаления не произошло");
        return false;
    }

    @Override
    public boolean deleteSubjectByName(String name) {
        log.debug("Попытка взять предмет по имени");
        Optional<Subject> optionalSubject = subjectMap.values()
                .stream()
                .filter(sb -> name.equals(sb.getName()))
                .findAny();
        if (optionalSubject.isPresent()) {
            log.info("Удаление прдмета в репозитории");
            Subject subjectFromOptional = optionalSubject.get();
            return subjectMap.remove(subjectFromOptional.getId(), subjectFromOptional);
        }
        log.error("Предмет не найден, удаления не произошло");
        return false;
    }
}
