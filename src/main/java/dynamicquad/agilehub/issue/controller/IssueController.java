package dynamicquad.agilehub.issue.controller;

import static dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.response.IssueHierarchyResponse;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueReadResponseDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이슈", description = "이슈를 생성하고 조회합니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IssueController {

    private final IssueService issueService;
    private final IssueQueryService issueQueryService;

    @Operation(summary = "이슈 생성", description = "프로젝트의 이슈를 생성합니다.",
        requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = IssueCreateRequest.class))))
    @ApiResponse(responseCode = "201", description = "이슈 생성 성공")
    @PostMapping(value = "/api/projects/{key}/issues", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProjectIssue(@Valid @ModelAttribute IssueCreateRequest request,
                                                @PathVariable("key") String key) {
        Long issueId = issueService.createIssue(key, request);

        return ResponseEntity.created(URI.create("/api/projects/" + key + "/issues/" + issueId))
            .body(CommonResponse.of(SuccessStatus.CREATED, issueId));
    }


    @Operation(summary = "이슈 조회", description = "프로젝트의 이슈를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "이슈 조회 성공", content = @Content(schema = @Schema(implementation = IssueReadResponseDto.class)))}
    )
    @GetMapping(value = "/api/projects/{key}/issues/{issueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getProjectIssue(@PathVariable("key") String key, @PathVariable("issueId") Long issueId) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getIssue(key, issueId));
    }


    @Operation(summary = "이슈 전체 조회", description = "프로젝트의 이슈 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "이슈 목록 조회 성공", content = @Content(schema = @Schema(implementation = IssueHierarchyResponse.class)))}
    )
    @GetMapping(value = "/api/projects/{key}/issues", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<?> getProjectIssues(@PathVariable("key") String key) {

        return CommonResponse.of(SuccessStatus.OK, issueQueryService.getIssues(key));
    }


    @Operation(summary = "이슈 수정", description = "프로젝트의 이슈를 수정합니다.",
        requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = IssueEditRequest.class))))
    @ApiResponse(responseCode = "204", description = "이슈 수정 성공")
    @PutMapping(value = "/api/projects/{key}/issues/{issueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editProjectIssue(@Valid @ModelAttribute IssueEditRequest request,
                                              @PathVariable("key") String key, @PathVariable("issueId") Long issueId) {

        issueService.updateIssue(key, issueId, request);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "이슈 삭제", description = "프로젝트의 이슈를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "이슈 삭제 성공")
    @DeleteMapping(value = "/api/projects/{key}/issues/{issueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProjectIssue(@PathVariable("key") String key,
                                                @PathVariable("issueId") Long issueId) {

        issueService.deleteIssue(key, issueId);
        return ResponseEntity.noContent().build();
    }
}
