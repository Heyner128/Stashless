package me.heyner.stashless.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "name"})})
public class Inventory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  private String description;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "inventory_quantity",
      joinColumns = @JoinColumn(name = "inventory_id", referencedColumnName = "id"))
  @Column(name = "quantities")
  @MapKeyJoinColumn(name = "sku_id", referencedColumnName = "id")
  private Map<SKU, Integer> items;

  @ManyToOne
  @JoinColumn(nullable = false)
  private User user;

  @CreationTimestamp private Date createdAt;

  @UpdateTimestamp private Date updatedAt;

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
    Inventory inventory = (Inventory) o;
    return getId() != null && Objects.equals(getId(), inventory.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
