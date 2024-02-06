package com.collabed.core.config;

import com.collabed.core.api.util.CustomHttpHeaders;
import com.collabed.core.api.util.JwtTokenFilter;
import com.collabed.core.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userDetailsService;
    private final JwtTokenFilter jwtTokenFilter;

    @Value("${client.host}")
    private String clientHost;

    @Bean
    public AuthenticationManager customAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userDetailsService);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(configurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((requests) -> requests
                    .requestMatchers(
                            "/",
                            "/register/*",
                            "/login",
                            "/api-docs",
                            "/api-docs/*",
                            "/swagger-ui/*")
                    .permitAll()
                    .anyRequest().authenticated()
            )
            .logout(logout -> logout.permitAll()
                    .logoutSuccessHandler(
                            (request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)
                    )
            )
            .addFilterBefore(
                    jwtTokenFilter,
                    UsernamePasswordAuthenticationFilter.class
            )
        ;
        return http.build();
    }

    @Bean
    CorsConfigurationSource configurationSource() {
        var configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(List.of(CustomHttpHeaders.SESSION_KEY, "Authorization"));
        configuration.addAllowedMethod("*");
        configuration.addAllowedOrigin(clientHost);
        configuration.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
