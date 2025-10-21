package me.heyner.stashless.oidc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OidcRegistrationTests {
  @Autowired RegisteredClientRepository registeredClientRepository;
  @Autowired OAuth2AuthorizationServerProperties configurationProperties;
  @Autowired JdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("test client is registered")
  void testClientIsRegistered() {
    OAuth2AuthorizationServerProperties.Client propertiesClient =
        configurationProperties.getClient().get("oidc-client");
    RegisteredClient registeredClient =
        registeredClientRepository.findByClientId(propertiesClient.getRegistration().getClientId());
    assertThat(registeredClient, notNullValue());
    assertThat(
        registeredClient.getClientId(), equalTo(propertiesClient.getRegistration().getClientId()));
    assertThat(
        registeredClient.getClientSecret(),
        equalTo(propertiesClient.getRegistration().getClientSecret()));
  }

  @Test
  @DisplayName("test client is on the database")
  void testClientInDatabase() {
    Integer numberOfClients =
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM oauth2_registered_client", Integer.class);
    assertThat(numberOfClients, notNullValue());
    assertThat(numberOfClients, equalTo(1));
  }
}
