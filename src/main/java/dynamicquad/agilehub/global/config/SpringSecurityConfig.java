package dynamicquad.agilehub.global.config;

import dynamicquad.agilehub.global.auth.filter.JwtAuthFilter;
import dynamicquad.agilehub.global.auth.filter.JwtExceptionFilter;
import dynamicquad.agilehub.global.auth.filter.OAuth2SuccessHandler;
import dynamicquad.agilehub.global.auth.repository.CustomAuthorizationRequestRepository;
import dynamicquad.agilehub.global.auth.service.CustomOAuth2UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SpringSecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    @Profile("prod")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // csrf disable 처리 : 추후 설정 변경 필요
        http
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOriginPatterns(Collections.singletonList("*"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setExposedHeaders(List.of("Authorization"));
                config.setMaxAge(3600L);
                return config;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            // requestMatchers 설정
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/oauth2/**", "/auth/success/**", "*/api-docs/**", "/swagger-ui/**",
                    "/actuator/**",
                    "/favicon.ico")
                .permitAll()
                .anyRequest().authenticated()
            )

            // oauth2 설정
            .oauth2Login(customizer -> customizer
                .authorizationEndpoint(authorization -> authorization
                    .authorizationRequestRepository(new CustomAuthorizationRequestRepository()))
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            )

            // jwt 설정
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtExceptionFilter(), JwtAuthFilter.class);

        return http.build();
    }


    @Bean
    @Profile({"security-off", "test", "local"})
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 기존의 모든 필터 비활성화
        http
            .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 비활성화
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()  // 모든 요청 허용
            )
            .oauth2Login(oauth2 -> oauth2.disable())  // OAuth2 로그인 비활성화
            .formLogin(form -> form.disable())  // 폼 로그인 비활성화
            .httpBasic(basic -> basic.disable())  // HTTP Basic 인증 비활성화
            .logout(logout -> logout.disable());  // 로그아웃 기능 비활성화

        // JWT 필터 제거 (만약 커스텀 필터가 있다면)
        http.addFilterBefore(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain)
                throws ServletException, IOException {
                filterChain.doFilter(request, response);
            }
        }, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Profile({"security-off", "test", "local"})
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/**");  // 모든 경로에 대해 시큐리티 무시
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
