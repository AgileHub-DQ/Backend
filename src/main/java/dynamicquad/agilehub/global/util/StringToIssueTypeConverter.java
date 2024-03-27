package dynamicquad.agilehub.global.util;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToIssueTypeConverter implements Converter<String, IssueType> {

    @Override
    public IssueType convert(String source) {
        log.info("StringToIssueTypeConverter convert source: {}", source);
        return IssueType.parsing(source);
    }

}
