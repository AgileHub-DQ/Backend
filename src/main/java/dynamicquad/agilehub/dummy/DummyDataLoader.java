package dynamicquad.agilehub.dummy;

import dynamicquad.agilehub.dummy.bulk.repository.IssueBulkRepository;
import dynamicquad.agilehub.dummy.bulk.repository.MemberBulkRepository;
import dynamicquad.agilehub.dummy.bulk.repository.MemberProjectBulkRepository;
import dynamicquad.agilehub.dummy.bulk.repository.ProjectBulkRepository;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueStatus;
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

    // 상수 정의 - 더 명확한 이름과 의미 부여
    private static final int PROJECT_COUNT = 10_000;
    private static final int PROJECTS_WITH_ISSUES = 10;
    private static final int ISSUES_PER_PROJECT = 3_000_000;
    private static final LocalDate ISSUE_START_DATE = LocalDate.of(2025, 1, 1);
    private static final LocalDate ISSUE_END_DATE = LocalDate.of(2025, 12, 31);

    @PostConstruct
    void bulkInsert() {
        log.info("더미 데이터 생성 시작");
        long startTime = System.currentTimeMillis();

        //insertProjects();
        //insertMembers();
        //insertMemberProjects();
        insertIssues();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        log.info("더미 데이터 생성 완료: 실행 시간 {}ms", executionTime);
    }

    private void insertProjects() {
        log.info("{}개의 프로젝트 생성 시작", PROJECT_COUNT);
        List<Project> projects = new ArrayList<>(PROJECT_COUNT);

        for (long i = 0; i < PROJECT_COUNT; i++) {
            projects.add(Project.builder()
                .name("Project" + i)
                .key("KEY" + i)
                .build());
        }

        projectBulkRepository.saveAll(projects);
        log.info("프로젝트 생성 완료");
    }

    private void insertMembers() {
        log.info("{}개의 회원 생성 시작", PROJECT_COUNT);
        List<Member> members = new ArrayList<>(PROJECT_COUNT);

        for (long i = 0; i < PROJECT_COUNT; i++) {
            members.add(Member.builder()
                .name("Member" + i)
                .status(MemberStatus.ACTIVE)
                .build());
        }

        memberBulkRepository.saveAll(members);
        log.info("회원 생성 완료");
    }

    private void insertMemberProjects() {
        log.info("{}개의 멤버-프로젝트 관계 생성 시작", PROJECT_COUNT);
        List<MemberProject> memberProjects = new ArrayList<>(PROJECT_COUNT);

        for (long i = 0; i < PROJECT_COUNT; i++) {
            memberProjects.add(MemberProject.builder()
                .role(MemberProjectRole.ADMIN)
                .build());
        }

        memberProjectBulkRepository.saveAll(memberProjects);
        log.info("멤버-프로젝트 관계 생성 완료");
    }

    private void insertIssues() {
        log.info("{}개 프로젝트에 각 {}개씩 이슈 생성 시작", PROJECTS_WITH_ISSUES, ISSUES_PER_PROJECT);

        insertIssuesForProject(3L);
        log.info("프로젝트 ID {}에 대한 이슈 생성 완료", 3L);

        log.info("모든 이슈 생성 완료");
    }

    private void insertIssuesForProject(long projectId) {

        long number = ISSUES_PER_PROJECT;

        for (long i = 0; i < 100; i++) {
            List<Issue> issues = new ArrayList<>(ISSUES_PER_PROJECT);
            for (int j = 0; j < number / 100; j++) {

                issues.add(Issue.builder()
                    .title("Issue" + i)
                    .content("Issue content" + i)
                    .number("ISSUE-" + i)
                    .status(IssueStatus.DO)
                    .issueType(IssueType.EPIC)
                    .startDate(ISSUE_START_DATE)
                    .endDate(ISSUE_END_DATE)
                    .build());
            }
            issueBulkRepository.saveAll(issues, projectId, projectId, projectId);
            issues.clear(); // 메모리 사용량 최적화
        }


    }
}