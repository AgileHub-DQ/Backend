package dynamicquad.agilehub.dummy;

import dynamicquad.agilehub.dummy.bulkRepository.IssueBulkRepository;
import dynamicquad.agilehub.dummy.bulkRepository.MemberBulkRepository;
import dynamicquad.agilehub.dummy.bulkRepository.MemberProjectBulkRepository;
import dynamicquad.agilehub.dummy.bulkRepository.ProjectBulkRepository;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dummy")
@Slf4j
public class DummyDataLoader {
    private final ProjectBulkRepository projectBulkRepository;
    private final MemberBulkRepository memberBulkRepository;
    private final MemberProjectBulkRepository memberProjectBulkRepository;
    private final IssueBulkRepository issueBulkRepository;


    @PostConstruct
    void bulkInsert() {

        // project 벌크 실행
        long startTime = System.currentTimeMillis();

        projectBulk();
        memberBulk();
        memberProjectBulk();
        epicBulk();
        epicIssueBulk();
        storyBulk();
        storyIssueBulk();
        taskBulk();
        taskIssueBulk();

        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");

    }


    private void projectBulk() {
        List<Project> projects = new ArrayList<>();
        for (long i = 0; i < 10_000L; i++) {
            projects.add(Project.builder()
                .name("프로젝트" + i)
                .key("KEY" + i)
                .build());
        }

        projectBulkRepository.saveAll(projects);
    }

    private void memberBulk() {
        List<Member> members = new ArrayList<>();
        for (long i = 0; i < 10_000L; i++) {
            members.add(Member.builder()
                .name("멤버" + i)
                .status(MemberStatus.ACTIVE)
                .build());
        }
        memberBulkRepository.saveAll(members);
    }

    private void memberProjectBulk() {
        // memberProject 벌크 실행
        List<MemberProject> memberProjects = new ArrayList<>();
        for (long i = 0; i < 10_000L; i++) {
            memberProjects.add(MemberProject.builder()
                .role(MemberProjectRole.ADMIN)
                .build());
        }
        memberProjectBulkRepository.saveAll(memberProjects);
    }

    private void epicBulk() {
        List<Issue> epics = new ArrayList<>();
        for (long i = 0; i < 100; i++) {
            epics.add(Epic.builder()
                .title("에픽" + i)
                .content("에픽 내용" + i)
                .number("")
                .status(IssueStatus.DO)
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2021, 10, 23))
                .build());
        }
        issueBulkRepository.saveEpicAll(epics, 1L, 1L, 1L);
    }

    private void epicIssueBulk() {
        // epic-issue 매핑 벌크 실행
        List<Epic> epicIssueMappings = new ArrayList<>();
        for (long i = 0; i < 100; i++) {
            epicIssueMappings.add(Epic.builder()
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2021, 10, 23))
                .build());
        }
        issueBulkRepository.saveIssueEpicAll(epicIssueMappings);
    }


    private void storyBulk() {
        List<Issue> stories = new ArrayList<>();
        for (long i = 0; i < 100 * 200; i++) {
            stories.add(Story.builder()
                .title("스토리" + i)
                .content("스토리 내용" + i)
                .number("")
                .status(IssueStatus.DO)
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2021, 10, 23))
                .build());
        }
        issueBulkRepository.saveStoryAll(stories, 1L, 1L, 1L);
    }

    private void storyIssueBulk() {
        // story-issue 매핑 벌크 실행
        for (long i = 0; i < 100; i++) {
            List<Story> storiesMappingIssue = new ArrayList<>();
            for (long j = 0; j < 200; j++) {
                storiesMappingIssue.add(Story.builder()
                    .startDate(LocalDate.of(2021, 1, 1))
                    .endDate(LocalDate.of(2021, 10, 23))
                    .build());
            }

            issueBulkRepository.saveIssueStoryAll(storiesMappingIssue, i + 1, 101L + i * 200L);
        }
    }

    private void taskBulk() {
        List<Issue> tasks = new ArrayList<>();
        for (long i = 0; i < 100 * 200 * 200; i++) {
            tasks.add(Task.builder()
                .title("태스크" + i)
                .content("태스크 내용" + i)
                .number("")
                .status(IssueStatus.DO)
                .build());
        }
        issueBulkRepository.saveTaskAll(tasks, 1L, 1L, 1L);
    }

    private void taskIssueBulk() {
        // task-issue 매핑 벌크 실행
        for (long i = 0; i < 100 * 200; i++) {
            List<Task> tasksMappingIssue = new ArrayList<>();
            for (long j = 0; j < 200; j++) {
                tasksMappingIssue.add(Task.builder()
                    .build());
            }

            issueBulkRepository.saveIssueTaskAll(tasksMappingIssue, 101L + i, 20101L + i * 200L);
        }

    }


}
