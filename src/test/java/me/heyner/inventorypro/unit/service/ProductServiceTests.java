package me.heyner.inventorypro.unit.service;

import me.heyner.inventorypro.repository.ProductRepository;
import me.heyner.inventorypro.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ProductServiceTests {
  // TODO - write me pls

  @MockitoBean private ProductRepository productRepository;

  @MockitoBean private UserService userService;

  public ProductServiceTests(ProductRepository productRepository, UserService userService) {
    this.productRepository = productRepository;
    this.userService = userService;
  }
}
