package dynamicquad.agilehub.sprint.controller;

import static dynamicquad.agilehub.sprint.controller.SprintRequest.SprintAssignIssueRequest;
import static dynamicquad.agilehub.sprint.controller.SprintRequest.SprintCreateRequest;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.sprint.SprintService;
import dynamicquad.agilehub.sprint.controller.SprintResponse.SprintCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

}
