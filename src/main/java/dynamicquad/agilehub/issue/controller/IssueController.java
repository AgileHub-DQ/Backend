package dynamicquad.agilehub.issue.controller;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.service.IssueService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class IssueController {

    private final IssueService issueService;


    @PostMapping("/api/projects/{key}/issues")
    public ResponseEntity<?> createProjectIssue(@PathVariable("key") String key, @Valid @ModelAttribute
    IssueCreateRequest request) {
        log.info("createProjectIssue key: {}", key);
        Long issueId = issueService.createIssue(key, request);

        return ResponseEntity.created(URI.create("/api/projects/" + key + "/issues/" + issueId))
            .body(CommonResponse.of(SuccessStatus.CREATED, issueId));
    }


}
