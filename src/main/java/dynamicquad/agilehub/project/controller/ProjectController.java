package dynamicquad.agilehub.project.controller;

import dynamicquad.agilehub.project.controller.request.ProjectCreateReq;
import dynamicquad.agilehub.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/api/projects")
    public String createProject(@RequestBody @Valid ProjectCreateReq request, RedirectAttributes redirectAttributes) {
        log.info("createProject");
        redirectAttributes.addAttribute("key", projectService.createProject(request));
        return "redirect:/api/projects/{key}/boards";
    }

    @ResponseBody
    @GetMapping("/api/projects/{key}/boards")
    public String getProjectBoards() {
        log.info("getProjectBoards");
        return "project-boards";
    }
}
