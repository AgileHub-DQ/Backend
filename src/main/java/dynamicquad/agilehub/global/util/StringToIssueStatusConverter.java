package dynamicquad.agilehub.global.util;

import dynamicquad.agilehub.issue.domain.IssueStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToIssueStatusConverter implements Converter<String, IssueStatus> {
    @Override
    public IssueStatus convert(String source) {
        return IssueStatus.parsing(source);
    }
}
