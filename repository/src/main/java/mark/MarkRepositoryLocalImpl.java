package mark;

import lombok.extern.slf4j.Slf4j;
import secondary.Group;
import secondary.Mark;
import secondary.Subject;

import java.time.LocalDate;
import java.util.*;

@Slf4j
public class MarkRepositoryLocalImpl implements MarkRepository {
    private static int ID = 0;
    private final Map<Integer, Mark> markMap = new HashMap<>();
    private static volatile MarkRepositoryLocalImpl instance;

    private MarkRepositoryLocalImpl() {
    }

    public static MarkRepositoryLocalImpl getInstance() {
        if (instance == null) {
            synchronized (MarkRepositoryLocalImpl.class) {
                if (instance == null) {
                    instance = new MarkRepositoryLocalImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Mark createMark(Mark mark) {
        log.debug("Попытка найти оценку в репозитории");
        Optional<Mark> optionalMark = markMap.values()
                .stream()
                .filter(mk -> mk.getId() == ID)
                .filter(mk -> mk.getSubject().getId() == mark.getSubject().getId())
                .findAny();
        if (optionalMark.isEmpty()) {
            ID++;
            log.info("Добавлена новая оценка");
            markMap.put(ID, mark.withMark(ID));
            return mark;
        }
        log.error("Переданная оценка уже существует");
        return null;
    }

    @Override
    public Optional<Mark> getMarkByID(int id) {
        log.debug("Попытка найти оценку по ID");
        Optional<Mark> optionalMark = markMap.values()
                .stream()
                .filter(mk -> mk.getId() == id)
                .findAny();
        if (optionalMark.isPresent()) {
            log.info("Оценка найдена");
            return optionalMark;
        } else {
            log.error("Оценка не найдена");
            return Optional.empty();
        }
    }

    @Override
    public List<Mark> getAllMarks() {
        log.info("Берём все оценки из репозитория");
        return new ArrayList<>(markMap.values());
    }

    @Override
    public boolean updateSubjectMarkById(int id, Subject newSubject) {
        log.debug("Попытка взять оцеку по ID");
        Optional<Mark> optionalMark = markMap.values()
                .stream()
                .filter(mk -> id == mk.getId())
                .findAny();
        if (optionalMark.isPresent()) {
            log.info("Изменение оценки в репозитории");
            Mark markFromOptional = optionalMark.get();
            markFromOptional.setSubject(newSubject);
            markMap.put(id, markFromOptional);
            return markMap.containsValue(markFromOptional);
        }
        log.error("Оценка не найдена, изменений не произошло");
        return false;
    }

    @Override
    public boolean updateDateOfMarkById(int id, LocalDate newDateOfMark) {
        log.debug("Попытка взять оценку по ID");
        Optional<Mark> optionalMark = markMap.values()
                .stream()
                .filter(mk -> id == mk.getId())
                .findAny();
        if (optionalMark.isPresent()) {
            log.info("Изменение оценки в репозитории");
            Mark markFromOptional = optionalMark.get();
            markFromOptional.setDateOfMark(newDateOfMark);
            markMap.put(id, markFromOptional);
            return markMap.containsValue(markFromOptional);
        }
        log.error("Оценка не найдена, изменений не произошло");
        return false;
    }

    @Override
    public boolean updateGroupWhereMarkWasGiven(int id, Group newGroup) {
        log.debug("Попытка взять оценку по ID");
        Optional<Mark> optionalMark = markMap.values()
                .stream()
                .filter(mk -> id == mk.getId())
                .findAny();
        if (optionalMark.isPresent()) {
            log.info("Измнений оценки в репозитории");
            Mark markFromOptional = optionalMark.get();
            markFromOptional.setGroup(newGroup);
            markMap.put(id, markFromOptional);
            return markMap.containsValue(markFromOptional);
        }
        log.error("Оценка не найдена, изменений не произошло");
        return false;
    }

    @Override
    public boolean updateMarkById(int id, int newMark) {
        log.debug("Попытка взять оценку по ID");
        Optional<Mark> optionalMark = markMap.values()
                .stream()
                .filter(mk -> id == mk.getId())
                .findAny();
        if (optionalMark.isPresent()) {
            Mark markFromOptional = optionalMark.get();
            markFromOptional.setMark(newMark);
            markMap.put(id, markFromOptional);
            return markMap.containsValue(markFromOptional);
        }
        log.error("Оценка не найдена, изменений не произошло");
        return false;
    }

    @Override
    public boolean deleteMarkById(int id) {
        log.debug("Попытка взять оценку по ID");
        Optional<Mark> optionalMark = markMap.values()
                .stream()
                .filter(mk -> id == mk.getId())
                .findAny();
        if (optionalMark.isPresent()) {
            log.info("Удаление оценки из репозитория");
            Mark markFromOptional = optionalMark.get();
            return markMap.remove(id, markFromOptional);
        }
        log.error("Оценка не найдена, удаления не произошло");
        return false;
    }
}
