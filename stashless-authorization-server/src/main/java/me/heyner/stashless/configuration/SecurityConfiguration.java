package me.heyner.stashless.configuration;

import java.util.List;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@Configuration
public class SecurityConfiguration {

  private final OAuth2AuthorizationServerPropertiesMapper propertiesMapper;

  public SecurityConfiguration(OAuth2AuthorizationServerProperties properties) {
    this.propertiesMapper = new OAuth2AuthorizationServerPropertiesMapper(properties);
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
    List<RegisteredClient> registeredClients = this.propertiesMapper.asRegisteredClients();
    JdbcRegisteredClientRepository registeredClientRepository =
        new JdbcRegisteredClientRepository(jdbcTemplate);
    registeredClients.forEach(registeredClientRepository::save);
    return registeredClientRepository;
  }
}
