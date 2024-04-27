package dynamicquad.agilehub.global.config;

import dynamicquad.agilehub.global.controller.MemberArgumentResolver;
import dynamicquad.agilehub.global.util.StringToIssueStatusConverter;
import dynamicquad.agilehub.global.util.StringToIssueTypeConverter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIssueTypeConverter());
        registry.addConverter(new StringToIssueStatusConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MemberArgumentResolver());
    }
}
