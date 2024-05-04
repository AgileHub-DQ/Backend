package dynamicquad.agilehub.global.util;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import org.springframework.core.convert.converter.Converter;

public class StringToIssueTypeConverter implements Converter<String, IssueType> {

    @Override
    public IssueType convert(String source) {
        return IssueType.parsing(source);
    }

}
