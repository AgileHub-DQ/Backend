package dynamicquad.agilehub.global.config;

import dynamicquad.agilehub.global.util.StringToIssueStatusConverter;
import dynamicquad.agilehub.global.util.StringToIssueTypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        log.info("addFormatters");
        registry.addConverter(new StringToIssueTypeConverter());
        registry.addConverter(new StringToIssueStatusConverter());
    }

}
