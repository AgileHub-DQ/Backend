package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.util.PhotoS3Manager;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Image;
import dynamicquad.agilehub.issue.domain.ImageRepository;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
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
            saveImages(epic, photoS3Manager.uploadPhotos(request.getFiles(), WORKING_DIRECTORY));
        }

        return epic.getId();
    }

    private void saveImages(Epic epic, List<String> imagePath) {
        if (imagePath.isEmpty()) {
            return;
        }
        log.info("save images = {} ", imagePath);

        List<Image> images = imagePath.stream()
            .map(path -> Image.builder().path(path).issue(epic).build())
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
