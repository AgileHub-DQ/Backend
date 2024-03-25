package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.util.PhotoS3Manager;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component("EPIC_FACTORY")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EpicFactory implements IssueFactory {

    private final PhotoS3Manager photoS3Manager;

    private final MemberProjectRepository memberProjectRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;

    @Transactional
    @Override
    public Long createIssue(IssueCreateRequest request, Project project) {

        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);
        Member assignee = findMember(request.getAssigneeId(), project.getId());
        List<String> imagePath = getImagePath(request.getFiles());

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
        return epic.getId();
    }

    private List<String> getImagePath(List<MultipartFile> files) {
        return photoS3Manager.uploadPhotos(files);
    }

    private Member findMember(Long assigneeId, Long projectId) {
        Member member = memberRepository.findById(assigneeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        memberProjectRepository.findByMemberIdAndProjectId(member.getId(), projectId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_IN_PROJECT));

        return member;
    }


}
