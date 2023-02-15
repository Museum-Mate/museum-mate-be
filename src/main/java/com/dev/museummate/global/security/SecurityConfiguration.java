package com.dev.museummate.global.security;

import com.dev.museummate.global.redis.RedisDao;
import com.dev.museummate.global.token.JwtExceptionFilter;
import com.dev.museummate.global.token.JwtFilter;
import com.dev.museummate.global.utils.JwtUtils;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.global.security.oauth2.CustomOAuth2UserService;
import com.dev.museummate.global.security.oauth2.OAuth2FailureHandler;
import com.dev.museummate.global.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserRepository userRepository;
    private final RedisDao redisDao;
    private final JwtUtils jwtUtils;

    private static final String[] SWAGGER_AUTH = {
        "/api-docs/swagger-config/**",
        "/swagger-ui.html/**",
        "/swagger-ui/**",
        "/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**",
    };
    private static final String[] ALL_AUTH_USER = {

    };
    private static final String[] GET_AUTH_USER = {
        "/api/v1/example/security",
        "/api/v1/my/**",
    };
    private static final String[] POST_AUTH_USER = {
        "/api/v1/users/reissue",
        "/api/v1/users/logout",
        "/api/v1/exhibitions/new",
        "/api/v1/exhibitions/{exhibitionId}", // pathVariable 로 전환해야됨
        "/exhibitions/{exhibitionId}/bookmarks",
        "/api/v1/reviews/{reviewId}", // pathVariable 로 전환해야됨
        "/api/v1/gatherings/posts",
        "/api/v1/gatherings/{gatheringId}/enroll", // pathVariable 로 전환해야됨
        "/api/v1/gatherings/{gatheringID}/comments", // pathVariable 로 전환해야됨
        "/api/v1/gatherings/{gatheringID}/comments/{commentId}/replie", // pathVariable 로 전환해야됨
    };
    private static final String[] PUT_AUTH_USER = {
        "/api/v1/users/modify",
        "/reviews/{reviewId}",
        " /gatherings/{gatheringId}",
        "/gatherings/{gatheringId}/comments/{id}",
    };
    private static final String[] DELETE_AUTH_USER = {
        "/reviews/{reviewId}",
        "/users/delete",
        "/reviews/{reviewId}",
        "/gatherings/{gatheringId}/cancel",
    };
    private static final String[] AUTH_ADMIN = {
        "/api/v1/example/security/admin"
    };
    private static final String[] PERMIT_ALL = {
        "/api/v1/users/join",
        "/api/v1/users/login",
        "/api/v1/users/check",
        "/api/v1/users/sendMail",
        "/api/v1/exhibitions",
        "/api/v1/gatherings",
        "/api/v1/exhibitions/{exhibitionId}", // pathVariable 로 전환해야됨
        "/api/v1/my/alarms",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .httpBasic().disable()
            .csrf().disable()
            .cors().and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(SWAGGER_AUTH).permitAll()
                .requestMatchers(PERMIT_ALL).permitAll()
                .requestMatchers(HttpMethod.GET, GET_AUTH_USER).authenticated()
                .requestMatchers(HttpMethod.POST, POST_AUTH_USER).authenticated()
                .requestMatchers(HttpMethod.PUT, PUT_AUTH_USER).authenticated()
                .requestMatchers(HttpMethod.DELETE, DELETE_AUTH_USER).authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/example/security/admin").hasRole("ADMIN")
                .anyRequest().permitAll()   //고정
            )
            .oauth2Login()
            .and()
            .oauth2Login()
            .loginPage("/login")
            .userInfoEndpoint().userService(new CustomOAuth2UserService(userRepository))
            .and()
            .successHandler(new OAuth2SuccessHandler(userRepository, jwtUtils))
            .failureHandler(new OAuth2FailureHandler())
            .and()

            .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
            .and()
            .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .and()

            .addFilterBefore(new JwtFilter(userRepository, redisDao, jwtUtils), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class)
            .build();
    }

}
