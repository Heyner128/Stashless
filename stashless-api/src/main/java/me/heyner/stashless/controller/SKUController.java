package me.heyner.stashless.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import me.heyner.stashless.dto.SKUInputDto;
import me.heyner.stashless.dto.SKUOutputDto;
import me.heyner.stashless.service.SKUService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "JWT token")
@Tag(name = "SKU")
@RestController
@RequestMapping("/users/{username}/products/{productUuid}/skus")
public class SKUController {

  private final SKUService skuService;

  public SKUController(SKUService skuService) {
    this.skuService = skuService;
  }

  @GetMapping
  public List<SKUOutputDto> getSKUs(@PathVariable UUID productUuid) {
    return skuService.getSkus(productUuid);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SKUOutputDto createSKU(
      @PathVariable UUID productUuid, @Valid @RequestBody SKUInputDto skuDto) {
    return skuService.saveAndMapSKU(productUuid, skuDto);
  }

  @PutMapping
  public List<SKUOutputDto> updateSKUs(
      @PathVariable UUID productUuid, @RequestBody List<SKUInputDto> skuDtos) {
    return skuService.updateSkus(productUuid, skuDtos);
  }
}
