package me.heyner.stashless.unit.service;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import me.heyner.stashless.model.*;
import me.heyner.stashless.repository.OptionRepository;
import me.heyner.stashless.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OptionServiceTests {

  private Option mockOption;

  private Product mockProduct;

  @MockitoBean private OptionRepository optionRepository;

  @Autowired private ProductRepository productRepository;

  @Autowired
  public OptionServiceTests(OptionRepository optionRepository) {
    this.optionRepository = optionRepository;
  }

  public void setUpMocks() {
    mockProduct =
        new Product()
            .setId(UUID.randomUUID())
            .setName("Shirt")
            .setDescription("A t-shirt")
            .setBrand("Adidas")
            .setUser(
                new User()
                    .setId(1L)
                    .setEmail("test@test.com")
                    .setPassword("TEst@1")
                    .setUsername("test")
                    .setAuthorities(Set.of(Authority.USER)));

    mockOption =
        new Option()
            .setId(UUID.randomUUID())
            .setName("Color")
            .setValues(Set.of(new OptionValue().setId(1L).setValue("Red")));
  }

  @BeforeEach
  public void setUp() {
    setUpMocks();
    when(productRepository.findById(mockProduct.getId()))
        .thenReturn(Optional.ofNullable(mockProduct));

    when(productRepository.save(mockProduct)).thenReturn(mockProduct);

    when(optionRepository.save(mockOption)).thenReturn(mockOption);
  }
}
