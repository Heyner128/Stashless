package me.heyner.stashless.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import me.heyner.stashless.dto.InventoryInputDto;
import me.heyner.stashless.dto.InventoryItemInputDto;
import me.heyner.stashless.dto.InventoryItemOutputDto;
import me.heyner.stashless.dto.InventoryOutputDto;
import me.heyner.stashless.dto.SKUInputDto;
import me.heyner.stashless.exception.EntityNotFoundException;
import me.heyner.stashless.exception.ExistingEntityException;
import me.heyner.stashless.model.Inventory;
import me.heyner.stashless.model.SKU;
import me.heyner.stashless.model.User;
import me.heyner.stashless.repository.InventoryRepository;
import me.heyner.stashless.repository.SKURepository;
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

  private final SKUService skuService;

  private final UserService userService;

  private final ModelMapper modelMapper = new ModelMapper();
  private final SKURepository sKURepository;

  public InventoryService(
      InventoryRepository inventoryRepository,
      SKUService skuService,
      UserService userService,
      SKURepository sKURepository) {
    this.inventoryRepository = inventoryRepository;
    this.skuService = skuService;
    this.userService = userService;

    this.sKURepository = sKURepository;
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
      String name = inventoryInputDto.getName();
      throw new ExistingEntityException(name != null ? name : "Unknown inventory");
    }
  }

  public InventoryOutputDto updateInventory(UUID uuid, InventoryInputDto inventoryInputDto)
      throws EntityNotFoundException {
    Inventory inventory =
        inventoryRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("not found"));
    if (inventoryInputDto.getName() != null) {
      inventory.setName(inventoryInputDto.getName());
    }
    if (inventoryInputDto.getDescription() != null) {
      inventory.setDescription(inventoryInputDto.getDescription());
    }
    Inventory savedInventory = inventoryRepository.save(inventory);
    logger.info("Inventory updated: {}", inventory);
    return modelMapper.map(savedInventory, InventoryOutputDto.class);
  }

  private InventoryItemOutputDto mapInventoryItem(Map.Entry<SKU, Integer> item) {
    InventoryItemOutputDto inventoryItemOutputDto = new InventoryItemOutputDto();
    inventoryItemOutputDto.setUuid(item.getKey().getId());
    if (item.getKey().getProduct() != null && item.getKey().getProduct().getId() != null) {
      inventoryItemOutputDto.setProductUuid(item.getKey().getProduct().getId());
    }
    inventoryItemOutputDto.setCostPrice(item.getKey().getCostPrice());
    inventoryItemOutputDto.setAmountAvailable(item.getKey().getAmountAvailable());
    inventoryItemOutputDto.setMarginPercentage(item.getKey().getMarginPercentage());
    inventoryItemOutputDto.setName(item.getKey().getName());
    if (!item.getKey().getOptions().isEmpty()) {
      inventoryItemOutputDto.setOptions(
          item.getKey().getOptions().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      e -> e.getKey().getName(),
                      e ->
                          e.getValue().getOptionValue() == null
                              ? "NO_VALUE"
                              : e.getValue().getOptionValue())));
    }

    inventoryItemOutputDto.setQuantity(item.getValue());
    return inventoryItemOutputDto;
  }

  public List<InventoryItemOutputDto> getInventoryItems(UUID uuid) {
    Inventory inventory =
        inventoryRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("not found"));

    List<InventoryItemOutputDto> items =
        inventory.getItems().entrySet().stream().map(this::mapInventoryItem).toList();

    logger.info("Retrieved {} items from inventory {}", items.size(), uuid);
    return items;
  }

  @Transactional
  public InventoryItemOutputDto addInventoryItem(
      UUID uuid, InventoryItemInputDto inventoryItemInputDto) {
    Inventory inventory =
        inventoryRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("not found"));

    SKUInputDto skuInputDto =
        new SKUInputDto()
            .setProductUuid(inventoryItemInputDto.getProductUuid())
            .setCostPrice(inventoryItemInputDto.getCostPrice())
            .setAmountAvailable(inventoryItemInputDto.getAmountAvailable())
            .setMarginPercentage(inventoryItemInputDto.getMarginPercentage())
            .setName(inventoryItemInputDto.getName())
            .setOptions(inventoryItemInputDto.getOptions());

    UUID productUuid = inventoryItemInputDto.getProductUuid();
    if (productUuid == null) {
      throw new IllegalArgumentException("Product UUID cannot be null");
    }
    SKU savedSku = skuService.saveSKU(productUuid, skuInputDto);

    inventory.getItems().put(savedSku, inventoryItemInputDto.getQuantity());

    logger.info("Item {} added to inventory {}", savedSku.getName(), inventory.getId());

    return inventory.getItems().entrySet().stream()
        .filter(
            entry ->
                entry.getKey().getId() != null && entry.getKey().getId().equals(savedSku.getId()))
        .findFirst()
        .map(this::mapInventoryItem)
        .orElseThrow(() -> new EntityNotFoundException("Item not found in inventory"));
  }

  @Transactional
  public InventoryItemOutputDto updateInventoryItem(
      UUID uuid, UUID itemUuid, InventoryItemInputDto inventoryItemInputDto) {
    Inventory inventory =
        inventoryRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("not found"));

    SKUInputDto skuInputDto =
        new SKUInputDto()
            .setProductUuid(inventoryItemInputDto.getProductUuid())
            .setCostPrice(inventoryItemInputDto.getCostPrice())
            .setAmountAvailable(inventoryItemInputDto.getAmountAvailable())
            .setMarginPercentage(inventoryItemInputDto.getMarginPercentage())
            .setName(inventoryItemInputDto.getName())
            .setOptions(inventoryItemInputDto.getOptions());

    SKU savedSku = skuService.updateSKU(itemUuid, skuInputDto);

    inventory.getItems().put(savedSku, inventoryItemInputDto.getQuantity());

    logger.info("Item {} added to inventory {}", savedSku.getName(), inventory.getId());

    return inventory.getItems().entrySet().stream()
        .filter(
            entry ->
                entry.getKey().getId() != null && entry.getKey().getId().equals(savedSku.getId()))
        .findFirst()
        .map(this::mapInventoryItem)
        .orElseThrow(() -> new EntityNotFoundException("Item not found in inventory"));
  }

  @Transactional
  public void deleteInventoryItem(UUID uuid, UUID skuUuid) {
    Inventory inventory =
        inventoryRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("not found"));

    SKU sku = skuService.getSKU(skuUuid);

    if (inventory.getItems().containsKey(sku)) {
      inventory.getItems().remove(sku);
    } else {
      throw new EntityNotFoundException("not found");
    }

    sKURepository.delete(sku);

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
        InventoryOutputDto.class);
  }

  public List<InventoryOutputDto> getInventoriesByUsername(String username)
      throws UsernameNotFoundException {
    UserDetails user = userService.loadUserByUsername(username);
    List<Inventory> inventories = inventoryRepository.findByUser_username(username);
    logger.info("Getting {} inventories for user {}", inventories.size(), user);
    return inventories.stream()
        .map(inventory -> modelMapper.map(inventory, InventoryOutputDto.class))
        .toList();
  }
}
