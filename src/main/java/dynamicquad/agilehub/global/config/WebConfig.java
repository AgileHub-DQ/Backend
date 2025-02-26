package dynamicquad.agilehub.global.config;

import dynamicquad.agilehub.global.auth.util.MemberArgumentResolver;
import dynamicquad.agilehub.global.util.StringToIssueLabelConverter;
import dynamicquad.agilehub.global.util.StringToIssueStatusConverter;
import dynamicquad.agilehub.global.util.StringToIssueTypeConverter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIssueTypeConverter());
        registry.addConverter(new StringToIssueStatusConverter());
        registry.addConverter(new StringToIssueLabelConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MemberArgumentResolver(activeProfile));
    }
}
