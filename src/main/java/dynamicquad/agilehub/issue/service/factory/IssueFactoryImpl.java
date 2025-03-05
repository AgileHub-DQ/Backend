package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.dto.IssueRequestDto.CreateIssue;
import dynamicquad.agilehub.issue.dto.IssueRequestDto.EditIssue;
import dynamicquad.agilehub.issue.dto.IssueResponseDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.ContentDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.IssueDetail;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.SubIssueDetail;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.issue.service.command.ImageService;
import dynamicquad.agilehub.issue.service.command.IssueNumberGenerator;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service // Model 계층, 다른 서비스계층에 종속되므로, @Component 대신 @Service 사용
public class IssueFactoryImpl implements IssueFactory {

    private final IssueRepository issueRepository;

    private final ImageService imageService;
    private final IssueNumberGenerator issueNumberGenerator;
    private final MemberService memberService;

    @Value("${aws.s3.workingDirectory.issue}")
    private String WORKING_DIRECTORY;

    @Override
    @Transactional
    public Long createIssue(CreateIssue request, Project project) {
        // 이슈 번호 생성
        String issueNumber = issueNumberGenerator.generate(project.getKey());

        // 멤버를 찾기
        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());
        Issue issue = toEntity(request, project, issueNumber, assignee);

        // 이슈 저장
        issueRepository.save(issue);

        // S3에 이미지 저장
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            log.info("uploading images");
            imageService.saveImages(issue, request.getFiles(), WORKING_DIRECTORY);
        }

        return issue.getId();
    }

    @Transactional
    @Override
    public Long updateIssue(Issue issue, Project project, EditIssue request) {
        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());
        issue.updateIssue(request, assignee);

        imageService.cleanupMismatchedImages(issue, request.getImageUrls(), WORKING_DIRECTORY);
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            imageService.saveImages(issue, request.getFiles(), WORKING_DIRECTORY);
        }
        return issue.getId();
    }

    private Issue toEntity(CreateIssue request, Project project, String issueNumber, Member assignee) {
        if (IssueType.EPIC.equals(request.getType())) {
            return Issue.createEpic(request, assignee, issueNumber, project);
        }
        else if (IssueType.STORY.equals(request.getType())) {
            return Issue.createStory(request, assignee, issueNumber, project);
        }
        else if (IssueType.TASK.equals(request.getType())) {
            return Issue.createTask(request, assignee, issueNumber, project);
        }

        throw new IllegalArgumentException("Invalid issue type");
    }


    @Override
    public ContentDto createContentDto(Issue issue) {
        return IssueResponseDto.ContentDto.from(issue);
    }

    @Override
    public IssueDetail createIssueDetail(Issue issue, ContentDto contentDto, AssigneeDto assigneeDto) {
        return IssueResponseDto.IssueDetail.from(issue, contentDto, assigneeDto);
    }

    @Override
    public SubIssueDetail createParentIssue(Issue issue) {
        Long parentIssueId = issue.getParentIssueId();

        if (parentIssueId == null) {
            return new IssueResponseDto.SubIssueDetail();
        }

        Issue parentIssue = issueRepository.findById(parentIssueId).orElseThrow(() -> {
            log.error("Parent issue not found. issueId: {}", parentIssueId);
            throw new GeneralException(ErrorStatus.ISSUE_NOT_FOUND);
        });

        AssigneeDto assigneeDto = AssigneeDto.from(parentIssue);
        return IssueResponseDto.SubIssueDetail.from(parentIssue, assigneeDto);
    }

    @Override
    public List<SubIssueDetail> createChildIssueDtos(Issue issue) {
        List<Issue> childIssues = issueRepository.findByParentIssueId(issue.getId());
        if (childIssues.isEmpty()) {
            return List.of();
        }

        return childIssues.stream()
            .map(childIssue -> {
                AssigneeDto assigneeDto = AssigneeDto.from(childIssue);
                return IssueResponseDto.SubIssueDetail.from(childIssue, assigneeDto);
            })
            .toList();
    }


}
