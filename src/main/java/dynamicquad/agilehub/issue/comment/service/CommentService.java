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
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.ProjectValidator;
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


    // TODO: 코멘트를 작성한 멤버의 아이디를 인자로 받고 memberRepository에서 해당 멤버를 찾아온다음 Comment에 등록함. 체크 필요 [ ]
    @Transactional
    public CommentCreateResponse createComment(String key, Long issueId, Long memberId, String content) {

        Project project = projectValidator.findProject(key);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(project, issue);

        Member writer = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Comment comment = Comment.builder()
            .content(content)
            .writer(writer)
            .issue(issue)
            .build();

        return CommentCreateResponse.fromEntity(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(String key, Long issueId, Long commentId, Long memberId) {
        //TODO: 코멘트 삭제 시, 해당 코멘트를 작성한 멤버와 현재 로그인한 멤버가 같은지 확인 필요 [ ]
        Project project = projectValidator.findProject(key);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(project, issue);

        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentUpdateResponse updateComment(String key, Long issueId, Long commentId, String content,
                                               Long memberId) {
        //TODO: 코멘트 수정 시, 해당 코멘트를 작성한 멤버와 현재 로그인한 멤버가 같은지 확인 필요 [ ]
        Project project = projectValidator.findProject(key);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(project, issue);

        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        comment.updateComment(content);
        return CommentUpdateResponse.fromEntity(comment);
    }
}
