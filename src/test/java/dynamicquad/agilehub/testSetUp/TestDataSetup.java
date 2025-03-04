package dynamicquad.agilehub.testSetUp;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueLabel;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import java.time.LocalDate;
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


    public Issue createIssue(Project testProject, String title, IssueType issueType, Member assignee) {
        Issue issue = Issue.builder()
            .project(testProject)
            .title(title)
            .label(IssueLabel.TEST)
            .content("content")
            .startDate(LocalDate.of(2021, 1, 1))
            .endDate(LocalDate.of(2021, 1, 2))
            .number("TEST-X")
            .issueType(issueType)
            .assignee(assignee)
            .status(IssueStatus.DO)
            .build();

        return issue;
    }
}
