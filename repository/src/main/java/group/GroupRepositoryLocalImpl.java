package group;

import lombok.extern.slf4j.Slf4j;
import secondary.Group;

import java.util.*;

@Slf4j
public class GroupRepositoryLocalImpl implements GroupRepository {
    private static int ID = 0;
    private final Map<Integer, Group> groupMap = new HashMap<>();
    private static volatile GroupRepositoryLocalImpl instance;

    private GroupRepositoryLocalImpl() {
    }

    public static GroupRepositoryLocalImpl getInstance() {
        if (instance == null) {
            synchronized (GroupRepositoryLocalImpl.class) {
                if (instance == null) {
                    instance = new GroupRepositoryLocalImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Group createGroup(Group group) {
        log.debug("Попытка найти в репозитории переданную группу");
        Optional<Group> optionalGroup = groupMap.values()
                .stream()
                .filter(gr -> gr.getId() == ID)
                .filter(gr -> gr.getName().equals(group.getName()))
                .filter(gr -> gr.getTeacher().equals(group.getTeacher()))
                .findAny();
        if (optionalGroup.isEmpty()) {
            ID++;
            log.info("Добавлена новая группа");
            groupMap.put(ID, group.withId(ID));
            return group;
        }
        log.error("Переданная группа уже существует");
        return null;
    }

    @Override
    public Optional<Group> getGroupById(int id) {
        log.debug("Попытка взять группу по ID");
        Optional<Group> optionalGroup = groupMap.values()
                .stream()
                .filter(group -> id == group.getId())
                .findAny();
        if (optionalGroup.isPresent()) {
            log.info("Берём группу из репозитория");
            return optionalGroup;
        }
        log.error("Группа не найдена");
        return Optional.empty();
    }

    @Override
    public Optional<Group> getGroupByName(String name) {
        log.debug("Попытка взять группу по имени");
        Optional<Group> optionalGroup = groupMap.values()
                .stream()
                .filter(group -> name.equals(group.getName()))
                .findAny();
        if (optionalGroup.isPresent()) {
            log.info("Берём группу из репозитория");
            return optionalGroup;
        }
        log.error("Группа не найдена");
        return Optional.empty();
    }

    @Override
    public List<Group> getAllGroups() {
        log.info("Берём все группы из репозитория");
        return new ArrayList<>(groupMap.values());
    }

    @Override
    public boolean updateGroupNameById(int id, String newName) {
        log.debug("Попытка взять группу по ID");
        Optional<Group> optionalGroup = groupMap.values()
                .stream()
                .filter(gr -> id == gr.getId())
                .findAny();
        if (optionalGroup.isPresent()) {
            log.info("Изменение группы в репозитории");
            Group groupFromOptional = optionalGroup.get();
            groupFromOptional.setName(newName);
            groupMap.put(id, groupFromOptional);
            return groupMap.containsValue(groupFromOptional);
        }
        log.error("Группа не найдена, изменений не произошло");
        return false;
    }

    @Override
    public boolean deleteGroupById(int id) {
        log.debug("Попытка взять группу по ID");
        Optional<Group> optionalGroup = groupMap.values()
                .stream()
                .filter(gr -> id == gr.getId())
                .findAny();
        if (optionalGroup.isPresent()) {
            log.info("Удаление группы из репозитория");
            Group groupFromOptional = optionalGroup.get();
            return groupMap.remove(id, groupFromOptional);
        }
        log.error("Группа не найдена, удаления не произошло");
        return false;
    }
}
