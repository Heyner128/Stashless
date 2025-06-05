package me.heyner.stashless.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.heyner.stashless.validation.PasswordMatches;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
@PasswordMatches(message = "The passwords don't match")
public class RegisterUserDto {

  @NotBlank(message = "The email can't be empty")
  @Email
  private String email;

  @NotBlank(message = "The username can't be empty")
  @Size(min = 4, message = "The minimum username length is 4 characters")
  private String username;

  @NotBlank(message = "The password can't be empty")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "The password should contain an uppercase letter, a lowercase letter, a digit an special character in @$!%*?& and be at least 8 characters long")
  private String password;

  @NotBlank(message = "The matching password can't be empty")
  private String matchingPassword;

  public boolean passwordMatches() {
    return password.equals(matchingPassword);
  }
}
