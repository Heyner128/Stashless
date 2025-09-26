package me.heyner.stashless.service;

import me.heyner.stashless.dto.UpdateUserDto;
import me.heyner.stashless.dto.UserDto;
import me.heyner.stashless.exception.EntityNotFoundException;
import me.heyner.stashless.model.User;
import me.heyner.stashless.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  public User findByEmail(String email) throws EntityNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    logger.info("Found user: {}", user);
    return user;
  }

  public UserDto updateUser(String username, UpdateUserDto userDto) {
    User user = loadUserByUsername(username);
    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    user.setAuthorities(userDto.getAuthorities());
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
