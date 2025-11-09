package me.heyner.stashless.oidc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OidcEndpointsTests {

  @Autowired private JwtDecoder jwtDecoder;

  @TestConfiguration
  public static class PasswordEncoderConfiguration {
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
      return NoOpPasswordEncoder.getInstance();
    }
  }

  @Autowired TestRestTemplate restTemplate;
  @Autowired RegisteredClientRepository registeredClientRepository;
  private final String TEST_OIDC_CLIENT_ID = "test-oidc-client";

  @Test
  @DisplayName("well known endpoint works")
  void wellKnownEndpoint() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/.well-known/openid-configuration", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful(), equalTo(true));
  }

  @Test
  @DisplayName("token endpoint returns a valid jwt token")
  void tokenEndpoint() {
    RegisteredClient registeredClient =
        registeredClientRepository.findByClientId(TEST_OIDC_CLIENT_ID);
    assertThat(registeredClient, notNullValue());
    assertThat(registeredClient.getClientId(), equalTo(TEST_OIDC_CLIENT_ID));
    assertThat(registeredClient.getClientSecret(), notNullValue());
    final String authorizationEndpoint = "/oauth2/token";

    RequestEntity<?> request =
        RequestEntity.post(authorizationEndpoint)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(
                "grant_type=client_credentials&client_id="
                    + registeredClient.getClientId()
                    + "&client_secret="
                    + registeredClient.getClientSecret());

    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

    assertThat(response.getStatusCode().is2xxSuccessful(), equalTo(true));
  }
}
