package me.heyner.stashless.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class LoginUserDto {
  @SuppressWarnings("NullAway.Init")
  @NotNull
  @NotBlank(message = "The username can't be empty")
  private String username;

  @SuppressWarnings("NullAway.Init")
  @NotNull
  @NotBlank(message = "The password can't be empty")
  private String password;
}
