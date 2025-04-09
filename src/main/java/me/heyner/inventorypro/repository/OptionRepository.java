package me.heyner.inventorypro.repository;

import java.util.List;
import java.util.UUID;
import me.heyner.inventorypro.model.Option;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface OptionRepository extends ListCrudRepository<Option, UUID> {

  @Query("select o from Option o where o.product.id = ?1")
  List<Option> findByProduct_Id(UUID id);
}
