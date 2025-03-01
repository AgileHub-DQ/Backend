package dynamicquad.agilehub.testSetUp;

import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataSetup {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final IssueRepository issueRepository;

    @Autowired
    public TestDataSetup(ProjectRepository projectRepository, MemberRepository memberRepository,
                         MemberProjectRepository memberProjectRepository, IssueRepository issueRepository) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.memberProjectRepository = memberProjectRepository;
        this.issueRepository = issueRepository;
    }

    public Project createAndSaveProject(String projectName, String key) {
        Project project = Project.builder()
            .name(projectName)
            .key(key)
            .build();
        projectRepository.save(project);
        return project;
    }

    public MemberProject createAndSaveMemberProject(Project project, Member member, MemberProjectRole role) {
        MemberProject memberProject = MemberProject.builder()
            .project(project)
            .member(member)
            .role(role)
            .build();

        memberProjectRepository.save(memberProject);
        return memberProject;
    }

    public Member createAndSaveMember(String name, MemberStatus status) {
        Member member = Member.builder()
            .name(name)
            .status(status)
            .profileImageUrl("www.naver.com")
            .build();

        memberRepository.save(member);

        return member;
    }


}
