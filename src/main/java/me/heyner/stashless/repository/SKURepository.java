package me.heyner.stashless.repository;

import java.util.List;
import java.util.UUID;
import me.heyner.stashless.model.SKU;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface SKURepository extends ListCrudRepository<SKU, UUID> {
    @Query("SELECT s FROM SKU s WHERE s.product.id = :productId")
    List<SKU> findByProductId(UUID productId);
}
