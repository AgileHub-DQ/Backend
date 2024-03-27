package dynamicquad.agilehub.issue.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.service.factory.IssueFactoryProvider;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueService {

    private final ProjectRepository projectRepository;
    private final IssueFactoryProvider issueFactoryProvider;

    @Transactional
    public Long createIssue(String key, IssueCreateRequest request) {
        Project project = findProject(key);

        return issueFactoryProvider.getIssueFactory(request.getType())
            .createIssue(request, project);

    }

    private Project findProject(String key) {
        return projectRepository.findByKey(key)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
    }
}
