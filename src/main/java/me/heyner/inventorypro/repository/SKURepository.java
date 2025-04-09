package me.heyner.inventorypro.repository;

import java.util.List;
import java.util.UUID;
import me.heyner.inventorypro.model.SKU;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface SKURepository extends ListCrudRepository<SKU, UUID> {
  @Query("select sku from SKU sku where sku.product.id = ?1")
  List<SKU> findByProduct_Id(UUID id);
}
