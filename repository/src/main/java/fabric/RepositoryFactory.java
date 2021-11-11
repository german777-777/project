package fabric;

import credential.CredentialRepository;
import group.GroupRepository;
import mark.MarkRepository;
import person.PersonRepository;
import salary.SalaryRepository;
import subject.SubjectRepository;

public interface RepositoryFactory {
    GroupRepository getGroupRepository();
    MarkRepository getMarkRepository();
    PersonRepository getPersonRepository();
    SalaryRepository getSalaryRepository();
    SubjectRepository getSubjectRepository();
    CredentialRepository getCredentialRepository();

}
