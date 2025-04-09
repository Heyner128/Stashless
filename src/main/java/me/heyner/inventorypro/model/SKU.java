package me.heyner.inventorypro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"option_id", "option_value_id"}))
public class SKU {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(nullable = false)
  @ToString.Exclude
  private Product product;

  @Column(nullable = false)
  private BigDecimal costPrice;

  @Column(nullable = false)
  private Long amountAvailable;

  @Column(nullable = false)
  private int marginPercentage;

  @ManyToOne
  @JoinColumn(nullable = false)
  @ToString.Exclude
  private Option option;

  @ManyToOne
  @JoinColumn(nullable = false)
  @ToString.Exclude
  private OptionValue optionValue;

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
