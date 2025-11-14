package me.heyner.stashless.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UpdateUserDto;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UsersIntegrationTests {

  @Autowired MockMvc mvc;
  @Autowired TestRestTemplate restTemplate;
  @Autowired InventoryRepository inventoryRepository;
  @Autowired SKURepository skuRepository;
  @Autowired OptionRepository optionRepository;
  @Autowired ProductRepository productRepository;
  @Autowired UserRepository userRepository;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    inventoryRepository.deleteAll();

    skuRepository.deleteAll();

    optionRepository.deleteAll();

    productRepository.deleteAll();

    userRepository.deleteAll();
  }

  public UpdateUserDto createUser() throws Exception {
    RegisterUserDto registerUserDto =
        new RegisterUserDto()
            .setEmail("test@test.com")
            .setUsername("test")
            .setPassword("teeeST@1")
            .setMatchingPassword("teeeST@1");

    String response =
        mvc.perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerUserDto)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(response, UpdateUserDto.class);
  }

  @Test
  @DisplayName(
      "POST to /users with email, username and password returns OK and the user information ")
  public void testRegisterUser() throws Exception {

    UpdateUserDto createdUser = createUser();

    assertThat(createdUser.getUsername(), equalTo("test"));
    assertThat(createdUser.getEmail(), equalTo("test@test.com"));
  }

  @Test
  @WithMockUser(username = "test")
  void testUpdateUserDetails() throws Exception {

    UpdateUserDto created = createUser();

    UpdateUserDto toUpdate =
        new UpdateUserDto()
            .setUsername("test2")
            .setEmail("test@test.com")
            .setAuthorities(Set.of(Authority.USER));

    String response =
        mvc.perform(
                put("/users/" + created.getUsername())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(toUpdate)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UpdateUserDto updated = objectMapper.readValue(response, UpdateUserDto.class);

    assertThat(updated.getUsername(), notNullValue());
    assertThat(updated.getEmail(), notNullValue());
    assertThat(updated.getUsername(), equalTo("test2"));
    assertThat(updated.getEmail(), equalTo("test@test.com"));
  }
}
