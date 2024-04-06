package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class IssueFactoryProvider {
    private final Map<IssueType, IssueFactory> factories = new EnumMap<>(IssueType.class);

    public IssueFactoryProvider(@Qualifier("EPIC_FACTORY") IssueFactory epicFactory,
                                @Qualifier("STORY_FACTORY") IssueFactory storyFactory,
                                @Qualifier("TASK_FACTORY") IssueFactory taskFactory) {
        factories.put(IssueType.EPIC, epicFactory);
        factories.put(IssueType.STORY, storyFactory);
        factories.put(IssueType.TASK, taskFactory);
    }

    public IssueFactory getIssueFactory(IssueType type) {
        if (factories.containsKey(type)) {
            return factories.get(type);
        } else {
            throw new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND);
        }
    }
}
