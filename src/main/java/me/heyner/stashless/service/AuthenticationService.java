package me.heyner.stashless.service;

import jakarta.validation.Valid;
import java.util.Set;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final ModelMapper modelMapper = new ModelMapper();

  public AuthenticationService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserDto authenticate(@Valid LoginUserDto loginUserDto)
      throws DisabledException, LockedException, BadCredentialsException {
    // TODO authenticate the user ...

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
              .setAuthorities(Set.of(Authority.USER))
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
