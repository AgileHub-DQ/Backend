package dynamicquad.agilehub.issue.service;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.issue.service.command.IssueService;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class IssueServiceTest {

    @Autowired
    private IssueService issueService;

    @PersistenceContext
    EntityManager em;

    @Transactional
    @Test
    void 에픽이_지워지면_스토리_테스크_모두_지워진다() {
        // given
        Project project1 = createProject("프로젝트1", "project12311");
        em.persist(project1);

        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);
        Epic epic2P1 = createEpic("에픽2", "에픽2 내용", project1);
        em.persist(epic2P1);
        Story story1P1 = createStory("스토리1", "스토리1 내용", project1, epic2P1);
        em.persist(story1P1);
        Story story2P1 = createStory("스토리2", "스토리2 내용", project1, epic2P1);
        em.persist(story2P1);

        Member member = Member.builder().name("member").build();
        em.persist(member);

        MemberProject memberProject = MemberProject.builder()
            .member(member)
            .project(project1)
            .role(MemberProjectRole.ADMIN)
            .build();
        em.persist(memberProject);

        AuthMember authMember = AuthMember.builder()
            .id(member.getId())
            .name("member")
            .build();

        // when
        issueService.deleteIssue(project1.getKey(), epic2P1.getId(), authMember);
        em.flush();
        em.clear();
        // then
        assertThat(em.find(Epic.class, epic2P1.getId())).isNull();
        assertThat(em.find(Story.class, story1P1.getId())).isNull();
        assertThat(em.find(Story.class, story2P1.getId())).isNull();
    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }

    private Epic createEpic(String title, String content, Project project) {
        return Epic.builder()
            .title(title)
            .content(content)
            .status(IssueStatus.DO)
            .project(project)
            .build();
    }

    private Story createStory(String title, String content, Project project, Epic epic) {
        return Story.builder()
            .title(title)
            .content(content)
            .epic(epic)
            .status(IssueStatus.DO)
            .project(project)
            .build();
    }

    private Task createTask(String title, String content, Project project) {
        return Task.builder()
            .title(title)
            .content(content)
            .status(IssueStatus.DO)
            .project(project)
            .build();
    }
}