package dynamicquad.agilehub.issue.service.command;

import dynamicquad.agilehub.issue.domain.ProjectIssueSequence;
import dynamicquad.agilehub.issue.repository.ProjectIssueSequenceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueNumberGenerator {

    private final ProjectIssueSequenceRepository issueSequenceRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis.issue.number.prefix}")
    private String REDIS_ISSUE_PREFIX;

//    @Transactional
//    public String generate(String projectKey) {
//        ProjectIssueSequence sequence = issueSequenceRepository.findByProjectKey(projectKey)
//            .orElseThrow(() -> new IllegalArgumentException("ProjectIssueSequence not found"));
//
//        sequence.updateLastNumber(sequence.getNextNumber());
//
//        return projectKey + "-" + sequence.getLastNumber();
//    }

    public String generate(String projectKey) {
        String redisKey = REDIS_ISSUE_PREFIX + projectKey;
        Long nextNumber = redisTemplate.opsForValue().increment(redisKey);
        return projectKey + "-" + nextNumber;
    }

    @Scheduled(fixedDelay = 1000L * 30L)
    @Transactional
    public void syncWithDatabase() {
        issueSequenceRepository.findAll().forEach(sequence -> {
            String redisKey = REDIS_ISSUE_PREFIX + sequence.getProjectKey();
            String currentValue = redisTemplate.opsForValue().get(redisKey);

            if (currentValue != null) {
                try {
                    int redisValue = Integer.parseInt(currentValue);

                    // 현재 DB 값보다 큰 경우에만 업데이트
                    if (redisValue > sequence.getLastNumber()) {
                        sequence.updateLastNumber(redisValue);
                        log.debug("Synced sequence for project {}: {}",
                            sequence.getProjectKey(), redisValue);
                    }

                } catch (NumberFormatException e) {
                    log.error("Invalid number format in Redis for key: {}", redisKey, e);
                    redisTemplate.delete(redisKey);
                }
            }
        });
    }

    public void decrement(String projectKey) {
        ProjectIssueSequence sequence = issueSequenceRepository.findByProjectKey(projectKey)
            .orElseThrow(() -> new IllegalArgumentException("ProjectIssueSequence not found"));

        sequence.decrement();
    }

    @PostConstruct
    public void initializeRedis() {
        issueSequenceRepository.findAll().forEach(seq -> {
            String redisKey = REDIS_ISSUE_PREFIX + seq.getProjectKey();
            redisTemplate.opsForValue().set(redisKey, String.valueOf(seq.getLastNumber()));
        });
    }

}
