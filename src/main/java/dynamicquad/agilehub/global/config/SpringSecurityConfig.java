package dynamicquad.agilehub.global.config;

import dynamicquad.agilehub.global.auth.CustomOAuth2UserService;
import dynamicquad.agilehub.global.filter.OAuth2SuccessHandler;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final CustomOAuth2UserService customOAuth2UserService;

    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // csrf disable 처리 : 추후 설정 변경 필요
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("http://www.agilehub.store"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/**").permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2Login(customizer -> {
                            customizer.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService));
                            customizer.successHandler(oAuth2SuccessHandler);
                            customizer.authorizationEndpoint(auth -> auth.authorizationRequestRepository(null));
                        }
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
