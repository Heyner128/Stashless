package me.heyner.stashless.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

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
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private BigDecimal costPrice;

  @Column(nullable = false)
  private Long amountAvailable;

  @Column(nullable = false)
  private int marginPercentage;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  @ToString.Exclude
  private Product product;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "sku_options",
    joinColumns = @JoinColumn(name = "sku_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "option_value_id", referencedColumnName = "id"),
    uniqueConstraints = @UniqueConstraint(columnNames = {"option_id", "option_value_id"})
  )
  @MapKeyJoinColumn(name = "option_id", referencedColumnName = "id")
  private Map<Option, OptionValue> options;

  @CreationTimestamp private Date createdAt;

  @UpdateTimestamp private Date updateAt;

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
    SKU skuValue = (SKU) o;
    return getId() != null && Objects.equals(getId(), skuValue.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
