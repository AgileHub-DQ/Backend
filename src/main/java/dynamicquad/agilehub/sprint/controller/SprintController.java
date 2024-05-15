package dynamicquad.agilehub.sprint.controller;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.sprint.dto.SprintRequestDto;
import dynamicquad.agilehub.sprint.dto.SprintResponseDto;
import dynamicquad.agilehub.sprint.service.SprintQueryService;
import dynamicquad.agilehub.sprint.service.SprintService;
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
    @PostMapping(value = "/projects/{key}/sprints")
    public ResponseEntity<?> createProjectSprint(@Valid @RequestBody SprintRequestDto.CreateSprint request,
                                                 @PathVariable("key") String key,
                                                 @Auth AuthMember authMember) {

        SprintResponseDto.CreatedSprint resp = sprintService.createSprint(key, request, authMember);

        return ResponseEntity.created(URI.create("/projects/" + key + "/sprints/" + resp.getSprintId()))
            .body(CommonResponse.of(SuccessStatus.CREATED, resp));
    }

    @Operation(summary = "스프린트 내 이슈 할당", description = "이슈를 스프린트에 할당합니다")
    @PostMapping(value = "/projects/{key}/sprints/{sprintId}/issue")
    public ResponseEntity<?> assignIssueToSprint(@Valid @RequestBody SprintRequestDto.AssignIssueToSprint request,
                                                 @PathVariable("key") String key,
                                                 @PathVariable("sprintId") Long sprintId,
                                                 @Auth AuthMember authMember) {

        sprintService.assignIssueToSprint(key, sprintId, request.getIssueId(), authMember);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스프린트 내 이슈 제거", description = "스프린트에 할당된 이슈를 제거합니다")
    @DeleteMapping(value = "/projects/{key}/sprints/{sprintId}/issue")
    public ResponseEntity<?> removeIssueFromSprint(@Valid @RequestBody SprintRequestDto.AssignIssueToSprint request,
                                                   @PathVariable("key") String key,
                                                   @PathVariable("sprintId") Long sprintId,
                                                   @Auth AuthMember authMember) {

        sprintService.removeIssueFromSprint(key, sprintId, request.getIssueId(), authMember);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스프린트 상태 변경", description = "스프린트의 상태를 변경합니다 (PLANNED, ACTIVE, COMPLETED)")
    @PatchMapping(value = "/projects/{key}/sprints/{sprintId}/status")
    public ResponseEntity<?> changeSprintStatus(@Valid @RequestBody SprintRequestDto.SprintChangeStatus request,
                                                @PathVariable("key") String key,
                                                @PathVariable("sprintId") Long sprintId,
                                                @Auth AuthMember authMember) {

        sprintService.changeSprintStatus(key, sprintId, request.getStatus(), authMember);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스프린트들 조회", description = "프로젝트의 스프린트들을 조회합니다")
    @GetMapping(value = "/projects/{key}/sprints")
    public ResponseEntity<?> getSprints(@PathVariable("key") String key) {

        return ResponseEntity.ok(CommonResponse.of(SuccessStatus.OK, sprintQueryService.getSprints(key)));
    }

    @Operation(summary = "스프린트 삭제", description = "스프린트를 삭제합니다")
    @DeleteMapping(value = "/projects/{key}/sprints/{sprintId}")
    public ResponseEntity<?> deleteSprint(@PathVariable("key") String key,
                                          @PathVariable("sprintId") Long sprintId,
                                          @Auth AuthMember authMember) {

        sprintService.deleteSprint(key, sprintId, authMember);
        return ResponseEntity.noContent().build();
    }
}
