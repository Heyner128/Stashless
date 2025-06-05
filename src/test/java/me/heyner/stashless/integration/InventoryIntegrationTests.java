package me.heyner.stashless.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.heyner.stashless.dto.*;
import me.heyner.stashless.repository.*;
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
        new RegisterUserDto().setEmail(email).setUsername(username).setPassword(password).setMatchingPassword(password);

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
        new OptionInputDto().setName("Color").setValues(List.of("Red", "Blue", "Orange"));

    RequestEntity<OptionInputDto> requestOptionCreation =
        RequestEntity.post("/users/test/products/" + productUuid + "/options")
            .header("Authorization", "Bearer " + loginToken)
            .body(optionInputDto, OptionInputDto.class);

    return restTemplate.exchange(requestOptionCreation, OptionOutputDto.class);
  }

  public ResponseEntity<SKUOutputDto> createSKU(UUID productUuid) {

    SKUInputDto skuInputDto =
        new SKUInputDto()
          .setName("ADITSHIRT")
          .setCostPrice(new BigDecimal("9.99"))
          .setAmountAvailable(99L)
          .setMarginPercentage(10)
          .setOptions(Map.of("Color", "Red"));

    RequestEntity<SKUInputDto> requestSKUCreation =
        RequestEntity.post("/users/test/products/" + productUuid + "/skus")
            .header("Authorization", "Bearer " + loginToken)
            .body(skuInputDto, SKUInputDto.class);

    return restTemplate.exchange(requestSKUCreation, SKUOutputDto.class);
  }

  @Test
  void createInventoryTest() {
    ResponseEntity<InventoryOutputDto> response = createInventory();
    assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    assertThat(response.getBody().getName(), equalTo("MyTestInventory"));
  }

  @Test
  void getInventory() {
    InventoryOutputDto createdInventory = createInventory().getBody();
    RequestEntity<Void> request =
        RequestEntity.get("/users/test/inventory/" + createdInventory.getId())
            .header("Authorization", "Bearer " + loginToken)
            .build();

    ResponseEntity<InventoryOutputDto> response = restTemplate.exchange(request, InventoryOutputDto.class);

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(response.getBody(), equalTo(createdInventory));
  }

  @Test
  void updateInventory() {
    InventoryOutputDto createdInventory = createInventory().getBody();

    InventoryInputDto inventoryInputDto = new InventoryInputDto().setName("UpdatedTestInventory");

    RequestEntity<InventoryInputDto> request =
        RequestEntity.put("/users/test/inventory/" + createdInventory.getId())
            .header("Authorization", "Bearer " + loginToken)
            .body(inventoryInputDto, InventoryInputDto.class);

    ResponseEntity<InventoryOutputDto> response = restTemplate.exchange(request, InventoryOutputDto.class);

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(response.getBody().getName(), equalTo(inventoryInputDto.getName()));
  }

  @Test
  void deleteInventory() {
    InventoryOutputDto createdInventory = createInventory().getBody();
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

    createOption(createdProduct.getId());

    SKUOutputDto createdSKU = createSKU(createdProduct.getId()).getBody();

    InventoryItemInputDto inventoryItemInputDto =
        new InventoryItemInputDto().setQuantity(99).setSkuId(createdSKU.getId());

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

    ProductOutputDto createdProduct = createProduct().getBody();

    SKUOutputDto createdSKU = createSKU(createdProduct.getId()).getBody();

    InventoryItemInputDto inventoryItemInputDto =
        new InventoryItemInputDto().setQuantity(99).setSkuId(createdSKU.getId());

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
