package dynamicquad.agilehub.issue.domain.epic;

import dynamicquad.agilehub.issue.controller.response.EpicResponse.EpicStatisticDto;
import java.util.List;

public interface EpicStaticsRepository {
    List<EpicStatisticDto> getEpicStatics(Long projectId);
}
