package dynamicquad.agilehub.project.controller;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.project.controller.request.ProjectCreateReq;
import dynamicquad.agilehub.project.controller.request.ProjectUpdateReq;
import dynamicquad.agilehub.project.controller.response.ProjectRes;
import dynamicquad.agilehub.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Tag(name = "프로젝트", description = "프로젝트를 생성하고 조회합니다.")
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "프로젝트를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "프로젝트 생성 후 보드 페이지로 이동합니다."),
        @ApiResponse(responseCode = "400", description = "프로젝트 키가 중복됩니다."),
    })
    @PostMapping("/api/projects")
    public String createProject(@RequestBody @Valid ProjectCreateReq request, RedirectAttributes redirectAttributes) {
        log.info("createProject");
        redirectAttributes.addAttribute("key", projectService.createProject(request));
        return "redirect:/api/projects/{key}/boards";
    }

    @Operation(summary = "프로젝트 목록 조회", description = "유저의 프로젝트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProjectRes.class)))}),
        @ApiResponse(responseCode = "400", description = "유저가 존재하지 않습니다."),
        @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않습니다.")
    })
    @ResponseBody
    @GetMapping("/api/projects")
    public CommonResponse<?> getProjects() {
        log.info("getProjects");
        final Long memberId = 1L;
        List<ProjectRes> projects = projectService.getProjects(memberId);

        return CommonResponse.onSuccess(projects);
    }

    @PatchMapping("/api/projects/{key}")
    public String updateProject(@RequestBody @Valid ProjectUpdateReq request, @PathVariable String key,
                                RedirectAttributes redirectAttributes) {
        log.info("updateProject");
        redirectAttributes.addAttribute("key", projectService.updateProject(key, request));

        return "redirect:/api/projects/{key}/boards";
    }

}
