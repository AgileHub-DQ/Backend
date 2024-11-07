package dynamicquad.agilehub.issue.controller;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.IssueAndSubIssueDetail;
import dynamicquad.agilehub.issue.service.command.IssueService;
import dynamicquad.agilehub.issue.service.query.IssueQueryService;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이슈", description = "이슈를 생성하고 조회합니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IssueController {

    private final IssueService issueService;
    private final IssueQueryService issueQueryService;

    @Operation(summary = "이슈 생성", description = "프로젝트의 이슈를 생성합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = IssueRequestDto.CreateIssue.class))))
    @ApiResponse(responseCode = "201", description = "이슈 생성 성공")
    @PostMapping(value = "/projects/{key}/issues", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProjectIssue(@Valid @ModelAttribute IssueRequestDto.CreateIssue request,
                                                @PathVariable("key") String key,
                                                @Auth AuthMember authMember) {
        Long issueId = issueService.createIssue(key, request, authMember);

        return ResponseEntity.created(URI.create("/projects/" + key + "/issues/" + issueId))
            .body(CommonResponse.of(SuccessStatus.CREATED, issueId));
    }


    @Operation(summary = "이슈 상세 조회", description = "프로젝트의 이슈를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "이슈 조회 성공", content = @Content(schema = @Schema(implementation = IssueAndSubIssueDetail.class)))}
    )
    @GetMapping(value = "/projects/{key}/issues/{issueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getProjectIssue(@PathVariable("key") String key, @PathVariable("issueId") Long issueId,
                                             @Auth AuthMember authMember) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getIssue(key, issueId, authMember));
    }


    @Operation(summary = "이슈 수정", description = "프로젝트의 이슈를 수정합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = IssueRequestDto.EditIssue.class))))
    @ApiResponse(responseCode = "204", description = "이슈 수정 성공")
    @PutMapping(value = "/projects/{key}/issues/{issueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editProjectIssue(@Valid @ModelAttribute IssueRequestDto.EditIssue request,
                                              @PathVariable("key") String key, @PathVariable("issueId") Long issueId,
                                              @Auth AuthMember authMember) {

        try {
            issueService.updateIssue(key, issueId, request, authMember);
            return ResponseEntity.ok().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            String viewIssueUrl = "/projects/" + key + "/issues/" + issueId;

            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponse.of(ErrorStatus.OPTIMISTIC_LOCK_EXCEPTION, viewIssueUrl));
        }

    }


    @Operation(summary = "이슈 상태 수정", description = "프로젝트의 이슈 상태를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "이슈 상태 수정 성공")
    @PutMapping(value = "/projects/{key}/issues/{issueId}/status")
    public CommonResponse<?> editProjectIssueStatus(
        @Valid @RequestBody IssueRequestDto.EditIssueStatus request,
        @PathVariable("key") String key,
        @PathVariable("issueId") Long issueId,
        @Auth AuthMember authMember
    ) {

        issueService.updateIssueStatus(key, issueId, authMember, request.getStatus());
        return CommonResponse.of(SuccessStatus.OK, issueId);
    }

    @Operation(summary = "이슈 삭제", description = "프로젝트의 이슈를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "이슈 삭제 성공")
    @DeleteMapping(value = "/projects/{key}/issues/{issueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProjectIssue(@PathVariable("key") String key,
                                                @PathVariable("issueId") Long issueId,
                                                @Auth AuthMember authMember) {

        issueService.deleteIssue(key, issueId, authMember);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "이슈 기간 수정", description = "프로젝트의 이슈 기간을 수정합니다.")
    @ApiResponse(responseCode = "204", description = "이슈 기간 수정 성공")
    @PutMapping(value = "/projects/{key}/issues/{issueId}/period")
    public CommonResponse<?> editProjectIssuePeriod(
        @Valid @RequestBody IssueRequestDto.EditIssuePeriod request,
        @PathVariable("key") String key,
        @PathVariable("issueId") Long issueId,
        @Auth AuthMember authMember
    ) {

        issueService.updateIssuePeriod(key, issueId, authMember, request);
        return CommonResponse.of(SuccessStatus.OK, issueId);
    }

}
