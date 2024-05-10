package dynamicquad.agilehub.comment;

import static dynamicquad.agilehub.comment.request.CommentRequest.CommentCreateRequest;

import dynamicquad.agilehub.comment.response.CommentResponse.CommentCreateResponse;
import dynamicquad.agilehub.comment.response.CommentResponse.CommentUpdateResponse;
import dynamicquad.agilehub.comment.service.CommentQueryService;
import dynamicquad.agilehub.comment.service.CommentService;
import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이슈 댓글", description = "댓글 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;

    @Operation(summary = "코멘트 생성", description = "이슈에 코멘트를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "코멘트 생성 성공",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentCreateResponse.class))
    )
    @PostMapping(value = "/projects/{key}/issues/{issueId}/comments")
    public CommonResponse<?> createComment(@Valid @RequestBody CommentCreateRequest request,
                                           @PathVariable("key") String key,
                                           @PathVariable("issueId") Long issueId,
                                           @Auth AuthMember authMember) {

        CommentCreateResponse resp = commentService.createComment(key, issueId, request.getContent(), authMember);
        return CommonResponse.of(SuccessStatus.CREATED, resp);
    }

    @Operation(summary = "코멘트 조회", description = "이슈의 코멘트를 조회합니다.")
    @GetMapping(value = "/projects/{key}/issues/{issueId}/comments")
    public CommonResponse<?> getComments(@PathVariable("key") String key,
                                         @PathVariable("issueId") Long issueId) {

        return CommonResponse.of(SuccessStatus.OK, commentQueryService.getComments(key, issueId));
    }

    @Operation(summary = "코멘트 삭제", description = "이슈의 코멘트를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "코멘트 삭제 성공")
    @DeleteMapping(value = "/projects/{key}/issues/{issueId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("key") String key,
                                           @PathVariable("issueId") Long issueId,
                                           @PathVariable("commentId") Long commentId,
                                           @Auth AuthMember authMember) {

        commentService.deleteComment(key, issueId, commentId, authMember);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "코멘트 수정", description = "이슈의 코멘트를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "코멘트 수정 성공")
    @PutMapping(value = "/projects/{key}/issues/{issueId}/comments/{commentId}")
    public CommonResponse<?> updateComment(@PathVariable("key") String key,
                                           @PathVariable("issueId") Long issueId,
                                           @PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody CommentCreateRequest request,
                                           @Auth AuthMember authMember) {

        CommentUpdateResponse resp = commentService.updateComment(key, issueId, commentId,
            request.getContent(), authMember);
        return CommonResponse.of(SuccessStatus.OK, resp);
    }
}
