package fabric;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import credential.CredentialRepository;
import credential.CredentialRepositoryPostgresImpl;
import group.GroupRepository;
import group.GroupRepositoryPostgresImpl;
import mark.MarkRepository;
import mark.MarkRepositoryPostgresImpl;
import person.PersonRepository;
import person.PersonRepositoryPostgresImpl;
import salary.SalaryRepository;
import salary.SalaryRepositoryPostgresImpl;
import subject.SubjectRepository;
import subject.SubjectRepositoryPostgresImpl;

public class RepositoryFactoryPostgresImpl implements RepositoryFactory {
    private final GroupRepository groupRepository;
    private final MarkRepository markRepository;
    private final PersonRepository personRepository;
    private final SalaryRepository salaryRepository;
    private final SubjectRepository subjectRepository;
    private final CredentialRepository credentialRepository;

    private static volatile RepositoryFactoryPostgresImpl instance;

    private RepositoryFactoryPostgresImpl(ComboPooledDataSource pool) {
        groupRepository = GroupRepositoryPostgresImpl.getInstance(pool);
        markRepository = MarkRepositoryPostgresImpl.getInstance(pool);
        personRepository = PersonRepositoryPostgresImpl.getInstance(pool);
        salaryRepository = SalaryRepositoryPostgresImpl.getInstance(pool);
        subjectRepository = SubjectRepositoryPostgresImpl.getInstance(pool);
        credentialRepository = CredentialRepositoryPostgresImpl.getInstance(pool);
    }

    public static RepositoryFactoryPostgresImpl getInstance(ComboPooledDataSource pool) {
        if (instance == null) {
            synchronized (RepositoryFactoryPostgresImpl.class) {
                if (instance == null) {
                    instance = new RepositoryFactoryPostgresImpl(pool);
                }
            }
        }
        return instance;
    }

    public GroupRepository getGroupRepository() {
        return groupRepository;
    }

    public MarkRepository getMarkRepository() {
        return markRepository;
    }

    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    public SalaryRepository getSalaryRepository() {
        return salaryRepository;
    }

    public SubjectRepository getSubjectRepository() {
        return subjectRepository;
    }

    public CredentialRepository getCredentialRepository() {
        return credentialRepository;
    }
}
