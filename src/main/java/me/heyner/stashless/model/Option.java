package me.heyner.stashless.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

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
  private UUID id;

  @Column(nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      mappedBy = "option",
      orphanRemoval = true)
  private Set<OptionValue> values;

  @CreationTimestamp private Date createdAt;

  @UpdateTimestamp private Date updateAt;

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
    Class<?> objectEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != objectEffectiveClass) {
      return false;
    }
    Option option = (Option) o;
    return getId() != null && Objects.equals(getId(), option.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
