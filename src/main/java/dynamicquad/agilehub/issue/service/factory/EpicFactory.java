package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.story.StoryRepository;
import dynamicquad.agilehub.issue.service.ImageService;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("EPIC_FACTORY")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EpicFactory implements IssueFactory {

    private final IssueRepository issueRepository;
    private final StoryRepository storyRepository;
    private final ImageService imageService;

    private final MemberService memberService;

    @Value("${aws.s3.workingDirectory.issue}")
    private String WORKING_DIRECTORY;
    private String EPIC = "EPIC";
    private String STORY = "STORY";

    @Transactional
    @Override
    public Long createIssue(IssueCreateRequest request, Project project) {
        // TODO: 이슈가 삭제되면 이슈 번호가 중복될 수 있음 1번,2번,3번 이슈 생성뒤 2번 삭제하면 4번 이슈 생성시 3번이 되어 중복 [ ]
        // TODO: 이슈 번호 생성 로직을 따로 만들기 - number 최대로 큰 숫자 + 1로 로직 변경 [ ]
        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);

        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());

        // TODO: EPIC toEntity 메서드에 이 로직 넣기 [ ]
        Epic epic = Epic.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .number(issueNumber)
            .status(request.getStatus())
            .assignee(assignee)
            .project(project)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .build();

        issueRepository.save(epic);
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            log.info("uploading images");
            imageService.saveImages(epic, request.getFiles(), WORKING_DIRECTORY);
        }
        return epic.getId();
    }

    @Transactional
    @Override
    public Long updateIssue(Issue issue, Project project, IssueEditRequest request) {

        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());

        Epic epic = getEpic(issue);
        epic.updateEpic(request, assignee);
        imageService.cleanupMismatchedImages(epic, request.getImageUrls(), WORKING_DIRECTORY);
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            log.info("uploading images");
            imageService.saveImages(epic, request.getFiles(), WORKING_DIRECTORY);
        }
        return epic.getId();
    }

    @Override
    public ContentDto createContentDto(Issue issue) {

        return ContentDto.builder()
            .text(issue.getContent())
            .imagesURLs(issue.getImages().stream().map(Image::getPath).toList())
            .build();
    }

    @Override
    public IssueDto createIssueDto(Issue issue, ContentDto contentDto, AssigneeDto assigneeDto) {
        Epic epic = getEpic(issue);
        //TODO: IssueDto 클래스에 해당 부분 fromEntity 메서드로 만들기 [ ]
        return IssueDto.builder()
            .issueId(epic.getId())
            .key(epic.getProject().getKey() + "-" + epic.getNumber())
            .title(epic.getTitle())
            .type(EPIC)
            .status(String.valueOf(epic.getStatus()))
            .startDate(epic.getStartDate() == null ? "" : epic.getStartDate().toString())
            .endDate(epic.getEndDate() == null ? "" : epic.getEndDate().toString())
            .content(contentDto)
            .assignee(assigneeDto)
            .build();
    }

    @Override
    public SubIssueDto createParentIssueDto(Issue issue) {
        return new SubIssueDto();
    }

    @Override
    public List<SubIssueDto> createChildIssueDtos(Issue issue) {
        Epic epic = getEpic(issue);
        List<Story> stories = storyRepository.findByEpicId(epic.getId());
        if (stories.isEmpty()) {
            return List.of();
        }

        return stories.stream()
            .map(this::getStoryToSubIssueDto)
            .toList();
    }

    private SubIssueDto getStoryToSubIssueDto(Story story) {

        AssigneeDto assigneeDto = getAssigneeDto(story);

        return SubIssueDto.builder()
            .issueId(story.getId())
            .key(story.getProject().getKey() + "-" + story.getNumber())
            .status(String.valueOf(story.getStatus()))
            .type(STORY)
            .title(story.getTitle())
            .assignee(assigneeDto)
            .build();
    }

    private AssigneeDto getAssigneeDto(Story story) {
        return Optional.ofNullable(story.getAssignee())
            .map(assignee -> AssigneeDto.from(assignee.getId(), assignee.getName(), assignee.getProfileImageUrl()))
            .orElse(new AssigneeDto());
    }

    private Epic getEpic(Issue issue) {
        if (!(issue instanceof Epic epic)) {
            log.error("issue is not instance of Epic = {}", issue.getClass());
            throw new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND);
        }
        return epic;
    }


}
