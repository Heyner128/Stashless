package me.heyner.inventorypro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
public class LoginUserDto {
  @NotBlank(message = "The username can't be empty")
  private String username;

  @NotBlank(message = "The password can't be empty")
  private String password;
}
