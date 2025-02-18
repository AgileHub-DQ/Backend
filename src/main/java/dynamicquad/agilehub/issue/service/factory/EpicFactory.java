package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.SubIssueDetail;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.issue.repository.StoryRepository;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("EPIC_FACTORY")
@RequiredArgsConstructor
@Slf4j
public class EpicFactory implements IssueFactory {

    private final IssueRepository issueRepository;
    private final StoryRepository storyRepository;
    private final ImageService imageService;
    private final IssueNumberGenerator issueNumberGenerator;

    private final MemberService memberService;

    @Value("${aws.s3.workingDirectory.issue}")
    private String WORKING_DIRECTORY;

    @Transactional
    @Override
    public Long createIssue(IssueRequestDto.CreateIssue request, Project project) {

        // 이슈 번호 생성
        String issueNumber = issueNumberGenerator.generate(project.getKey());

        // 멤버를 찾기
        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());
        Epic epic = toEntity(request, project, issueNumber, assignee);

        // 이슈 저장
        issueRepository.save(epic);

        // S3에 이미지 저장
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            log.info("uploading images");
            imageService.saveImages(epic, request.getFiles(), WORKING_DIRECTORY);
        }
        return epic.getId();
    }

    @Transactional
    @Override
    public Long updateIssue(Issue issue, Project project, IssueRequestDto.EditIssue request) {

        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());

        Epic epic = Epic.extractFromIssue(issue);
        epic.updateEpic(request, assignee);
        imageService.cleanupMismatchedImages(epic, request.getImageUrls(), WORKING_DIRECTORY);
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            imageService.saveImages(epic, request.getFiles(), WORKING_DIRECTORY);
        }
        return epic.getId();
    }

    @Override
    public IssueResponseDto.ContentDto createContentDto(Issue issue) {
        return IssueResponseDto.ContentDto.from(issue);
    }

    @Override
    public IssueResponseDto.IssueDetail createIssueDetail(Issue issue, IssueResponseDto.ContentDto contentDto,
                                                          AssigneeDto assigneeDto) {
        return IssueResponseDto.IssueDetail.from(issue, contentDto, assigneeDto, IssueType.EPIC);
    }

    @Override
    public IssueResponseDto.SubIssueDetail createParentIssue(Issue issue) {
        return new IssueResponseDto.SubIssueDetail();
    }

    @Override
    public List<IssueResponseDto.SubIssueDetail> createChildIssueDtos(Issue issue) {
        Epic epic = Epic.extractFromIssue(issue);
        List<Story> stories = storyRepository.findByEpicId(epic.getId());
        if (stories.isEmpty()) {
            return List.of();
        }

        return stories.stream()
            .map(this::getStoryToSubIssue)
            .toList();
    }

    private SubIssueDetail getStoryToSubIssue(Story story) {
        AssigneeDto assigneeDto = AssigneeDto.from(story);
        return IssueResponseDto.SubIssueDetail.from(story, IssueType.STORY, assigneeDto);
    }


    private Epic toEntity(IssueRequestDto.CreateIssue request, Project project, String issueNumber, Member assignee) {
        return Epic.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .number(issueNumber)
            .status(request.getStatus())
            .label(request.getLabel())
            .assignee(assignee)
            .project(project)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .build();
    }


}
