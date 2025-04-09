package me.heyner.inventorypro.service;

import java.util.List;
import java.util.UUID;
import me.heyner.inventorypro.dto.InventoryInputDto;
import me.heyner.inventorypro.dto.InventoryItemOutputDto;
import me.heyner.inventorypro.dto.InventoryOutputDto;
import me.heyner.inventorypro.dto.OptionOutputDto;
import me.heyner.inventorypro.exception.EntityNotFoundException;
import me.heyner.inventorypro.exception.ExistingEntityException;
import me.heyner.inventorypro.model.Inventory;
import me.heyner.inventorypro.model.SKU;
import me.heyner.inventorypro.model.User;
import me.heyner.inventorypro.repository.InventoryRepository;
import me.heyner.inventorypro.repository.SKURepository;

import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

  private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

  private final InventoryRepository inventoryRepository;

  private final SKURepository skuRepository;

  private final UserService userService;

  private final ModelMapper modelMapper = new ModelMapper();

  public InventoryService(
      InventoryRepository inventoryRepository,
      SKURepository skuRepository,
      UserService userService) {
    this.inventoryRepository = inventoryRepository;
    this.skuRepository = skuRepository;
    this.userService = userService;
  }

  public InventoryOutputDto addInventory(String username, InventoryInputDto inventoryInputDto) {
    try {
      Inventory inventory = modelMapper.map(inventoryInputDto, Inventory.class);
      User user = userService.loadUserByUsername(username);
      inventory.setUser(user);
      Inventory savedInventory = inventoryRepository.save(inventory);
      logger.info("Inventory saved: {}", savedInventory);
      return modelMapper.map(savedInventory, InventoryOutputDto.class);
    } catch (DataIntegrityViolationException e) {
      logger.error("Inventory already exists: {}", e.getMessage());
      throw new ExistingEntityException(inventoryInputDto.getName());
    }

  }

  public InventoryOutputDto updateInventory(UUID uuid, InventoryInputDto inventoryInputDto)
      throws EntityNotFoundException {
    Inventory inventory =
        inventoryRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("not found"));
    inventory.setName(inventoryInputDto.getName());
    Inventory savedInventory = inventoryRepository.save(inventory);
    logger.info("Inventory updated: {}", inventory);
    return modelMapper.map(savedInventory, InventoryOutputDto.class);
  }

  public InventoryItemOutputDto addOrUpdateInventoryItem(UUID uuid, UUID skuUuid, Integer quantity) {
    Inventory inventory =
        inventoryRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("not found"));

    SKU sku =
        skuRepository.findById(skuUuid).orElseThrow(() -> new EntityNotFoundException("not found"));

    inventory.getItems().put(sku, quantity);

    Inventory savedInventory = inventoryRepository.save(inventory);
    logger.info("Item {} added to inventory {}", sku.getName(), inventory.getId());

    return savedInventory.getItems().entrySet().stream()
        .filter(entry -> entry.getKey().equals(sku))
        .map(
            entry -> {
              InventoryItemOutputDto inventoryItemOutputDto = new InventoryItemOutputDto();
              inventoryItemOutputDto.setSkuId(entry.getKey().getId());
              inventoryItemOutputDto.setSkuName(entry.getKey().getName());
              inventoryItemOutputDto.setBrand(entry.getKey().getProduct().getBrand());
              inventoryItemOutputDto.setCostPrice(entry.getKey().getCostPrice());
              inventoryItemOutputDto.setAmountAvailable(entry.getKey().getAmountAvailable());
              inventoryItemOutputDto.setMarginPercentage(entry.getKey().getMarginPercentage());
              inventoryItemOutputDto.setOption(
                  modelMapper.map(entry.getKey().getOption(), OptionOutputDto.class));
              inventoryItemOutputDto.setQuantity(entry.getValue());
              return inventoryItemOutputDto;
            })
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException("not found"));

    
  }

  public void deleteInventoryItem(UUID uuid, UUID skuUuid) {
    Inventory inventory =
        inventoryRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("not found"));

    SKU sku =
        skuRepository.findById(skuUuid).orElseThrow(() -> new EntityNotFoundException("not found"));

    var removedItem = inventory.getItems().remove(sku);

    if (removedItem == null) {
      throw new EntityNotFoundException("not found");
    }

    logger.info("Item {} removed from inventory {}", sku.getName(), inventory.getId());
  }

  public void deleteInventory(UUID uuid) {
    inventoryRepository.deleteById(uuid);
    logger.info("Inventory deleted: {}", uuid);
  }

  public InventoryOutputDto getInventory(UUID uuid) {

    return modelMapper.map(
      inventoryRepository
        .findById(uuid)
        .orElseThrow(() -> new EntityNotFoundException("Inventory not found")),
        InventoryOutputDto.class
    );
  }

  public List<InventoryOutputDto> getInventoriesByUsername(String username)
      throws UsernameNotFoundException {
    UserDetails user = userService.loadUserByUsername(username);
    List<Inventory> inventories = inventoryRepository.findByUser_username(username);
    logger.info("Getting {} inventories for user {}", inventories.size(), user);
    return inventories.stream().map(inventory -> modelMapper.map(inventory, InventoryOutputDto.class)).toList();
  }
}
