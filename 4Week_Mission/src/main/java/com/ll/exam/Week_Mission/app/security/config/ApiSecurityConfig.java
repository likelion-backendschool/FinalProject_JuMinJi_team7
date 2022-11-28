package com.ll.exam.Week_Mission.app.security.config;

import com.ll.exam.Week_Mission.app.security.jwt.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception  {
        http
                .antMatcher("/api/**")
                .httpBasic().disable()
                .csrf().disable()
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .authorizeRequests(
                        authorizeRequests -> authorizeRequests
                                .antMatchers("/api/*/member/login").permitAll()
                                .anyRequest()
                                .authenticated() // 최소자격 : 로그인
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(STATELESS)
                )
                .formLogin().disable()
                .addFilterBefore(
                        jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .logout().disable();

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*"); // CORS 요청 허용 사이트
        corsConfiguration.addAllowedHeader("*"); // 특정 헤더를 가진 경우에만 CORS 요청을 허용할 경우
        corsConfiguration.addAllowedMethod("*"); // CORS 요청을 허용할 Http Method들

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/api/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }
}