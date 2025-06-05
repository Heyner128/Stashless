package me.heyner.stashless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.heyner.stashless.model.Authority;
import me.heyner.stashless.validation.PasswordMatches;
import me.heyner.stashless.validation.ValidEmail;

import org.springframework.security.core.GrantedAuthority;

@Getter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
@PasswordMatches
public class UpdateUserDto {
  @Setter @NotNull private String username;

  @NotNull @ValidEmail @Setter private String email;

  @Size(min = 1)
  private List<Authority> authorities;

  @Setter
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String oldPassword;

  @Setter
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "The password should contain an uppercase letter, a lowercase letter, a digit an special character in @$!%*?& and be at least 8 characters long")
  private String newPassword;

  @Setter
  private String newMatchingPassword;

  public boolean passwordMatches() {
    return newPassword.equals(newMatchingPassword);
  }

  // Spring Security shit don't touch
  public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
    this.authorities =
        authorities.stream().map(authority -> Authority.valueOf(authority.getAuthority())).toList();
  }

  @JsonSetter
  public UpdateUserDto setAuthorities(List<Authority> authorities) {
    this.authorities = authorities;
    return this;
  }
}
