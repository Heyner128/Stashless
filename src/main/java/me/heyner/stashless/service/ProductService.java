package me.heyner.stashless.service;

import java.util.List;
import java.util.UUID;
import me.heyner.stashless.dto.ProductInputDto;
import me.heyner.stashless.dto.ProductOutputDto;
import me.heyner.stashless.exception.EntityNotFoundException;
import me.heyner.stashless.model.Option;
import me.heyner.stashless.model.Product;
import me.heyner.stashless.model.SKU;
import me.heyner.stashless.model.User;
import me.heyner.stashless.repository.OptionRepository;
import me.heyner.stashless.repository.ProductRepository;
import me.heyner.stashless.repository.SKURepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

  private final ProductRepository productRepository;

  private final OptionRepository optionRepository;

  private final SKURepository skuRepository;

  private final UserService userService;

  private final ModelMapper modelMapper = new ModelMapper();

  public ProductService(
      ProductRepository productRepository,
      OptionRepository optionRepository,
      SKURepository skuRepository,
      UserService userService) {
    this.productRepository = productRepository;
    this.optionRepository = optionRepository;
    this.skuRepository = skuRepository;
    this.userService = userService;
  }

  public List<ProductOutputDto> getProducts(String username) throws EntityNotFoundException {
    try {
      List<Product> products = productRepository.findByUser_username(username);
      logger.info("{} products found for user {}", products.size(), username);
      return products.stream()
          .map(product -> modelMapper.map(product, ProductOutputDto.class))
          .toList();
    } catch (RuntimeException ex) {
      logger.error(ex.getMessage(), ex);
      throw new EntityNotFoundException("Not found");
    }
  }

  public ProductOutputDto createProduct(String username, ProductInputDto productDto)
      throws EntityNotFoundException {
    Product product = modelMapper.map(productDto, Product.class);
    User user = userService.loadUserByUsername(username);
    product.setUser(user);
    Product savedProduct = productRepository.save(product);
    logger.info("{} Product successfully created", product.getName());
    return modelMapper.map(savedProduct, ProductOutputDto.class);
  }

  public ProductOutputDto getProduct(UUID uuid) throws EntityNotFoundException {
    Product product =
        productRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("Not found"));
    logger.info("Product {} found", product.getName());
    return modelMapper.map(product, ProductOutputDto.class);
  }

  public ProductOutputDto updateProduct(UUID uuid, ProductInputDto productDto)
      throws EntityNotFoundException {
    Product productToUpdate =
        productRepository
            .findById(uuid)
            .orElseThrow(() -> new EntityNotFoundException("Not found"));
    if (productDto.getName() != null) {
      productToUpdate.setName(productDto.getName());
    }
    if (productDto.getDescription() != null) {
      productToUpdate.setDescription(productDto.getDescription());
    }
    if (productDto.getBrand() != null) {
      productToUpdate.setBrand(productDto.getBrand());
    }
    Product savedProduct = productRepository.save(productToUpdate);
    logger.info("{} Product successfully updated", productToUpdate.getName());
    return modelMapper.map(savedProduct, ProductOutputDto.class);
  }

  public void deleteProduct(UUID uuid) throws EntityNotFoundException {

    List<Option> options = optionRepository.findByProductId(uuid);
    List<SKU> skus = skuRepository.findByProductId(uuid);
    for (SKU sku : skus) {
      skuRepository.delete(sku);
      logger.info("SKU {} deleted", sku.getId());
    }
    for (Option option : options) {
      optionRepository.delete(option);
      logger.info("Option {} deleted", option.getId());
    }
    productRepository.deleteById(uuid);
    logger.info("{} Product successfully deleted", uuid);
  }
}
