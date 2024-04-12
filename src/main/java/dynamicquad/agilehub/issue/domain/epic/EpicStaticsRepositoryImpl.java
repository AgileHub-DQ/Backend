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
            .select(Projections.bean(EpicStatisticDto.class,
                story.epic.id.as("epicId"),
                story.id.count().as("totalStoryCount"),
                new CaseBuilder().when(issue.status.eq(IssueStatus.DO)).then(1L).otherwise(0L).sum()
                    .as("toDoStoryCount"),
                new CaseBuilder().when(issue.status.eq(IssueStatus.PROGRESS)).then(1L).otherwise(0L).sum()
                    .as("inProgressStoryCount"),
                new CaseBuilder().when(issue.status.eq(IssueStatus.DONE)).then(1L).otherwise(0L).sum()
                    .as("completedStoryCount")))
            .from(story)
            .leftJoin(issue).on(story.id.eq(issue.id))
            .leftJoin(epic).on(epic.id.eq(story.epic.id))
            .where(issue.project.id.eq(projectId))
            .groupBy(story.epic.id)
            .orderBy(story.epic.id.asc())
            .fetch();
    }
}
