package me.heyner.stashless.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.Nullable;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "name"}))
@NoArgsConstructor
public class Option {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Nullable
  private UUID id;

  @Column(nullable = false)
  @Nullable
  private String name;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  @Nullable
  private Product product;

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      mappedBy = "option",
      orphanRemoval = true)
  @Nullable
  private Set<OptionValue> values;

  @CreationTimestamp @Nullable private Date createdAt;

  @UpdateTimestamp @Nullable private Date updateAt;

  public Option setValues(Set<OptionValue> values) {
    if (this.values == null) {
      this.values = new HashSet<>();
    }
    this.values.clear();
    values.forEach(value -> value.setOption(this));
    this.values.addAll(values);
    return this;
  }

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
    Option option = (Option) o;
    return getId() != null && Objects.equals(getId(), option.getId());
  }

  @Override
  public final int hashCode() {
    int hashCode = this.getClass().hashCode();
    if (this instanceof HibernateProxy hibernateProxy) {
      hashCode = hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
    }
    return hashCode;
  }
}
