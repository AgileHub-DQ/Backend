package dynamicquad.agilehub.issue.service.query;

import java.util.List;
import lombok.Getter;

@Getter
public class MonthlyReportDto {
    private List<String> contentsByEpic;
    private List<String> contentsByStory;
    private List<String> contentsByTask;

    public MonthlyReportDto(List<String> contentsByEpic, List<String> contentsByStory, List<String> contentsByTask) {
        this.contentsByEpic = contentsByEpic;
        this.contentsByStory = contentsByStory;
        this.contentsByTask = contentsByTask;
    }
}
