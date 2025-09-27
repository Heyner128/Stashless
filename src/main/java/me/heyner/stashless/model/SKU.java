package me.heyner.stashless.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Entity
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "sku")
public class SKU {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Nullable
  private UUID id;

  @Column(nullable = false)
  @Nullable
  private String name;

  @Column(nullable = false)
  @Nullable
  private BigDecimal costPrice;

  @Column(nullable = false)
  @Nullable
  private Long amountAvailable;

  @Column(nullable = false)
  private int marginPercentage;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  @ToString.Exclude
  @Nullable
  private Product product;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "sku_options",
      joinColumns = @JoinColumn(name = "sku_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "option_value_id", referencedColumnName = "id"),
      uniqueConstraints =
          @UniqueConstraint(columnNames = {"sku_id", "option_id", "option_value_id"}))
  @MapKeyJoinColumn(name = "option_id", referencedColumnName = "id")
  @NonNull
  private Map<Option, OptionValue> options = new HashMap<>();

  @CreationTimestamp @Nullable private Date createdAt;

  @UpdateTimestamp @Nullable private Date updateAt;

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
    SKU sku = (SKU) o;
    return getId() != null && Objects.equals(getId(), sku.getId());
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
