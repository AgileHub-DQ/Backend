package dynamicquad.agilehub.issue.controller;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.issue.controller.IssueResponse.IssueReadResponseDto;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.service.IssueQueryService;
import dynamicquad.agilehub.issue.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이슈", description = "이슈를 생성하고 조회합니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IssueController {

    private final IssueService issueService;
    private final IssueQueryService issueQueryService;

    @PostMapping(value = "/api/projects/{key}/issues", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이슈 생성", description = "프로젝트의 이슈를 생성합니다.",
        requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = IssueCreateRequest.class))))
    @ApiResponse(responseCode = "201", description = "이슈 생성 성공")
    public ResponseEntity<?> createProjectIssue(@Valid @ModelAttribute IssueCreateRequest request,
                                                @PathVariable("key") String key) {
        log.info("createProjectIssue key: {}", key);
        Long issueId = issueService.createIssue(key, request);

        return ResponseEntity.created(URI.create("/api/projects/" + key + "/issues/" + issueId))
            .body(CommonResponse.of(SuccessStatus.CREATED, issueId));
    }

    // 이슈 조회
    @GetMapping(value = "/api/projects/{key}/issues/{issueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이슈 조회", description = "프로젝트의 이슈를 조회합니다.")
    public CommonResponse<?> getProjectIssue(@PathVariable("key") String key, @PathVariable("issueId") Long issueId) {
        log.info("getProjectIssue key: {}, issueId: {}", key, issueId);
        IssueReadResponseDto issue = issueQueryService.getIssue(key, issueId);

        return CommonResponse.of(SuccessStatus.OK, issue);
    }
}
