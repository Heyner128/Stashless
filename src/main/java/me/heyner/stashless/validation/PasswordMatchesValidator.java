package me.heyner.stashless.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.dto.UpdateUserDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
  @Override
  public void initialize(PasswordMatches constraintAnnotation) {}

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext context) {
    return switch (obj) {
      case UpdateUserDto user ->
          user.getOldPassword() == null
              || Objects.equals(user.getNewPassword(), user.getNewMatchingPassword());
      case RegisterUserDto registerUser ->
          registerUser.getPassword() != null
              && registerUser.getPassword().equals(registerUser.getMatchingPassword());
      default -> false;
    };
  }
}
