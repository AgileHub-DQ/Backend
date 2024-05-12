package dynamicquad.agilehub.comment.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class CommentRequest {

    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class CommentCreateRequest {

        @Schema(description = "댓글 내용. 사진불가능하고 오로지 글만 가능합니다. 빈칸으로 보내는 것도 불가능.", example = "댓글 내용")
        @NotBlank(message = "댓글 내용은 필수입니다.")
        private String content;

    }

}
