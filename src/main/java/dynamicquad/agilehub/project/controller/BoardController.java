package dynamicquad.agilehub.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    @GetMapping("/api/projects/{key}/boards")
    public String getProjectBoards(@PathVariable("key") String key) {
        log.info("getProjectBoards");
        log.info("key: {}", key);
        return "project-boards";
    }

}
