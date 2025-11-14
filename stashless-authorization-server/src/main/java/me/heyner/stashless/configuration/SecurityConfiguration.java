package me.heyner.stashless.configuration;

import java.util.List;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final OAuth2AuthorizationServerPropertiesMapper propertiesMapper;

  public SecurityConfiguration(OAuth2AuthorizationServerProperties properties) {
    this.propertiesMapper = new OAuth2AuthorizationServerPropertiesMapper(properties);
  }

  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
      throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        OAuth2AuthorizationServerConfigurer.authorizationServer();
    return http.cors(Customizer.withDefaults())
        .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        .with(
            authorizationServerConfigurer,
            (authorizationServer) -> authorizationServer.oidc(Customizer.withDefaults()))
        .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
        .exceptionHandling(
            (exceptions) ->
                exceptions.defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/users")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/users/login")
                    .permitAll()
                    .requestMatchers(
                        (HttpMethod) null,
                        "/{filename:.*\\.css|.*\\.ico|.*\\.svg|.*\\.webp|.*\\.js|.*\\.woff|.*\\.woff2|.*\\.png}")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .cors(Customizer.withDefaults())
        // TODO configure csrf
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(form -> form.loginPage("/login").permitAll())
        .build();
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
    List<RegisteredClient> registeredClients = this.propertiesMapper.asRegisteredClients();
    // TODO change for JPA implementation to avoid duplicating spring code
    JdbcRegisteredClientRepository registeredClientRepository =
        new JdbcRegisteredClientRepository(jdbcTemplate);
    registeredClients.forEach(registeredClientRepository::save);
    return registeredClientRepository;
  }
}
