package dynamicquad.agilehub.issue.comment.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.comment.domain.Comment;
import dynamicquad.agilehub.issue.comment.domain.CommentRepository;
import dynamicquad.agilehub.issue.comment.response.CommentResponse.CommentCreateResponse;
import dynamicquad.agilehub.issue.comment.response.CommentResponse.CommentUpdateResponse;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.service.IssueValidator;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectValidator;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {


    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    private final ProjectValidator projectValidator;
    private final IssueValidator issueValidator;
    private final MemberProjectService memberProjectService;


    @Transactional
    public CommentCreateResponse createComment(String key, Long issueId, String content, AuthMember authMember) {

        validate(key, issueId, authMember);
        Issue issue = issueValidator.findIssue(issueId);

        Member writer = memberRepository.findById(authMember.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Comment comment = Comment.builder()
            .content(content)
            .writer(writer)
            .issue(issue)
            .build();

        return CommentCreateResponse.fromEntity(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(String key, Long issueId, Long commentId, AuthMember authMember) {

        validate(key, issueId, authMember);

        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));
        validateMemberMatch(authMember, comment);

        commentRepository.delete(comment);
    }


    @Transactional
    public CommentUpdateResponse updateComment(String key, Long issueId, Long commentId, String content,
                                               AuthMember authMember) {

        validate(key, issueId, authMember);

        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));
        validateMemberMatch(authMember, comment);

        comment.updateComment(content);
        return CommentUpdateResponse.fromEntity(comment);
    }

    private void validateMemberMatch(AuthMember authMember, Comment comment) {
        Member writer = comment.getWriter();
        if (!Objects.equals(writer.getId(), authMember.getId())) {
            throw new GeneralException(ErrorStatus.COMMENT_WRITER_MISS_MATCH);
        }
    }

    private void validate(String key, Long issueId, AuthMember authMember) {
        Project project = projectValidator.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());
        issueValidator.validateIssueInProject(project.getId(), issueId);
    }

}
