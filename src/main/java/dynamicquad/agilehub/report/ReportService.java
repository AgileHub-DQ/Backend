package dynamicquad.agilehub.report;

import dynamicquad.agilehub.global.mail.service.EmailService;
import dynamicquad.agilehub.global.util.DateUtil;
import dynamicquad.agilehub.issue.service.query.IssueQueryService;
import dynamicquad.agilehub.issue.service.query.MonthlyReportDto;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportService {

    private final EmailService emailService;
    private final IssueQueryService issueQueryService;
    private final GptService gptService;

    private final MemberProjectService memberProjectService;
    private final ProjectQueryService projectQueryService;

    public void generateMonthlyReport(AuthMember authMember,
                                      ReportSummaryRequestDto.SummaryRequestMail summaryRequestMail) {

        Long projectId = summaryRequestMail.getProjectId();
        memberProjectService.validateMemberInProject(authMember.getId(), projectId);

        MonthlyReportDto reportDto = issueQueryService.getIssuesForMonth(DateUtil.getCurrentMonth(), projectId);
        StringBuilder report = new StringBuilder();
        report.append("Monthly Report\n");

        // 에픽 요약
        report.append("Epics:\n");
        List<String> epics = reportDto.getContentsByEpic();
        if (!epics.isEmpty()) {
            String epicSummaries = gptService.summarizeIssues(epics);
            report.append(epicSummaries).append("\n\n");
        }

        // 스토리 요약
        report.append("Stories:\n");
        List<String> stories = reportDto.getContentsByStory();
        if (!stories.isEmpty()) {
            String storySummaries = gptService.summarizeIssues(stories);
            report.append(storySummaries).append("\n\n");
        }

        // 작업 요약
        report.append("Tasks:\n");
        List<String> tasks = reportDto.getContentsByTask();
        if (!tasks.isEmpty()) {
            String taskSummaries = gptService.summarizeIssues(tasks);
            report.append(taskSummaries).append("\n\n");
        }

        Project project = projectQueryService.findProjectById(summaryRequestMail.getProjectId());
        SummaryEmailInfo emailInfo = SummaryEmailInfo.builder()
            .to(summaryRequestMail.getEmail())
            .subject("Monthly Report")
            .projectName(project.getName())
            .report(report.toString())
            .build();

        emailService.sendMail(emailInfo, "reportSummary");
    }
}
