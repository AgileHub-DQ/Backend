package dynamicquad.agilehub.issue.controller;

import dynamicquad.agilehub.global.header.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class IssueController {


    @GetMapping("/api/projects/{key}/boards")
    public String getProjectBoards(@PathVariable("key") String key) {
        log.info("getProjectBoards");
        log.info("key: {}", key);
        return "project-boards";
    }

    @PostMapping("/api/projects/{key}/boards")
    public CommonResponse<?> createProjectIssue(@PathVariable("key") String key) {
        log.info("createProjectIssue");

        return null;
    }

}
