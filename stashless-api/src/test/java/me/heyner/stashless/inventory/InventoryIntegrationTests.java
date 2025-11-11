package me.heyner.stashless.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.heyner.stashless.dto.InventoryInputDto;
import me.heyner.stashless.dto.InventoryItemInputDto;
import me.heyner.stashless.dto.InventoryOutputDto;
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
import me.heyner.stashless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class InventoryIntegrationTests {

  @Autowired MockMvc mvc;

  @Autowired InventoryRepository inventoryRepository;

  @Autowired ProductRepository productRepository;

  @Autowired SKURepository skuRepository;

  @Autowired OptionRepository optionRepository;

  @Autowired UserRepository userRepository;

  @Autowired UserService userService;

  @Autowired ObjectMapper objectMapper = new ObjectMapper();

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

    userService.signUp(registerUserDto);
  }

  public InventoryOutputDto createInventory() throws Exception {
    InventoryInputDto inventoryInputDto = new InventoryInputDto().setName("MyTestInventory");

    String response =
        mvc.perform(
                post("/users/test/inventory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inventoryInputDto)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(response, InventoryOutputDto.class);
  }

  public ProductOutputDto createProduct() throws Exception {
    ProductInputDto productInputDto =
        new ProductInputDto()
            .setName("T-Shirt")
            .setBrand("Adidas")
            .setDescription("An adidas t-shirt");

    String response =
        mvc.perform(
                post("/users/test/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productInputDto)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(response, ProductOutputDto.class);
  }

  public OptionOutputDto createOption(UUID productUuid) throws Exception {
    OptionInputDto optionInputDto =
        new OptionInputDto().setName("Color").setValues(Set.of("Red", "Blue", "Orange"));

    String response =
        mvc.perform(
                post("/users/test/products/" + productUuid + "/options")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(optionInputDto)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(response, OptionOutputDto.class);
  }

  @Test
  @WithMockUser(username = "test")
  void createInventoryTest() throws Exception {

    InventoryOutputDto createdInventory = createInventory();

    assertNotNull(createdInventory);
    assertNotNull(createdInventory.getId());
    assertNotNull(createdInventory.getName());
    assertEquals("MyTestInventory", createdInventory.getName());
  }

  @Test
  @WithMockUser(username = "test")
  void getInventory() throws Exception {

    InventoryOutputDto createdInventory = createInventory();

    String response =
        mvc.perform(get("/users/test/inventory/" + createdInventory.getId()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    InventoryOutputDto fetched = objectMapper.readValue(response, InventoryOutputDto.class);
    assertNotNull(fetched);
    assertEquals(createdInventory.getId(), fetched.getId());
    assertEquals(createdInventory.getName(), fetched.getName());
  }

  @Test
  @WithMockUser(username = "test")
  void updateInventory() throws Exception {

    InventoryOutputDto createdInventory = createInventory();

    InventoryInputDto inventoryInputDto = new InventoryInputDto().setName("UpdatedTestInventory");

    String response =
        mvc.perform(
                put("/users/test/inventory/" + createdInventory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inventoryInputDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    InventoryOutputDto updated = objectMapper.readValue(response, InventoryOutputDto.class);
    assertNotNull(updated);
    assertEquals(inventoryInputDto.getName(), updated.getName());
    assertEquals(createdInventory.getId(), updated.getId());
  }

  @Test
  @WithMockUser(username = "test")
  void deleteInventory() throws Exception {

    InventoryOutputDto createdInventory = createInventory();

    mvc.perform(delete("/users/test/inventory/" + createdInventory.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "test")
  void addInventoryItem() throws Exception {

    InventoryOutputDto createdInventory = createInventory();

    ProductOutputDto createdProduct = createProduct();
    assertNotNull(createdProduct);

    OptionOutputDto createdOption = createOption(createdProduct.getId());
    assertNotNull(createdOption);
    assertNotNull(createdOption.getName());
    assertNotNull(createdOption.getValues());
    assertEquals(3, createdOption.getValues().size());

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

    mvc.perform(
            post("/users/test/inventory/" + createdInventory.getId() + "/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventoryItemInputDto)))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(username = "test")
  void deleteInventoryItem() throws Exception {

    InventoryOutputDto createdInventory = createInventory();

    ProductOutputDto createdProduct = createProduct();
    assertNotNull(createdProduct);

    OptionOutputDto createdOption = createOption(createdProduct.getId());
    assertNotNull(createdOption);

    assertNotNull(createdOption.getName());
    assertNotNull(createdOption.getValues());
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

    mvc.perform(
            post("/users/test/inventory/" + createdInventory.getId() + "/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventoryItemInputDto)))
        .andExpect(status().isCreated());

    mvc.perform(delete("/users/test/inventory/" + createdInventory.getId()))
        .andExpect(status().isNoContent());
  }
}
