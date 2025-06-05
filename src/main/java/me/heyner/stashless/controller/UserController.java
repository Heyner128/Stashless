package me.heyner.stashless.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import me.heyner.stashless.dto.LoginUserDto;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UpdateUserDto;
import me.heyner.stashless.dto.UserDto;
import me.heyner.stashless.model.Cookies;
import me.heyner.stashless.service.AuthenticationService;
import me.heyner.stashless.service.JwtService;
import me.heyner.stashless.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  private final AuthenticationService authenticationService;

  private final JwtService jwtService;

  private final ModelMapper modelMapper = new ModelMapper();
  
  @Value("${spring.profiles.active:prod}")
  private String profile;

  public UserController(
      UserService userService, JwtService jwtService, AuthenticationService authenticationService) {
    this.userService = userService;
    this.jwtService = jwtService;
    this.authenticationService = authenticationService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto register(@RequestBody @Valid RegisterUserDto registerUserDto) {
    return authenticationService.signUp(registerUserDto);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginUserDto loginUserDto) {
    UserDto authenticatedUser = authenticationService.authenticate(loginUserDto);

    String jwtToken =
        jwtService.generateToken(userService.loadUserByUsername(authenticatedUser.getUsername()));

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("token", jwtToken);
    responseBody.put("username", authenticatedUser.getUsername());

    ResponseCookie sessionCookie =
        ResponseCookie.from(Cookies.SESSION_TOKEN, jwtToken)
            .httpOnly(true)
            .domain(!profile.equals("dev") ? "heyner.me" : "localhost")
            .secure(!profile.equals("dev"))
            .sameSite("Lax")
            .path("/")
            .maxAge(jwtService.getJwtExpiration())
            .build();

    ResponseCookie userIdCookie =
        ResponseCookie.from(Cookies.USER_ID, jwtService.extractUsername(jwtToken))
            .httpOnly(false)
            .domain(!profile.equals("dev") ? "heyner.me" : "localhost")
            .secure(!profile.equals("dev"))
            .sameSite("Lax")
            .path("/")
            .maxAge(jwtService.getJwtExpiration())
            .build();

    return  ResponseEntity
        .ok()
        .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
        .header(HttpHeaders.SET_COOKIE, userIdCookie.toString())
        .body(responseBody);
  }

  @SecurityRequirement(name = "JWT token")
  @PostMapping("/{username}/logout")
  public ResponseEntity<Void> logout(@PathVariable String username) {
    ResponseCookie sessionCookie =
        ResponseCookie.from(Cookies.SESSION_TOKEN, "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .build();

    ResponseCookie userIdCookie =
        ResponseCookie.from(Cookies.USER_ID, "")
            .httpOnly(false)
            .secure(false)
            .path("/")
            .maxAge(0)
            .build();

    return ResponseEntity
        .noContent()
        .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
        .header(HttpHeaders.SET_COOKIE, userIdCookie.toString())
        .build();
  }


  @SecurityRequirement(name = "JWT token")
  @GetMapping("/{username}")
  public UserDto getUser(@PathVariable String username) {
    return modelMapper.map(userService.loadUserByUsername(username), UserDto.class);
  }

  @SecurityRequirement(name = "JWT token")
  @PutMapping("/{username}")
  public UserDto updateUser(@PathVariable String username, @RequestBody @Valid UpdateUserDto userDto) {
    return userService.updateUser(username, userDto);
  }
}
