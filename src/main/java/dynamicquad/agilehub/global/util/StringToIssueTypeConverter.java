package dynamicquad.agilehub.global.util;

import dynamicquad.agilehub.issue.IssueType;
import org.springframework.core.convert.converter.Converter;

public class StringToIssueTypeConverter implements Converter<String, IssueType> {

    @Override
    public IssueType convert(String source) {
        return IssueType.parsing(source);
    }

}
