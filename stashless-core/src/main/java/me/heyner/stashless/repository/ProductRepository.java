package me.heyner.stashless.repository;

import java.util.List;
import java.util.UUID;
import me.heyner.stashless.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductRepository extends ListCrudRepository<Product, UUID> {

  @Query("select p from Product p where p.user.username = ?1")
  List<Product> findByUser_username(String username);
}
