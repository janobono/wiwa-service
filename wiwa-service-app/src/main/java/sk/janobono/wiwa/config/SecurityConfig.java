package sk.janobono.wiwa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.JwtToken;
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
    private final CorsConfigProperties corsConfigProperties;
    private final SecurityConfigProperties securityConfigProperties;
    private final JwtToken jwtToken;
    private final ApplicationPropertyService applicationPropertyService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(configurationSource()))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(permitAllRequestMatcher()).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint())
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(accessDeniedHandler())
                                .authenticationEntryPoint(authenticationEntryPoint())
                );

        return httpSecurity.build();
    }

    private CorsConfigurationSource configurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(corsConfigProperties.allowedOrigins());
        corsConfiguration.setAllowedMethods(corsConfigProperties.allowedMethods());
        corsConfiguration.setAllowedHeaders(corsConfigProperties.allowedHeaders());
        corsConfiguration.setAllowCredentials(corsConfigProperties.allowCredentials());
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    private RequestMatcher permitAllRequestMatcher() {
        final Pattern publicPathPattern = Pattern.compile(securityConfigProperties.publicPathPatternRegex());
        return request -> publicPathPattern.matcher(request.getServletPath()).matches();
    }

    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtToken, applicationPropertyService);
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new ApplicationAccessDeniedHandler(handlerExceptionResolver);
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new ApplicationAuthenticationEntryPoint(handlerExceptionResolver);
    }
}
