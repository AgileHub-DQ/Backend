package dynamicquad.agilehub.global.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultSESConfig {
    @Value("${aws.ses.accessKey}")
    private String accessKey;

    @Value("${aws.ses.secretKey}")
    private String secretKey;

    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonSimpleEmailServiceClientBuilder.standard()
            .withRegion(Regions.AP_NORTHEAST_2)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withClientConfiguration(new ClientConfiguration()
                .withConnectionTimeout(3000)
                .withSocketTimeout(5000)
                .withRequestTimeout(10000))
            .build();
    }
}
