package com.sm.project.config.springSecurity;

import com.sm.project.config.springSecurity.Handler.CustomAccessDeniedHandler;
import com.sm.project.config.springSecurity.Handler.CustomAuthenticationEntryPoint;
import com.sm.project.config.springSecurity.Handler.JwtAuthenticationExceptionHandler;
import com.sm.project.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {



    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationExceptionHandler exceptionFilter;
    private final RedisService redisService;
    private final TokenProvider tokenProvider;
    @Value("${jwt.token.secret}")
    private String secretKey;

    private final UrlBasedCorsConfigurationSource corsConfigurationSource;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring()
                .antMatchers(
                        "/favicon.ico",
                        "/health",
                        "/",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/api/members/**",
                        "/api/members/login",
                        "/api/members/register",
                        "/api/members/send",
                        "/api/members/email",
                        "/api/members/password/send",
                        "/api/members/password",
                        "/api/members/nickname",
                        "/api/members/password/reset"
                );
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .httpBasic().disable()//토큰 인증 방식으로 하기 위해서 HTTP 기본 인증 비활성화
                .csrf().disable()//CSRF 공격 방어 기능 비활성화
                .cors()
                .configurationSource(corsConfigurationSource)

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()//모든 접근 허용
                //.antMatchers(HttpMethod.POST, "/api/members/jwt/test").authenticated()//인증 필요로 접근 막기

                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()


                .addFilterBefore(new JwtFilter(tokenProvider, redisService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionFilter, JwtFilter.class)
                .build();


    }
}
