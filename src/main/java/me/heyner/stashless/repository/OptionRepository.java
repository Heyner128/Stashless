package me.heyner.stashless.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.heyner.stashless.model.Option;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface OptionRepository extends ListCrudRepository<Option, UUID> {
    @Query("SELECT o FROM Option o WHERE o.product.id = :productId")
    List<Option> findByProductId(UUID productId);

    @Query("SELECT o FROM Option o WHERE o.name = :name AND o.product.id = :productId")
    Optional<Option> findByNameAndProductId(UUID productId, String name);
}
