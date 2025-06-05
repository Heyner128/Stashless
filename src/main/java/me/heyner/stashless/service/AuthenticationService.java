package me.heyner.stashless.service;

import jakarta.validation.Valid;

import java.util.List;
import me.heyner.stashless.dto.LoginUserDto;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UserDto;
import me.heyner.stashless.exception.EntityNotFoundException;
import me.heyner.stashless.exception.ExistingEntityException;
import me.heyner.stashless.model.Authority;
import me.heyner.stashless.model.User;
import me.heyner.stashless.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final ModelMapper modelMapper = new ModelMapper();

  public AuthenticationService(
      AuthenticationManager authenticationManager,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserDto authenticate(@Valid LoginUserDto loginUserDto)
      throws DisabledException, LockedException, BadCredentialsException {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginUserDto.getUsername(), loginUserDto.getPassword()));

    return modelMapper.map(
        userRepository
            .findByUsername(loginUserDto.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("Not found")),
      UserDto.class);
  }

  public UserDto signUp(@Valid RegisterUserDto registerUserDto) throws ExistingEntityException {
    try {
      User user =
          new User()
              .setAuthorities(List.of(Authority.USER))
              .setEmail(registerUserDto.getEmail())
              .setUsername(registerUserDto.getUsername())
              .setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
      userRepository.save(user);
      return modelMapper.map(user, UserDto.class);
    } catch (DataIntegrityViolationException e) {
      throw new ExistingEntityException(registerUserDto.getEmail());
    }
  }
}
