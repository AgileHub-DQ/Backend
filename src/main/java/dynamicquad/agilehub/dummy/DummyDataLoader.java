package dynamicquad.agilehub.dummy;

import dynamicquad.agilehub.dummy.bulkRepository.IssueBulkRepository;
import dynamicquad.agilehub.dummy.bulkRepository.MemberBulkRepository;
import dynamicquad.agilehub.dummy.bulkRepository.MemberProjectBulkRepository;
import dynamicquad.agilehub.dummy.bulkRepository.ProjectBulkRepository;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
@Slf4j
public class DummyDataLoader {
    private static final int COUNT = 10_000;
    private final ProjectBulkRepository projectBulkRepository;
    private final MemberBulkRepository memberBulkRepository;
    private final MemberProjectBulkRepository memberProjectBulkRepository;
    private final IssueBulkRepository issueBulkRepository;


    //@PostConstruct
    void bulkInsert() {

        // TODO: project 벌크 실행
        long startTime = System.currentTimeMillis();
        List<Project> projects = new ArrayList<>();
        for (long i = 0; i < COUNT; i++) {
            projects.add(Project.builder()
                .name("프로젝트" + i)
                .key("KEY" + i)
                .build());
        }

        projectBulkRepository.saveAll(projects);
        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");

    }

    //@PostConstruct
    void memberBulk() {
        // TODO: member 벌크 실행
        long startTime = System.currentTimeMillis();
        List<Member> members = new ArrayList<>();
        for (long i = 0; i < COUNT; i++) {
            members.add(Member.builder()
                .name("멤버" + i)
                .status(MemberStatus.ACTIVE)
                .build());
        }
        memberBulkRepository.saveAll(members);
        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");

    }


    //@PostConstruct
    void memberProjectBulk() {
        // TODO: memberProject 벌크 실행
        long startTime = System.currentTimeMillis();
        List<MemberProject> memberProjects = new ArrayList<>();
        for (long i = 0; i < COUNT; i++) {
            memberProjects.add(MemberProject.builder()
                .role(MemberProjectRole.ADMIN)
                .build());
        }
        memberProjectBulkRepository.saveAll(memberProjects);
        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");

    }

    //@PostConstruct
    void issueEpicBulk() {
        // TODO: issue 벌크 실행
        long startTime = System.currentTimeMillis();
        List<Epic> epics = new ArrayList<>();
        for (long i = 0; i < 1_000_000; i++) {
            epics.add(Epic.builder()
                .title("에픽" + i)
                .content("에픽 내용" + i)
                .number((int) i)
                .status(IssueStatus.DO)
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2021, 10, 23))
                .build());
        }
        issueBulkRepository.saveEpicAll(epics, 1L, 1L, 1L);
        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");
    }

    //@PostConstruct
    void epicIssueMappingBulk() {
        //먼저 생성된 이슈시작지점 아이디부터 확인하기
        long startTime = System.currentTimeMillis();
        List<Epic> epics = new ArrayList<>();
        for (long i = 0; i < 1_000_000; i++) {
            epics.add(Epic.builder()
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2021, 10, 23))
                .build());
        }
        issueBulkRepository.saveIssueEpicAll(epics);
        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");

    }

    //@PostConstruct
    void issueStoryBulk() {
        long startTime = System.currentTimeMillis();

        List<Story> stories = new ArrayList<>();
        for (long i = 0; i < 3_000_000L; i++) {
            stories.add(Story.builder()
                .title("스토리" + i)
                .content("스토리 내용" + i)
                .number((int) i)
                .status(IssueStatus.DO)
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2021, 10, 23))
                .build());
        }
        issueBulkRepository.saveStoryAll(stories, 1L, 1L, 1L);

        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");

    }

    //@PostConstruct
    void storyIssueMappingBulk() {
        //먼저 생성된 이슈시작지점 아이디부터 확인하기
        long startTime = System.currentTimeMillis();

        List<Story> stories = new ArrayList<>();
        for (long i = 0; i < 3_000_000L; i++) {

            stories.add(Story.builder()
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2021, 10, 23))
                .build());
        }
        issueBulkRepository.saveIssueStoryAll(stories, 10L, 1_000_001L);

        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");

    }

    //@PostConstruct
    void issueTaskBulk() {
        long startTime = System.currentTimeMillis();
        List<Task> tasks = new ArrayList<>();
        for (long i = 0; i < 100 * 200 * 200; i++) {
            tasks.add(Task.builder()
                .title("태스크" + i)
                .content("태스크 내용" + i)
                .number((int) i)
                .status(IssueStatus.DO)
                .build());
        }
        issueBulkRepository.saveTaskAll(tasks, 1L, 1L, 1L);

        long endTime = System.currentTimeMillis();
        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");
    }

    //@PostConstruct
    void taskIssueMappingBulk() {
        //먼저 생성된 이슈시작지점 아이디부터 확인하기
        //long startTime = System.currentTimeMillis();
        List<Task> tasks = new ArrayList<>();
        for (long i = 0; i < 100 * 200; i++) {
            tasks.add(Task.builder()
                .build());
        }
        issueBulkRepository.saveIssueTaskAll(tasks, 1_100_000L, 4_000_001L);

        //long endTime = System.currentTimeMillis();
/*        System.out.println("--------------------");
        System.out.println("수행시간 : " + (endTime - startTime) + "ms");
        System.out.println("--------------------");*/

    }

}
