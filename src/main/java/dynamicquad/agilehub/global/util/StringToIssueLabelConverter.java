package dynamicquad.agilehub.global.util;

import dynamicquad.agilehub.issue.domain.IssueLabel;
import org.springframework.core.convert.converter.Converter;

public class StringToIssueLabelConverter implements Converter<String, IssueLabel> {

    @Override
    public IssueLabel convert(String source) {
        return IssueLabel.parsing(source);
    }
}
