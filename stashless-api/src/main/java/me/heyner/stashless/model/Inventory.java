package me.heyner.stashless.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import java.util.HashMap;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "name"})})
public class Inventory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Nullable
  private UUID id;

  @Column(nullable = false)
  @Nullable
  private String name;

  @Nullable private String description;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "inventory_items",
      joinColumns =
          @JoinColumn(name = "inventory_id", referencedColumnName = "id", nullable = false))
  @Column(name = "quantity", nullable = false)
  @MapKeyJoinColumn(name = "sku_id", referencedColumnName = "id")
  @NonNull
  private Map<SKU, Integer> items = new HashMap<>();

  @ManyToOne
  @JoinColumn(nullable = false)
  @Nullable
  private User user;

  @CreationTimestamp @Nullable private Date createdAt;

  @UpdateTimestamp @Nullable private Date updatedAt;

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
    if (thisEffectiveClass != objectEffectiveClass) {
      return false;
    }
    Inventory inventory = (Inventory) o;
    return getId() != null && Objects.equals(getId(), inventory.getId());
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
