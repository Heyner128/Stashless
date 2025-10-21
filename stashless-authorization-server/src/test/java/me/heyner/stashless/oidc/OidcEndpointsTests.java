package me.heyner.stashless.oidc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OidcEndpointsTests {
  @Autowired TestRestTemplate restTemplate;

  @Test
  @DisplayName("well known endpoint works")
  void wellKnownEndpoint() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/.well-known/openid-configuration", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful(), equalTo(true));
  }
}
