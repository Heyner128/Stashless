package me.heyner.stashless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.heyner.stashless.model.Authority;
import me.heyner.stashless.validation.PasswordMatches;
import me.heyner.stashless.validation.ValidEmail;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@PasswordMatches
public class UpdateUserDto {
  @SuppressWarnings("NullAway.Init")
  @Setter
  @NotNull
  private String username;

  @SuppressWarnings("NullAway.Init")
  @NotNull
  @ValidEmail
  @Setter
  private String email;

  @Nullable
  @Size(min = 1)
  private Set<Authority> authorities;

  @Nullable
  @Setter
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String oldPassword;

  @Nullable
  @Setter
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "The password should contain an uppercase letter, a lowercase letter, a digit an special character in @$!%*?& and be at least 8 characters long")
  private String newPassword;

  @Nullable @Setter private String newMatchingPassword;

  // Spring Security shit don't touch
  public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
    this.authorities =
        authorities.stream()
            .map(authority -> Authority.valueOf(authority.getAuthority()))
            .collect(Collectors.toSet());
  }

  @JsonSetter
  public UpdateUserDto setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
    return this;
  }
}
