package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.controller.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.story.StoryRepository;
import dynamicquad.agilehub.issue.service.ImageService;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.Project;
import java.time.LocalDate;
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

    private final MemberProjectRepository memberProjectRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;
    private final StoryRepository storyRepository;

    private final ImageService imageService;

    @Value("${aws.s3.workingDirectory.issue}")
    private String WORKING_DIRECTORY;
    private String EPIC = "EPIC";

    @Transactional
    @Override
    public Long createIssue(IssueCreateRequest request, Project project) {

        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);
        Member assignee = findMember(request.getAssigneeId(), project.getId());

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

        return IssueDto.builder()
            .issueId(epic.getId())
            .key(epic.getProject().getKey() + "-" + epic.getNumber())
            .title(epic.getTitle())
            .type(EPIC)
            .status(String.valueOf(epic.getStatus()))
            .startDate(Optional.ofNullable(epic.getStartDate()).map(LocalDate::toString).orElse("시작 날짜 미정"))
            .endDate(Optional.ofNullable(epic.getEndDate()).map(LocalDate::toString).orElse("종료 날짜 미정"))
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
        log.info("createChildIssueDtos EPIC 하위이슈인 STORY 모두 가져오기");
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

        AssigneeDto assigneeDto = Optional.ofNullable(story.getAssignee())
            .map(assignee -> AssigneeDto.builder()
                .id(assignee.getId())
                .name(assignee.getName())
                .build())
            .orElse(new AssigneeDto());

        return SubIssueDto.builder()
            .issueId(story.getId())
            .key(story.getProject().getKey() + "-" + story.getNumber())
            .status(story.getStatus().toString())
            .type("STORY")
            .title(story.getTitle())
            .assignee(assigneeDto)
            .build();
    }

    private Epic getEpic(Issue issue) {
        if (!(issue instanceof Epic epic)) {
            log.error("issue is not instance of Epic = {}", issue.getClass());
            throw new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND);
        }
        return epic;
    }

    private Member findMember(Long assigneeId, Long projectId) {

        if (assigneeId == null) {
            return null;
        }

        Member member = memberRepository.findById(assigneeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        memberProjectRepository.findByMemberIdAndProjectId(member.getId(), projectId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_IN_PROJECT));

        return member;
    }


}
