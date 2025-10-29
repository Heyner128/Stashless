package me.heyner.stashless.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.Set;
import me.heyner.stashless.dto.LoginUserDto;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UpdateUserDto;
import me.heyner.stashless.dto.UserDto;
import me.heyner.stashless.model.Authority;
import me.heyner.stashless.repository.InventoryRepository;
import me.heyner.stashless.repository.OptionRepository;
import me.heyner.stashless.repository.ProductRepository;
import me.heyner.stashless.repository.SKURepository;
import me.heyner.stashless.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersIntegrationTests {

  @Autowired TestRestTemplate restTemplate;
  @Autowired InventoryRepository inventoryRepository;
  @Autowired SKURepository skuRepository;
  @Autowired OptionRepository optionRepository;
  @Autowired ProductRepository productRepository;
  @Autowired UserRepository userRepository;

  @BeforeEach
  void setUp() {
    inventoryRepository.deleteAll();

    skuRepository.deleteAll();

    optionRepository.deleteAll();

    productRepository.deleteAll();

    userRepository.deleteAll();
  }

  public ResponseEntity<UpdateUserDto> registerRequest() {
    RegisterUserDto registerUserDto =
        new RegisterUserDto()
            .setEmail("test@test.com")
            .setUsername("test")
            .setPassword("teeeST@1")
            .setMatchingPassword("teeeST@1");

    return restTemplate.postForEntity("/users", registerUserDto, UpdateUserDto.class);
  }

  public ResponseEntity<Map> loginRequest() {
    LoginUserDto loginUserDto = new LoginUserDto().setUsername("test").setPassword("teeeST@1");

    return restTemplate.postForEntity("/users/login", loginUserDto, Map.class);
  }

  @Test
  @DisplayName(
      "POST to /users with email, username and password returns OK and the user information ")
  public void testRegisterUser() {

    var response = registerRequest();

    assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    assertNotNull(response.getBody());
    assertThat(response.getBody().getUsername(), equalTo("test"));
    assertThat(response.getBody().getEmail(), equalTo("test@test.com"));
  }

  @Test
  @DisplayName("POST to /login with email and password logs in the user")
  void testLoginUser() {
    // Registers a new user
    registerRequest();

    // Tests the login
    var response = loginRequest();

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertNotNull(response.getBody());
    assertThat(response.getBody().get("token"), notNullValue());
    assertThat(response.getBody().get("username"), equalTo("test"));
  }

  @Test
  void testGetUserDetails() {

    // Registers a new user
    registerRequest();

    // Logs in the user
    var loginResponse = loginRequest();

    // creates an authenticated request
    assertNotNull(loginResponse.getBody());
    RequestEntity<Void> request =
        RequestEntity.get("/users/test")
            .header("Authorization", "Bearer " + loginResponse.getBody().get("token"))
            .build();

    // Gets the created user information
    ResponseEntity<UserDto> response = restTemplate.exchange(request, UserDto.class);

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertNotNull(response.getBody());
    assertThat(response.getBody().getUsername(), equalTo("test"));
    assertThat(response.getBody().getEmail(), equalTo("test@test.com"));
  }

  @Test
  void testUpdateUserDetails() {

    // Registers a new user
    registerRequest();

    // Logs in the user
    var loginResponse = loginRequest();

    // creates an authenticated request
    assertNotNull(loginResponse.getBody());
    RequestEntity<UpdateUserDto> request =
        RequestEntity.put("/users/test")
            .header("Authorization", "Bearer " + loginResponse.getBody().get("token"))
            .body(
                new UpdateUserDto()
                    .setUsername("test2")
                    .setEmail("test@test.com")
                    .setAuthorities(Set.of(Authority.USER)));

    // Gets the created user information
    ResponseEntity<UserDto> response = restTemplate.exchange(request, UserDto.class);

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getBody().getUsername(), notNullValue());
    assertThat(response.getBody().getEmail(), notNullValue());
    assertThat(response.getBody().getUsername(), equalTo("test2"));
    assertThat(response.getBody().getEmail(), equalTo("test@test.com"));
  }
}
