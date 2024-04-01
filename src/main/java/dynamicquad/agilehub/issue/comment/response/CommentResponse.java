package dynamicquad.agilehub.issue.comment.response;

import dynamicquad.agilehub.issue.comment.domain.Comment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class CommentResponse {

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class CommentCreateResponse {
        private Long id;
        private String content;
        private Long issueId;
        private Long writerId;
        private String writerName;
        private LocalDateTime createdAt;


        // TODO: 코멘트에 작성한 멤버의 아이디와 이름을 추출 하고 있으니 확인 바람. 체크 필요 [ ]
        public static CommentCreateResponse fromEntity(Comment comment) {
            return CommentCreateResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .issueId(comment.getIssue().getId())
                .writerId(comment.getWriter().getId())
                .writerName(comment.getWriter().getName())
                .createdAt(comment.getCreatedAt())
                .build();
        }
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class CommentReadResponse {
        private Long id;
        private String content;
        private Long issueId;
        private Long writerId;
        private String writerName;
        private LocalDateTime createdAt;

        // TODO: 코멘트에 작성한 멤버의 아이디와 이름을 추출 하고 있으니 확인 바람. 체크 필요 [ ]
        public static CommentReadResponse fromEntity(Comment comment) {
            return CommentReadResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .issueId(comment.getIssue().getId())
                .writerId(comment.getWriter().getId())
                .writerName(comment.getWriter().getName())
                .createdAt(comment.getCreatedAt())
                .build();
        }
    }
}
