package me.heyner.stashless.service;

import jakarta.validation.Valid;
import java.util.Set;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UpdateUserDto;
import me.heyner.stashless.dto.UserDto;
import me.heyner.stashless.exception.ExistingEntityException;
import me.heyner.stashless.model.Authority;
import me.heyner.stashless.model.User;
import me.heyner.stashless.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;
  private final ModelMapper modelMapper = new ModelMapper();
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
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

  public UserDto updateUser(String username, UpdateUserDto userDto) {
    User user = loadUserByUsername(username);
    if (userDto.getUsername() != null) {
      user.setUsername(userDto.getUsername());
    }
    if (userDto.getEmail() != null) {
      user.setEmail(userDto.getEmail());
    }
    if (userDto.getAuthorities() != null) {
      user.setAuthorities(userDto.getAuthorities());
    }
    if (userDto.getNewPassword() != null) {
      if (!passwordEncoder.matches(userDto.getOldPassword(), user.getPassword())) {
        throw new IllegalArgumentException("Invalid password");
      }
      user.setPassword(userDto.getNewPassword());
    }

    User savedUser = userRepository.save(user);
    logger.info("Updated user: {}", user);
    return modelMapper.map(savedUser, UserDto.class);
  }

  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Not found"));

    logger.info("Found user: {}", user);
    return user;
  }
}
