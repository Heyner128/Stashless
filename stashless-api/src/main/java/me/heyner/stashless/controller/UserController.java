package me.heyner.stashless.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UpdateUserDto;
import me.heyner.stashless.dto.UserDto;
import me.heyner.stashless.service.AuthenticationService;
import me.heyner.stashless.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  private final AuthenticationService authenticationService;

  private final ModelMapper modelMapper = new ModelMapper();

  public UserController(UserService userService, AuthenticationService authenticationService) {
    this.userService = userService;
    this.authenticationService = authenticationService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto register(@RequestBody @Valid RegisterUserDto registerUserDto) {
    return authenticationService.signUp(registerUserDto);
  }

  @SecurityRequirement(name = "JWT token")
  @GetMapping("/{username}")
  public UserDto getUser(@PathVariable String username) {
    return modelMapper.map(userService.loadUserByUsername(username), UserDto.class);
  }

  @SecurityRequirement(name = "JWT token")
  @PutMapping("/{username}")
  public UserDto updateUser(
      @PathVariable String username, @RequestBody @Valid UpdateUserDto userDto) {
    return userService.updateUser(username, userDto);
  }
}
