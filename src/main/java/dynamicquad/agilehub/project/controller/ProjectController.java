package dynamicquad.agilehub.project.controller;

import static dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectCreateRequest;
import static dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectUpdateRequest;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.project.controller.response.ProjectResponse;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import dynamicquad.agilehub.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "프로젝트", description = "프로젝트를 생성하고 조회합니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectQueryService projectQueryService;

    @Operation(summary = "프로젝트 생성", description = "프로젝트를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "프로젝트 생성 성공"),
        @ApiResponse(responseCode = "400", description = "프로젝트 키가 중복됩니다."),
    })
    @PostMapping("/api/projects")
    public ResponseEntity<?> createProject(@RequestBody @Valid ProjectCreateRequest request) {

        String key = projectService.createProject(request);
        return ResponseEntity.created(URI.create("/api/projects/" + key + "/issues"))
            .body(CommonResponse.of(SuccessStatus.CREATED, key));
    }

    @Operation(summary = "프로젝트 목록 조회", description = "유저의 프로젝트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class)))}),
        @ApiResponse(responseCode = "400", description = "유저가 존재하지 않습니다."),
        @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않습니다.")
    })
    @GetMapping("/api/projects")
    public CommonResponse<?> getProjects() {
        //TODO: 테스트용 memberId
        //TODO: 실제 사용시 로그인한 유저의 memberId를 가져와야함 [ ]
        final Long memberId = 1L;
        List<ProjectResponse> projects = projectQueryService.getProjects(memberId);
        return CommonResponse.onSuccess(projects);
    }

    @Operation(summary = "프로젝트 수정", description = "프로젝트를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "프로젝트 수정 후 보드 페이지로 이동합니다."),
        @ApiResponse(responseCode = "400", description = "프로젝트 키가 중복됩니다."),
        @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않습니다.")
    })
    @PutMapping("/api/projects/{key}")
    public ResponseEntity<?> updateProject(@RequestBody @Valid ProjectUpdateRequest request, @PathVariable String key) {

        String updateKey = projectService.updateProject(key, request);
        return ResponseEntity.created(URI.create("/api/projects/" + updateKey + "/issues"))
            .body(CommonResponse.of(SuccessStatus.CREATED, updateKey));
    }


}
