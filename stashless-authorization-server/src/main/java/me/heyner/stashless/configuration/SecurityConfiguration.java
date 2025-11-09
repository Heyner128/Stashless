package me.heyner.stashless.configuration;

import java.util.List;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final OAuth2AuthorizationServerPropertiesMapper propertiesMapper;

  public SecurityConfiguration(OAuth2AuthorizationServerProperties properties) {
    this.propertiesMapper = new OAuth2AuthorizationServerPropertiesMapper(properties);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
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
