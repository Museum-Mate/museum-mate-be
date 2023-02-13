package com.dev.museummate.security;

import com.dev.museummate.configuration.redis.RedisDao;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.security.oauth2.CustomOAuth2UserService;
import com.dev.museummate.security.oauth2.OAuth2FailureHandler;
import com.dev.museummate.security.oauth2.OAuth2SuccessHandler;
import com.dev.museummate.utils.JwtUtils;
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
                        .requestMatchers(HttpMethod.GET,"/api/v1/example/security").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/example/security/admin").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/reissue","/api/v1/users/logout","/api/v1/users/modify","/api/v1/users/delete").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/v1/my/calendars","/api/v1/my/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/v1/reviews/**", "/api/v1/exhibitions/**").authenticated() // 추가
                        .requestMatchers("/api/v1/gatherings/**").authenticated()
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
