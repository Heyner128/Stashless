package me.heyner.stashless.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class CoreSecurityConfiguration {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain coreSecurityFilterChain(HttpSecurity http) throws Exception {
    final String[] ENDPOINTS_PATTERNS = {"/actuator/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**"};

    http.cors(Customizer.withDefaults())
            .securityMatcher(ENDPOINTS_PATTERNS)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, ENDPOINTS_PATTERNS[0])
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET, Arrays.copyOfRange(ENDPOINTS_PATTERNS, 1, ENDPOINTS_PATTERNS.length))
                    .permitAll()
        );

    return http.build();
  }
}
