package dynamicquad.agilehub.issue.comment;

import static dynamicquad.agilehub.issue.comment.request.CommentRequest.CommentCreateRequest;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.issue.comment.response.CommentResponse.CommentCreateResponse;
import dynamicquad.agilehub.issue.comment.response.CommentResponse.CommentUpdateResponse;
import dynamicquad.agilehub.issue.comment.service.CommentQueryService;
import dynamicquad.agilehub.issue.comment.service.CommentService;
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
                                           @PathVariable("issueId") Long issueId) {

        //TODO: 현재, 가짜 멤버 객체 1L로 설정한 상태이므로 추후에 제거하고 실제 멤버 객체를 받아오도록 수정 필요
        //TODO: 가짜 멤버 객체
        Long memberId = 1L;

        CommentCreateResponse resp = commentService.createComment(key, issueId, memberId, request.getContent());
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
                                           @PathVariable("commentId") Long commentId) {

        //TODO: 현재, 가짜 멤버 객체 1L로 설정한 상태이므로 추후에 제거하고 실제 멤버 객체를 받아오도록 수정 필요
        Long memberId = 1L;

        commentService.deleteComment(key, issueId, commentId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "코멘트 수정", description = "이슈의 코멘트를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "코멘트 수정 성공")
    @PutMapping(value = "/projects/{key}/issues/{issueId}/comments/{commentId}")
    public CommonResponse<?> updateComment(@PathVariable("key") String key,
                                           @PathVariable("issueId") Long issueId,
                                           @PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody CommentCreateRequest request) {

        //TODO: 현재, 가짜 멤버 객체 1L로 설정한 상태이므로 추후에 제거하고 실제 멤버 객체를 받아오도록 수정 필요
        Long memberId = 1L;

        CommentUpdateResponse resp = commentService.updateComment(key, issueId, commentId,
            request.getContent(), memberId);
        return CommonResponse.of(SuccessStatus.OK, resp);
    }
}
