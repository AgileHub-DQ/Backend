package dynamicquad.agilehub.issue.service.command;

import dynamicquad.agilehub.issue.domain.ProjectIssueSequence;
import dynamicquad.agilehub.issue.repository.ProjectIssueSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IssueNumberGenerator {

    private final ProjectIssueSequenceRepository issueSequenceRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Retry
    public String generate(String projectKey) {
        ProjectIssueSequence sequence = issueSequenceRepository.findByProjectKey(projectKey)
            .orElseThrow(() -> new IllegalArgumentException("ProjectIssueSequence not found"));

        sequence.updateLastNumber(sequence.getNextNumber());

        return projectKey + "-" + sequence.getLastNumber();
    }

    @Transactional
    public void decrement(String projectKey) {
        ProjectIssueSequence sequence = issueSequenceRepository.findByProjectKey(projectKey)
            .orElseThrow(() -> new IllegalArgumentException("ProjectIssueSequence not found"));

        sequence.decrement();
    }
}
