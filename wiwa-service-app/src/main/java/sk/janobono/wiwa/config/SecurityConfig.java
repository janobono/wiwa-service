package sk.janobono.wiwa.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;
import sk.janobono.wiwa.common.component.JwtToken;
import sk.janobono.wiwa.common.config.CommonConfigProperties;
import sk.janobono.wiwa.security.ApplicationAccessDeniedHandler;
import sk.janobono.wiwa.security.ApplicationAuthenticationEntryPoint;
import sk.janobono.wiwa.security.jwt.JwtAuthenticationEntryPoint;
import sk.janobono.wiwa.security.jwt.JwtAuthenticationFilter;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final CommonConfigProperties commonConfigProperties;
    private final SecurityConfigProperties securityConfigProperties;
    private final JwtToken jwtToken;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtToken);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new ApplicationAccessDeniedHandler(handlerExceptionResolver);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new ApplicationAuthenticationEntryPoint(handlerExceptionResolver);
    }

    @Bean
    public RequestMatcher permitAllRequestMatcher() {
        return new RequestMatcher() {
            private Pattern publicPathPattern;

            private Pattern getPublicPathPattern() {
                if (publicPathPattern == null) {
                    publicPathPattern = Pattern.compile(securityConfigProperties.publicPathPatternRegex());
                }
                return publicPathPattern;
            }

            @Override
            public boolean matches(HttpServletRequest request) {
                return getPublicPathPattern().matcher(request.getServletPath()).matches();
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .cors().disable()
                .authorizeHttpRequests()
                .requestMatchers(permitAllRequestMatcher()).permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint());
        return httpSecurity.build();
    }
}
