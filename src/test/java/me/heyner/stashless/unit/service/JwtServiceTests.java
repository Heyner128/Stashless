package me.heyner.stashless.unit.service;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.ExpiredJwtException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import me.heyner.stashless.model.User;
import me.heyner.stashless.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtServiceTests {

  private final JwtService jwtService;

  private final String mockJwt =
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzIzODM3LCJleHAiOjM2MDAwMDE3MjM4Mzd9.5VK0QhQXUNKycnjzMzysffoxSU97ec7KEsgxbfzAoPIghV2ki4b5rNqlkxB47JWszmp3uuIHztZ2qAEctC1DMA";

  private final String mockExpiredJwt =
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzIzODM3LCJleHAiOjE3MjM4Mzd9.XakR6qR4C6MYIkUZzR2kf2py2LL5C0RwUV_vhWl0L93ZV0LVk5L4YsxcC6RpgjZLuQ6aR0_J1Wpi6pECG6NO1Q";

  private final String mockInvalidUsernameJwt =
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0aW52YWxpZCIsImlhdCI6MTcyMzgzNywiZXhwIjozNjAwMDAxNzIzODM3fQ.LHjO2oCP-Pra4zp06FF3x0DUNTpjUZi1ZVPHW_Gd-emlBuvm7iqh40w-MWbl8Lirep6SQxYUciZAWOwoKSLpAQ";

  private final Long currentMillis = 1723837011L;
  private final Long jwtExpiration = 3600000000000000L;
  private User user;

  @Autowired
  public JwtServiceTests(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @BeforeEach
  void setUpMockUser() {
    user = new User();
    user.setId(1L);
    user.setUsername("test");
    user.setPassword("teeeST1@");
    user.setEmail("test@test.com");
  }

  @Test
  void testBuildToken() {
    String generatedJwt =
        jwtService.buildToken(
            new HashMap<>(),
            user,
            Clock.fixed(Instant.ofEpochMilli(currentMillis), ZoneId.of("UTC")),
            jwtExpiration);
    assertEquals(mockJwt, generatedJwt);
  }

  @Test
  void testGenerateToken() {
    String generatedToken1 = jwtService.generateToken(user);
    String generatedToken2 = jwtService.generateToken(new HashMap<>(), user);
    assertEquals(generatedToken1, generatedToken2);
  }

  @Test
  void testExtractUsername() {
    String username = jwtService.extractUsername(mockJwt);
    assertEquals(user.getUsername(), username);
  }

  @Test
  void testIsTokenValid() {
    assertTrue(jwtService.isTokenValid(mockJwt, user));
    assertFalse(jwtService.isTokenValid(mockInvalidUsernameJwt, user));
    assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(mockExpiredJwt, user));
  }

  @Test
  void testIsTokenExpired() {
    assertFalse(jwtService.isTokenExpired(mockJwt));
    assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(mockExpiredJwt));
  }

  @Test
  void testExtractExpiration() {
    Date expirationDate = jwtService.extractExpiration(mockJwt);
    assertTrue(Math.abs(currentMillis + jwtExpiration - expirationDate.getTime()) <= 1000);
  }
}
