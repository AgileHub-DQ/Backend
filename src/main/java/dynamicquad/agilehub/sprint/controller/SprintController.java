package dynamicquad.agilehub.sprint.controller;

import static dynamicquad.agilehub.sprint.controller.SprintRequest.SprintCreateRequest;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.sprint.SprintService;
import dynamicquad.agilehub.sprint.controller.SprintResponse.SprintCreateResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/api/projects/{key}/sprints")
    public ResponseEntity<?> createProjectSprint(@Valid @RequestBody SprintCreateRequest request,
                                                 @PathVariable("key") String key) {

        SprintCreateResponse resp = sprintService.createSprint(key, request);

        return ResponseEntity.created(URI.create("/api/projects/" + key + "/sprints/" + resp.getSprintId()))
            .body(CommonResponse.of(SuccessStatus.CREATED, resp));
    }
}
