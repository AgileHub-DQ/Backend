package dynamicquad.agilehub.issue.domain.epic;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dynamicquad.agilehub.issue.controller.response.EpicResponse.EpicStatisticDto;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.QIssue;
import dynamicquad.agilehub.issue.domain.story.QStory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EpicStaticsRepositoryImpl implements EpicStaticsRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<EpicStatisticDto> getEpicStatics(Long projectId) {

        log.info("getEpicStatics");
        QStory story = QStory.story;
        QIssue issue = QIssue.issue;
        QEpic epic = QEpic.epic;

        return queryFactory
            .select(
                Projections.fields(EpicStatisticDto.class,
                    epic.id.as("epicId"),
                    story.id.count().as("storiesCount"),
                    new CaseBuilder()
                        .when(issue.status.eq(IssueStatus.DO))
                        .then(1L)
                        .otherwise(0L)
                        .sum().as("statusDo"),
                    new CaseBuilder()
                        .when(issue.status.eq(IssueStatus.PROGRESS))
                        .then(1L)
                        .otherwise(0L)
                        .sum().as("statusProgress"),
                    new CaseBuilder()
                        .when(issue.status.eq(IssueStatus.DONE))
                        .then(1L)
                        .otherwise(0L)
                        .sum().as("statusDone")
                ))
            .from(epic)
            .leftJoin(story).on(epic.id.eq(story.epic.id))
            .leftJoin(issue).on(story.id.eq(issue.id))
            .where(epic.project.id.eq(projectId))
            .groupBy(epic.id)
            .fetch();
    }
}
