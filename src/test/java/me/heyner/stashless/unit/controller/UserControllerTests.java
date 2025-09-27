package me.heyner.stashless.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.stream.Collectors;
import me.heyner.stashless.controller.UserController;
import me.heyner.stashless.dto.LoginUserDto;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UpdateUserDto;
import me.heyner.stashless.dto.UserDto;
import me.heyner.stashless.model.Authority;
import me.heyner.stashless.model.User;
import me.heyner.stashless.service.AuthenticationService;
import me.heyner.stashless.service.JwtService;
import me.heyner.stashless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(UserController.class)
class UserControllerTests {

  private final MockMvc mockMvc;
  private final ObjectMapper jacksonObjectMapper;
  private final ModelMapper modelMapper = new ModelMapper();
  @MockitoBean private final JwtService jwtService;
  @MockitoBean private final UserService userService;
  @MockitoBean private final AuthenticationService authenticationService;
  private User user;

  @Autowired
  public UserControllerTests(
      MockMvc mockMvc,
      UserService userService,
      AuthenticationService authenticationService,
      JwtService jwtService,
      ObjectMapper jacksonObjectMapper) {
    this.mockMvc = mockMvc;
    this.userService = userService;
    this.jwtService = jwtService;
    this.authenticationService = authenticationService;
    this.jacksonObjectMapper = jacksonObjectMapper;
  }

  @BeforeEach
  void createMockUser() {
    user = new User();
    user.setId(1L);
    user.setUsername("test");
    user.setPassword("teeeST1@");
    user.setEmail("test@test.com");
    user.setAuthorities(Set.of(Authority.USER));
  }

  @Test
  @WithMockUser(
      username = "test",
      password = "teeeST1@",
      authorities = {"USER"})
  @DisplayName("POST on /users returns OK if the body is valid")
  void testRegister() throws Exception {

    var mockRegisterUserDto = new RegisterUserDto();
    mockRegisterUserDto.setUsername("test");
    mockRegisterUserDto.setPassword("teeeST1@");
    mockRegisterUserDto.setMatchingPassword("teeeST1@");
    mockRegisterUserDto.setEmail("test@test.com");

    when(authenticationService.signUp(mockRegisterUserDto))
        .thenReturn(modelMapper.map(user, UserDto.class));

    var requestBuilder =
        MockMvcRequestBuilders.post("/users")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(mockRegisterUserDto));

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isCreated())
        .andExpect(
            content()
                .json(
                    jacksonObjectMapper.writeValueAsString(modelMapper.map(user, UserDto.class))));
  }

  @Test
  @WithMockUser(
      username = "test",
      password = "teeeST1@",
      authorities = {"USER"})
  @DisplayName("POST on /users returns OK if the body is valid")
  void testRegisterWithNonMatchingPassword() throws Exception {

    var mockRegisterUserDto = new RegisterUserDto();
    mockRegisterUserDto.setUsername("test");
    mockRegisterUserDto.setPassword("teeeST1@");
    mockRegisterUserDto.setMatchingPassword("123456789");
    mockRegisterUserDto.setEmail("test@test.com");

    when(authenticationService.signUp(mockRegisterUserDto))
        .thenReturn(modelMapper.map(user, UserDto.class));

    var requestBuilder =
        MockMvcRequestBuilders.post("/users")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(mockRegisterUserDto));

    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(
      username = "test",
      password = "teeeST1@",
      authorities = {"USER"})
  @DisplayName("POST on /users/login returns OK if the body is valid")
  void testLogin() throws Exception {
    var mockLogin = new LoginUserDto();

    mockLogin.setUsername("test");
    mockLogin.setPassword("teeeST1@");

    when(authenticationService.authenticate(mockLogin))
        .thenReturn(modelMapper.map(user, UserDto.class));
    when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);
    when(jwtService.generateToken(any(User.class))).thenReturn("token");

    var requestBuilder =
        MockMvcRequestBuilders.post("/users/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(mockLogin));

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json("{\"token\":  \"token\", \"username\":  \"" + user.getUsername() + "\"}"));
  }

  @Test
  @WithMockUser(
      username = "test",
      password = "teeeST1@",
      authorities = {"USER"})
  @DisplayName("GET on /users/test returns OK and the user information")
  void testGetInfo() throws Exception {
    when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);

    var requestBuilder =
        MockMvcRequestBuilders.get("/users/test")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    jacksonObjectMapper.writeValueAsString(modelMapper.map(user, UserDto.class))));
  }

  @Test
  @WithMockUser(
      username = "test",
      password = "teeeST1@",
      authorities = {"USER"})
  @DisplayName("PUT on /users/test returns OK if the body is valid")
  void testUpdateInfo() throws Exception {

    when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);

    when(userService.updateUser(eq(user.getUsername()), any(UpdateUserDto.class)))
        .thenReturn(modelMapper.map(user, UserDto.class));

    UpdateUserDto updatedUserDto =
        new UpdateUserDto()
            .setUsername(user.getUsername())
            .setEmail(user.getEmail())
            .setAuthorities(
                user.getAuthorities().stream()
                    .map(authority -> Authority.valueOf(authority.getAuthority()))
                    .collect(Collectors.toSet()))
            .setOldPassword(user.getPassword())
            .setNewPassword("newPassword12@")
            .setNewMatchingPassword("newPassword12@");

    var requestBuilder =
        MockMvcRequestBuilders.put("/users/test")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(updatedUserDto));

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    jacksonObjectMapper.writeValueAsString(modelMapper.map(user, UserDto.class))));
  }

  @Test
  @WithMockUser(
      username = "test",
      password = "teeeST1@",
      authorities = {"USER"})
  @DisplayName("PUT on /users/test returns NOT OK if the new password doesn't match")
  void testUpdateInfoWithNonMatchingPassword() throws Exception {
    when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);

    when(userService.updateUser(eq(user.getUsername()), any(UpdateUserDto.class)))
        .thenReturn(modelMapper.map(user, UserDto.class));

    UpdateUserDto updatedUserDto =
        new UpdateUserDto()
            .setUsername(user.getUsername())
            .setEmail(user.getEmail())
            .setAuthorities(
                user.getAuthorities().stream()
                    .map(authority -> Authority.valueOf(authority.getAuthority()))
                    .collect(Collectors.toSet()))
            .setOldPassword(user.getPassword())
            .setNewPassword("newPassword")
            .setNewMatchingPassword("newPasswordNonMatching");

    var requestBuilder =
        MockMvcRequestBuilders.put("/users/test")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(updatedUserDto));

    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }
}
