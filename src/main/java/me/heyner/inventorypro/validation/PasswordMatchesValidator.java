package me.heyner.inventorypro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.heyner.inventorypro.dto.RegisterUserDto;
import me.heyner.inventorypro.dto.UpdateUserDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {}

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        return switch (obj) {
            case UpdateUserDto user -> user.getOldPassword() == null || user.getNewPassword().equals(user.getNewMatchingPassword());
            case RegisterUserDto registerUser -> registerUser.getPassword() != null && registerUser.getPassword().equals(registerUser.getMatchingPassword());
            default -> false;
        };
        
    }

    
}
