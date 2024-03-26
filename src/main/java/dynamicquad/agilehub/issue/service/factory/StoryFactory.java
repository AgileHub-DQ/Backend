package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.util.PhotoS3Manager;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.issue.domain.image.ImageRepository;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("STORY_FACTORY")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoryFactory implements IssueFactory {

    private final PhotoS3Manager photoS3Manager;

    private final MemberProjectRepository memberProjectRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;
    private final ImageRepository imageRepository;

    @Value("${aws.s3.workingDirectory.issue}")
    private String WORKING_DIRECTORY;

    @Transactional
    @Override
    public Long createIssue(IssueCreateRequest request, Project project) {

        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);
        Member assignee = findMember(request.getAssigneeId(), project.getId());
        Epic upEpic = retrieveEpicFromParentIssue(request.getParentId());

        Story story = Story.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .number(issueNumber)
            .status(request.getStatus())
            .assignee(assignee)
            .project(project)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .epic(upEpic)
            .build();

        issueRepository.save(story);

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            saveImages(story, photoS3Manager.uploadPhotos(request.getFiles(), WORKING_DIRECTORY));
        }

        return story.getId();
    }

    public Epic retrieveEpicFromParentIssue(Long parentId) {
        if (parentId == null) {
            return null;
        }
        validateParentIssue(parentId);
        return (Epic) issueRepository.findById(parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_FOUND));

    }

    private void validateParentIssue(Long parentId) {
        String type = issueRepository.findIssueTypeById(parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_FOUND));

        if (!IssueType.EPIC.equals(IssueType.valueOf(type))) {
            throw new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_EPIC);
        }

    }

    private void saveImages(Story story, List<String> imagePath) {
        if (imagePath.isEmpty()) {
            return;
        }

        List<Image> images = imagePath.stream()
            .map(path -> Image.builder().path(path).build().setIssue(story))
            .toList();

        imageRepository.saveAll(images);
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
