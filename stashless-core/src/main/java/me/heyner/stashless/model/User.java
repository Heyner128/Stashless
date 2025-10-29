package me.heyner.stashless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@Table(
    name = "application_user",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = "email"),
      @UniqueConstraint(columnNames = "username")
    })
public class User implements UserDetails {

  @JsonIgnore
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Nullable
  private Long id;

  @Column(nullable = false)
  @Nullable
  private String email;

  @Column(nullable = false)
  @Nullable
  private String username;

  @Column(nullable = false)
  @JsonIgnore
  @ToString.Exclude
  @Nullable
  private String password;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @NonNull
  private Set<Authority> authorities = new HashSet<>();

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> objectEffectiveClass = o.getClass();
    if (o instanceof HibernateProxy objectHibernateProxy) {
      objectEffectiveClass =
          objectHibernateProxy.getHibernateLazyInitializer().getPersistentClass();
    }
    Class<?> thisEffectiveClass = this.getClass();
    if (this instanceof HibernateProxy thisHibernateProxy) {
      thisEffectiveClass = thisHibernateProxy.getHibernateLazyInitializer().getPersistentClass();
    }
    if (objectEffectiveClass != thisEffectiveClass) {
      return false;
    }
    User user = (User) o;
    return getId() != null && Objects.equals(getId(), user.getId());
  }

  @Override
  public final int hashCode() {
    int hashCode = this.getClass().hashCode();
    if (this instanceof HibernateProxy hibernateProxy) {
      hashCode = hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
    }
    return hashCode;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities.stream()
        .map(authority -> new SimpleGrantedAuthority(authority.toString()))
        .toList();
  }
}
