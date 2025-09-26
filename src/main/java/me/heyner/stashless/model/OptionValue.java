package me.heyner.stashless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(
    uniqueConstraints =
        @UniqueConstraint(
            columnNames = {"option_id", "value"},
            name = "uq_option_id_value"))
@NoArgsConstructor
public class OptionValue {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(nullable = false)
  private String value;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "option_id", nullable = false)
  private Option option;

  @ManyToMany(mappedBy = "options", fetch = FetchType.LAZY)
  Set<SKU> skus;

  @CreationTimestamp private Date createdDate;

  @UpdateTimestamp private Date updateDate;

  @PreRemove
  private void preRemove() {
    if (skus != null) {
      skus.forEach(sku -> sku.getOptions().remove(this.getOption()));
    }
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    OptionValue optionValue = (OptionValue) o;
    return getId() != null && Objects.equals(getId(), optionValue.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
