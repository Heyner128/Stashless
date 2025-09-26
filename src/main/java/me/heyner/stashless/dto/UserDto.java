package me.heyner.stashless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
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
public class UserDto {
  @Setter @NotNull private String username;

  @NotNull @ValidEmail @Setter private String email;

  @Size(min = 1)
  private Set<Authority> authorities;

  @Setter
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  // Spring Security shit don't touch
  public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
    this.authorities =
        authorities.stream()
            .map(authority -> Authority.valueOf(authority.getAuthority()))
            .collect(Collectors.toSet());
  }

  @JsonSetter
  public UserDto setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
    return this;
  }
}
