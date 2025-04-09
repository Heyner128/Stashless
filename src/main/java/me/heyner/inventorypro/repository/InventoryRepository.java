package me.heyner.inventorypro.repository;

import java.util.List;
import java.util.UUID;
import me.heyner.inventorypro.model.Inventory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface InventoryRepository extends ListCrudRepository<Inventory, UUID> {

  @Query("select i from Inventory i where i.user.username = ?1")
  List<Inventory> findByUser_username(String username);
}
