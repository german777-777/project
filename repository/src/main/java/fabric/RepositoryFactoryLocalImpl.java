package fabric;

import credential.CredentialRepository;
import credential.CredentialRepositoryLocalImpl;
import group.GroupRepository;
import group.GroupRepositoryLocalImpl;
import mark.MarkRepository;
import mark.MarkRepositoryLocalImpl;
import person.PersonRepository;
import person.PersonRepositoryLocalImpl;
import salary.SalaryRepository;
import salary.SalaryRepositoryLocalImpl;
import subject.SubjectRepository;
import subject.SubjectRepositoryLocalImpl;

public class RepositoryFactoryLocalImpl implements RepositoryFactory {
    private final GroupRepository groupRepository;
    private final MarkRepository markRepository;
    private final PersonRepository personRepository;
    private final SalaryRepository salaryRepository;
    private final SubjectRepository subjectRepository;
    private final CredentialRepository credentialRepository;

    private static volatile RepositoryFactoryLocalImpl instance;

    private RepositoryFactoryLocalImpl() {
        groupRepository = GroupRepositoryLocalImpl.getInstance();
        markRepository = MarkRepositoryLocalImpl.getInstance();
        personRepository = PersonRepositoryLocalImpl.getInstance();
        salaryRepository = SalaryRepositoryLocalImpl.getInstance();
        subjectRepository = SubjectRepositoryLocalImpl.getInstance();
        credentialRepository = CredentialRepositoryLocalImpl.getInstance();
    }

    public static RepositoryFactoryLocalImpl getInstance() {
        if (instance == null) {
            synchronized (RepositoryFactoryLocalImpl.class) {
                if (instance == null) {
                    instance = new RepositoryFactoryLocalImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public GroupRepository getGroupRepository() {
        return groupRepository;
    }

    @Override
    public MarkRepository getMarkRepository() {
        return markRepository;
    }

    @Override
    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    @Override
    public SalaryRepository getSalaryRepository() {
        return salaryRepository;
    }

    @Override
    public SubjectRepository getSubjectRepository() {
        return subjectRepository;
    }

    @Override
    public CredentialRepository getCredentialRepository() {
        return credentialRepository;
    }

}
