package fabric;

import credential.CredentialRepository;
import credential.CredentialRepositoryJpaImpl;
import group.GroupRepository;
import group.GroupRepositoryJpaImpl;
import mark.MarkRepository;
import mark.MarkRepositoryJpaImpl;
import org.hibernate.SessionFactory;
import person.PersonRepository;
import person.PersonRepositoryJpaImpl;
import salary.SalaryRepository;
import salary.SalaryRepositoryJpaImpl;
import subject.SubjectRepository;
import subject.SubjectRepositoryJpaImpl;

public class RepositoryFactoryJpaImpl implements RepositoryFactory {

    private final GroupRepository groupRepository;
    private final MarkRepository markRepository;
    private final PersonRepository personRepository;
    private final SalaryRepository salaryRepository;
    private final SubjectRepository subjectRepository;
    private final CredentialRepository credentialRepository;

    private static volatile RepositoryFactoryJpaImpl instance;

    private RepositoryFactoryJpaImpl(SessionFactory factory) {
        groupRepository = GroupRepositoryJpaImpl.getInstance(factory);
        markRepository = MarkRepositoryJpaImpl.getInstance(factory);
        personRepository = PersonRepositoryJpaImpl.getInstance(factory);
        salaryRepository = SalaryRepositoryJpaImpl.getInstance(factory);
        subjectRepository = SubjectRepositoryJpaImpl.getInstance(factory);
        credentialRepository = CredentialRepositoryJpaImpl.getInstance(factory);
    }

    public static RepositoryFactoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (RepositoryFactoryJpaImpl.class) {
                if (instance == null) {
                    instance = new RepositoryFactoryJpaImpl(factory);
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
