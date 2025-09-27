package me.heyner.stashless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.Nullable;

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
  @Nullable
  private Long id;

  @Column(nullable = false)
  @Nullable
  private String value;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "option_id", nullable = false)
  @Nullable
  private Option option;

  @ManyToMany(mappedBy = "options", fetch = FetchType.LAZY)
  @NonNull
  Set<SKU> skus = new HashSet<>();

  @CreationTimestamp @Nullable private Date createdDate;

  @UpdateTimestamp @Nullable private Date updateDate;

  @PreRemove
  private void preRemove() {
    skus.forEach(sku -> sku.getOptions().remove(this.getOption()));
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
    OptionValue optionValue = (OptionValue) o;
    return getId() != null && Objects.equals(getId(), optionValue.getId());
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
