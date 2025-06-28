package me.heyner.stashless.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import me.heyner.stashless.dto.LoginUserDto;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UpdateUserDto;
import me.heyner.stashless.dto.UserDto;
import me.heyner.stashless.exception.EntityNotFoundException;
import me.heyner.stashless.model.Authority;
import me.heyner.stashless.model.User;
import me.heyner.stashless.repository.UserRepository;
import me.heyner.stashless.service.AuthenticationService;
import me.heyner.stashless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class UserAuthenticationServiceTests {

  private static final String MOCK_USER = "test";
  private static final String MOCK_EMAIL = "test@test.com";
  private static final String MOCK_PASSWORD = "teeeST1@";
  private static final String ENCODED_MOCK_PASSWORD = "encodedMockPasswordST1@";
  private static final String FAKE_EMAIL = "fakeemail@fakemeil.com";
  private static final String FAKE_PASSWORD = "fakepassword";

  private final ModelMapper modelMapper = new ModelMapper();
  private final UserService userService;
  private final AuthenticationService authenticationService;
  private User user;
  private LoginUserDto loginUserDto;
  private LoginUserDto invalidLoginUserDto;
  private RegisterUserDto registerUserDto;
  private UpdateUserDto updateUserDto;

  @MockitoBean private UserRepository userRepository;
  @MockitoBean private final PasswordEncoder passwordEncoder;
  @MockitoBean private AuthenticationManager authenticationManager;

  @Autowired
  public UserAuthenticationServiceTests(
      UserRepository userRepository,
      UserService userService,
      AuthenticationService authenticationService,
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder
    ) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.authenticationService = authenticationService;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  void setupMockUser() {
    user = new User();
    user.setId(1L);
    user.setUsername(MOCK_USER);
    user.setPassword(ENCODED_MOCK_PASSWORD);
    user.setEmail(MOCK_EMAIL);
    user.setAuthorities(Set.of(Authority.USER));
  }

  void setUpDto() {
    loginUserDto = new LoginUserDto();
    loginUserDto.setUsername(MOCK_USER);
    loginUserDto.setPassword(MOCK_PASSWORD);
    invalidLoginUserDto = new LoginUserDto();
    invalidLoginUserDto.setUsername(MOCK_USER);
    invalidLoginUserDto.setPassword(FAKE_PASSWORD);
    registerUserDto = new RegisterUserDto();
    registerUserDto.setUsername(MOCK_USER);
    registerUserDto.setPassword(MOCK_PASSWORD);
    registerUserDto.setMatchingPassword(MOCK_PASSWORD);
    registerUserDto.setEmail(MOCK_EMAIL);
    updateUserDto = new UpdateUserDto();
    updateUserDto.setUsername(user.getUsername());
    updateUserDto.setEmail(user.getEmail());
    updateUserDto.setAuthorities(user.getAuthorities());
    updateUserDto.setOldPassword(MOCK_PASSWORD);
    updateUserDto.setNewPassword("newPassword12@");
    updateUserDto.setNewMatchingPassword("newPassword12@");
  }

  void setupMockBeans() {
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userRepository.findByUsername(loginUserDto.getUsername())).thenReturn(Optional.of(user));
    when(userRepository.findByEmail(UserAuthenticationServiceTests.FAKE_EMAIL))
        .thenThrow(EntityNotFoundException.class);
    when(userRepository.findByUsername(UserAuthenticationServiceTests.FAKE_EMAIL))
        .thenReturn(Optional.empty());
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(MOCK_PASSWORD, ENCODED_MOCK_PASSWORD)).thenReturn(true);
    when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                invalidLoginUserDto.getUsername(), invalidLoginUserDto.getPassword())))
        .thenThrow(BadCredentialsException.class);
  }

  @BeforeEach
  void setUp() {
    setupMockUser();
    setUpDto();
    setupMockBeans();
  }

  @Test
  void testSignUp() {
    UserDto signedUpUser = modelMapper.map(authenticationService.signUp(registerUserDto), UserDto.class);
    assertNotEquals(signedUpUser.getPassword(), registerUserDto.getPassword());
    assertEquals(signedUpUser.getUsername(), user.getUsername());
    assertEquals(signedUpUser.getEmail(), user.getEmail());
  }

  @Test
  void testAuthenticate() {
    assertThrows(
        BadCredentialsException.class,
        () -> authenticationService.authenticate(invalidLoginUserDto));
    assertEquals(
        authenticationService.authenticate(loginUserDto), modelMapper.map(user, UserDto.class));
  }

  @Test
  void testFindByEmail() {
    User foundUser;
    foundUser = userService.findByEmail(user.getEmail());
    assertEquals(foundUser, user);
    assertThrows(
        EntityNotFoundException.class,
        () -> userService.findByEmail(UserAuthenticationServiceTests.FAKE_EMAIL));
  }

  @Test
  void testFindByUsername() {
    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));
    User foundUser = userService.loadUserByUsername(user.getUsername());
    assertEquals(foundUser, user);
    assertThrows(
        UsernameNotFoundException.class,
        () -> userService.loadUserByUsername("fakeusername@fakemeil.com"));
  }

  @Test
  void testUpdateUser() {
    UserDto updatedUser = userService.updateUser(user.getUsername(), updateUserDto);
    assertEquals(updatedUser.getUsername(), user.getUsername());
    assertEquals(updatedUser.getEmail(), user.getEmail());
  }

  @Test
  void testUpdateUserWithInvalidPassword() {
    updateUserDto.setOldPassword(FAKE_PASSWORD);
    String username = user.getUsername();
    assertThrows(IllegalArgumentException.class, () -> userService.updateUser(username, updateUserDto));
  }
}
