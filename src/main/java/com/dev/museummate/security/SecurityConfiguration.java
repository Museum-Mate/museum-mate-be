package com.dev.museummate.security;

import com.dev.museummate.configuration.redis.service.TokenService;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.security.oauth2.CustomOAuth2UserService;
import com.dev.museummate.security.oauth2.OAuth2FailureHandler;
import com.dev.museummate.security.oauth2.OAuth2SuccessHandler;
import com.dev.museummate.utils.JwtUtils;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

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
                        .requestMatchers("/api/v1/users/join","/api/v1/users/login","/api/v1/users/check","/api/v1/users/sendMail").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/v1/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/v1/example/security").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/example/security/admin").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/reissue","/api/v1/users/logout","/api/v1/users/modify","/api/v1/users/delete").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/v1/my/calendars","/api/v1/my/**").authenticated()
                        .requestMatchers("/api/v1/gatherings/**", "/api/v1/reviews/1").authenticated()
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

                .addFilterBefore(new JwtFilter(userRepository, tokenService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class)
                .build();
    }
    @Bean
     CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
