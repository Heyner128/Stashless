package me.heyner.stashless.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.*;
import me.heyner.stashless.dto.ProductOutputDto;
import me.heyner.stashless.model.Product;
import me.heyner.stashless.model.User;
import me.heyner.stashless.repository.ProductRepository;
import me.heyner.stashless.service.ProductService;
import me.heyner.stashless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ProductServiceTests {

  private Product product;

  private User user;

  private ProductOutputDto productOutputDto;

  @MockitoBean private ProductRepository productRepository;

  @MockitoBean private UserService userService;

  private final ProductService productService;

  @Autowired
  public ProductServiceTests(
      ProductRepository productRepository, UserService userService, ProductService productService) {
    this.productRepository = productRepository;
    this.userService = userService;
    this.productService = productService;
  }

  @BeforeEach
  void setUp() {
    setupMockProduct();
    setupMockUser();
    setupDto();
    setupMockBeans();
  }

  void setupMockProduct() {
    product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("Test Product");
    product.setDescription("Test Description");
    product.setBrand("Test Brand");
  }

  void setupMockUser() {
    user = new User();
    user.setId(1L);
    user.setUsername("testUser");
    user.setEmail("");
    user.setPassword("testPassword");
    user.setAuthorities(new HashSet<>());
  }

  void setupDto() {
    productOutputDto = new ProductOutputDto();
    productOutputDto.setId(UUID.randomUUID());
    productOutputDto.setName("Test Product");
    productOutputDto.setDescription("Test Description");
    productOutputDto.setBrand("Test Brand");
  }

  void setupMockBeans() {
    when(productRepository.findById(productOutputDto.getId()))
        .thenReturn(Optional.ofNullable(product));

    when(productRepository.findByUser_username(user.getUsername())).thenReturn(List.of(product));

    when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);
  }

  @Test
  void testGetProducts() {
    List<ProductOutputDto> products = productService.getProducts("testUser");
    assertEquals(1, products.size());
    assertEquals(product.getName(), products.get(0).getName());
    assertEquals(product.getDescription(), products.get(0).getDescription());
    assertEquals(product.getBrand(), products.get(0).getBrand());
  }
}
