package me.heyner.stashless.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import me.heyner.stashless.dto.InventoryInputDto;
import me.heyner.stashless.dto.InventoryOutputDto;
import me.heyner.stashless.model.Authority;
import me.heyner.stashless.model.Inventory;
import me.heyner.stashless.model.User;
import me.heyner.stashless.repository.InventoryRepository;
import me.heyner.stashless.service.InventoryService;
import me.heyner.stashless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class InventoryServiceTests {
  private final InventoryService inventoryService;
  private final ModelMapper modelMapper = new ModelMapper();
  @MockitoBean private InventoryRepository inventoryRepository;
  @MockitoBean private UserService userService;
  private User user;
  private Inventory inventory;

  @Autowired
  public InventoryServiceTests(
      InventoryService inventoryService,
      InventoryRepository inventoryRepository,
      UserService userService) {
    this.inventoryService = inventoryService;
    this.inventoryRepository = inventoryRepository;
    this.userService = userService;
  }

  public void setUpMockUser() {
    user = new User();
    user.setId(1L);
    user.setUsername("test");
    user.setPassword("teeeST1@");
    user.setEmail("test@test.com");
    user.setAuthorities(Set.of(Authority.USER));
  }

  public void setupMockInventory() {
    inventory = new Inventory();
    inventory.setId(UUID.randomUUID());
    inventory.setName("My inventory");
    inventory.setUser(user);
  }

  public void setUpMockBeans() {
    when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);
    when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
    when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
    when(inventoryRepository.findByUser_username(user.getUsername()))
        .thenReturn(List.of(inventory));
  }

  @BeforeEach
  void setUp() {
    setUpMockUser();
    setupMockInventory();
    setUpMockBeans();
  }

  @Test
  public void testAddInventory() {
    InventoryInputDto inventoryInputDto = modelMapper.map(inventory, InventoryInputDto.class);
    InventoryOutputDto newInventory = inventoryService.addInventory(user.getUsername(), inventoryInputDto);
    assertEquals(newInventory, modelMapper.map(inventory, InventoryOutputDto.class));
  }

  @Test
  public void testUpdateInventory() {
    InventoryInputDto inventoryInputDto = modelMapper.map(inventory, InventoryInputDto.class);
    InventoryOutputDto savedInventory =
        inventoryService.updateInventory(inventory.getId(), inventoryInputDto);
    assertEquals(inventory.getName(), savedInventory.getName());
  }

  @Test
  public void testGetInventory() {
    InventoryOutputDto gottenInventory = inventoryService.getInventory(inventory.getId());
    assertEquals(
      gottenInventory, modelMapper.map(inventory, InventoryOutputDto.class)
    );
  }

  @Test
  public void testGetInventoriesByUsername() {
    List<InventoryOutputDto> inventoriesDto = inventoryService.getInventoriesByUsername(user.getUsername());
    assertEquals(inventoriesDto.getFirst(), modelMapper.map(inventory, InventoryOutputDto.class));
  }
}
