package dynamicquad.agilehub.sprint.controller;

import static dynamicquad.agilehub.sprint.controller.SprintRequest.SprintAssignIssueRequest;
import static dynamicquad.agilehub.sprint.controller.SprintRequest.SprintChangeStatusRequest;
import static dynamicquad.agilehub.sprint.controller.SprintRequest.SprintCreateRequest;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.sprint.SprintQueryService;
import dynamicquad.agilehub.sprint.SprintService;
import dynamicquad.agilehub.sprint.controller.response.SprintResponse.SprintCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "스프린트", description = "스프린트를 생성하고 조회합니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
public class SprintController {

    private final SprintService sprintService;
    private final SprintQueryService sprintQueryService;

    @Operation(summary = "스프린트 생성", description = "프로젝트에 스프린트를 생성합니다.")
    @PostMapping(value = "/api/projects/{key}/sprints")
    public ResponseEntity<?> createProjectSprint(@Valid @RequestBody SprintCreateRequest request,
                                                 @PathVariable("key") String key) {

        SprintCreateResponse resp = sprintService.createSprint(key, request);

        return ResponseEntity.created(URI.create("/api/projects/" + key + "/sprints/" + resp.getSprintId()))
            .body(CommonResponse.of(SuccessStatus.CREATED, resp));
    }

    @Operation(summary = "스프린트 내 이슈 할당", description = "이슈를 스프린트에 할당합니다")
    @PostMapping(value = "/api/projects/{key}/sprints/{sprintId}/issue")
    public ResponseEntity<?> assignIssueToSprint(@Valid @RequestBody SprintAssignIssueRequest request,
                                                 @PathVariable("key") String key,
                                                 @PathVariable("sprintId") Long sprintId) {

        sprintService.assignIssueToSprint(key, sprintId, request.getIssueId());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스프린트 내 이슈 제거", description = "스프린트에 할당된 이슈를 제거합니다")
    @DeleteMapping(value = "/api/projects/{key}/sprints/{sprintId}/issue")
    public ResponseEntity<?> removeIssueFromSprint(@Valid @RequestBody SprintAssignIssueRequest request,
                                                   @PathVariable("key") String key,
                                                   @PathVariable("sprintId") Long sprintId) {

        sprintService.removeIssueFromSprint(key, sprintId, request.getIssueId());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스프린트 상태 변경", description = "스프린트의 상태를 변경합니다 (PLANNED, ACTIVE, COMPLETED)")
    @PatchMapping(value = "/api/projects/{key}/sprints/{sprintId}/status")
    public ResponseEntity<?> changeSprintStatus(@Valid @RequestBody SprintChangeStatusRequest request,
                                                @PathVariable("key") String key,
                                                @PathVariable("sprintId") Long sprintId) {

        sprintService.changeSprintStatus(key, sprintId, request.getStatus());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스프린트들 조회", description = "프로젝트의 스프린트들을 조회합니다")
    @GetMapping(value = "/api/projects/{key}/sprints")
    public ResponseEntity<?> getSprints(@PathVariable("key") String key) {

        return ResponseEntity.ok(CommonResponse.of(SuccessStatus.OK, sprintQueryService.getSprints(key)));
    }
}
