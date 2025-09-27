package me.heyner.stashless.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.heyner.stashless.dto.InventoryInputDto;
import me.heyner.stashless.dto.InventoryItemInputDto;
import me.heyner.stashless.dto.InventoryItemOutputDto;
import me.heyner.stashless.dto.InventoryOutputDto;
import me.heyner.stashless.dto.LoginUserDto;
import me.heyner.stashless.dto.OptionInputDto;
import me.heyner.stashless.dto.OptionOutputDto;
import me.heyner.stashless.dto.ProductInputDto;
import me.heyner.stashless.dto.ProductOutputDto;
import me.heyner.stashless.dto.RegisterUserDto;
import me.heyner.stashless.repository.InventoryRepository;
import me.heyner.stashless.repository.OptionRepository;
import me.heyner.stashless.repository.ProductRepository;
import me.heyner.stashless.repository.SKURepository;
import me.heyner.stashless.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryIntegrationTests {

  @Autowired TestRestTemplate restTemplate;

  @Autowired InventoryRepository inventoryRepository;

  @Autowired ProductRepository productRepository;

  @Autowired SKURepository skuRepository;

  @Autowired OptionRepository optionRepository;

  @Autowired UserRepository userRepository;

  private String loginToken;

  @BeforeEach
  void setup() {
    String username = "test";
    String password = "teeeST@1";
    String email = "test@test.com";

    inventoryRepository.deleteAll();

    skuRepository.deleteAll();

    optionRepository.deleteAll();

    productRepository.deleteAll();

    userRepository.deleteAll();

    RegisterUserDto registerUserDto =
        new RegisterUserDto()
            .setEmail(email)
            .setUsername(username)
            .setPassword(password)
            .setMatchingPassword(password);

    restTemplate.postForLocation("/users", registerUserDto);

    LoginUserDto loginUserDto = new LoginUserDto().setUsername(username).setPassword(password);

    this.loginToken =
        (String) restTemplate.postForObject("/users/login", loginUserDto, Map.class).get("token");
  }

  public ResponseEntity<InventoryOutputDto> createInventory() {
    InventoryInputDto inventoryInputDto = new InventoryInputDto().setName("MyTestInventory");

    RequestEntity<InventoryInputDto> request =
        RequestEntity.post("/users/test/inventory")
            .header("Authorization", "Bearer " + loginToken)
            .body(inventoryInputDto, InventoryInputDto.class);
    return restTemplate.exchange(request, InventoryOutputDto.class);
  }

  public ResponseEntity<ProductOutputDto> createProduct() {
    ProductInputDto productInputDto =
        new ProductInputDto()
            .setName("T-Shirt")
            .setBrand("Adidas")
            .setDescription("An adidas t-shirt");

    RequestEntity<ProductInputDto> requestProductCreation =
        RequestEntity.post("/users/test/products")
            .header("Authorization", "Bearer " + loginToken)
            .body(productInputDto, ProductInputDto.class);

    return restTemplate.exchange(requestProductCreation, ProductOutputDto.class);
  }

  public ResponseEntity<OptionOutputDto> createOption(UUID productUuid) {
    OptionInputDto optionInputDto =
        new OptionInputDto().setName("Color").setValues(Set.of("Red", "Blue", "Orange"));

    RequestEntity<OptionInputDto> requestOptionCreation =
        RequestEntity.post("/users/test/products/" + productUuid + "/options")
            .header("Authorization", "Bearer " + loginToken)
            .body(optionInputDto, OptionInputDto.class);

    return restTemplate.exchange(requestOptionCreation, OptionOutputDto.class);
  }

  @Test
  void createInventoryTest() {
    ResponseEntity<InventoryOutputDto> response = createInventory();
    assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    assertNotNull(response.getBody());
    assertThat(response.getBody().getName(), equalTo("MyTestInventory"));
  }

  @Test
  void getInventory() {
    InventoryOutputDto createdInventory = createInventory().getBody();
    assertNotNull(createdInventory);
    RequestEntity<Void> request =
        RequestEntity.get("/users/test/inventory/" + createdInventory.getId())
            .header("Authorization", "Bearer " + loginToken)
            .build();

    ResponseEntity<InventoryOutputDto> response =
        restTemplate.exchange(request, InventoryOutputDto.class);

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(response.getBody(), equalTo(createdInventory));
  }

  @Test
  void updateInventory() {
    InventoryOutputDto createdInventory = createInventory().getBody();

    InventoryInputDto inventoryInputDto = new InventoryInputDto().setName("UpdatedTestInventory");

    assertNotNull(createdInventory);
    RequestEntity<InventoryInputDto> request =
        RequestEntity.put("/users/test/inventory/" + createdInventory.getId())
            .header("Authorization", "Bearer " + loginToken)
            .body(inventoryInputDto, InventoryInputDto.class);

    ResponseEntity<InventoryOutputDto> response =
        restTemplate.exchange(request, InventoryOutputDto.class);

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertNotNull(response.getBody());
    assertThat(response.getBody().getName(), equalTo(inventoryInputDto.getName()));
  }

  @Test
  void deleteInventory() {
    InventoryOutputDto createdInventory = createInventory().getBody();
    assertNotNull(createdInventory);
    RequestEntity<Void> request =
        RequestEntity.delete("/users/test/inventory/" + createdInventory.getId())
            .header("Authorization", "Bearer " + loginToken)
            .build();

    ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);

    assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
  }

  @Test
  void addInventoryItem() {
    InventoryOutputDto createdInventory = createInventory().getBody();

    ProductOutputDto createdProduct = createProduct().getBody();

    assertNotNull(createdProduct);
    OptionOutputDto createdOption = createOption(createdProduct.getId()).getBody();

    assertNotNull(createdOption);
    assertThat(createdOption.getName(), notNullValue());
    assertNotNull(createdOption.getValues());
    assertThat(createdOption.getValues().size(), equalTo(3));

    InventoryItemInputDto inventoryItemInputDto =
        new InventoryItemInputDto()
            .setName("Shirt")
            .setAmountAvailable(1L)
            .setMarginPercentage(10)
            .setProductUuid(createdProduct.getId())
            .setOptions(
                Map.of(createdOption.getName(), createdOption.getValues().iterator().next()))
            .setCostPrice(BigDecimal.valueOf(1L))
            .setQuantity(99);

    assertNotNull(createdInventory);
    RequestEntity<InventoryItemInputDto> requestInventoryCreation =
        RequestEntity.post("/users/test/inventory/" + createdInventory.getId() + "/item")
            .header("Authorization", "Bearer " + loginToken)
            .body(inventoryItemInputDto, InventoryItemInputDto.class);

    ResponseEntity<InventoryItemOutputDto> responseInventoryCreation =
        restTemplate.exchange(requestInventoryCreation, InventoryItemOutputDto.class);

    assertThat(responseInventoryCreation.getStatusCode(), equalTo(HttpStatus.CREATED));
  }

  @Test
  void deleteInventoryItem() {
    InventoryOutputDto createdInventory = createInventory().getBody();

    InventoryItemInputDto inventoryItemInputDto = new InventoryItemInputDto().setQuantity(99);

    assertNotNull(createdInventory);
    RequestEntity<InventoryItemInputDto> requestInventoryCreation =
        RequestEntity.post("/users/test/inventory/" + createdInventory.getId() + "/item")
            .header("Authorization", "Bearer " + loginToken)
            .body(inventoryItemInputDto, InventoryItemInputDto.class);

    restTemplate.exchange(requestInventoryCreation, InventoryItemOutputDto.class);

    RequestEntity<Void> requestInventoryItemDeletion =
        RequestEntity.delete("/users/test/inventory/" + createdInventory.getId())
            .header("Authorization", "Bearer " + loginToken)
            .build();

    ResponseEntity<Void> responseInventoryItemDeletion =
        restTemplate.exchange(requestInventoryItemDeletion, Void.class);

    assertThat(responseInventoryItemDeletion.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
  }
}
