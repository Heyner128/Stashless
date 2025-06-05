package me.heyner.stashless.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import me.heyner.stashless.dto.ProductInputDto;
import me.heyner.stashless.dto.ProductOutputDto;
import me.heyner.stashless.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT token")
@Tag(name = "Products")
@RestController
@RequestMapping("/users/{username}/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public List<ProductOutputDto> getAllProducts(@PathVariable String username) {
    return productService.getProducts(username);
  }

  @GetMapping("/{productUuid}")
  public ProductOutputDto getProduct(@PathVariable UUID productUuid) {
    return productService.getProduct(productUuid);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductOutputDto createProduct(
      @RequestBody @Valid ProductInputDto productDto, @PathVariable String username) {
    return productService.createProduct(username, productDto);
  }

  @PutMapping("/{productUuid}")
  public ProductOutputDto updateProduct(
      @PathVariable UUID productUuid, @RequestBody @Valid ProductInputDto productDto) {
    return productService.updateProduct(productUuid, productDto);
  }

  @DeleteMapping("/{productUuid}")
  public void deleteProduct(@PathVariable UUID productUuid) {
    productService.deleteProduct(productUuid);
  }
}
