package me.heyner.inventorypro.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import me.heyner.inventorypro.dto.OptionInputDto;
import me.heyner.inventorypro.dto.OptionOutputDto;
import me.heyner.inventorypro.model.Option;
import me.heyner.inventorypro.service.OptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT token")
@Tag(name = "Options")
@RestController
@RequestMapping("/users/{username}/products/{productUuid}/options")
public class OptionController {

  private final OptionService optionService;

  public OptionController(OptionService optionService) {
    this.optionService = optionService;
  }

  @GetMapping
  public List<OptionOutputDto> getProductOptions(@PathVariable UUID productUuid) {
    return optionService.getOptions(productUuid);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OptionOutputDto createOption(
      @PathVariable UUID productUuid, @RequestBody @Valid OptionInputDto optionDto) {
    return optionService.addOption(productUuid, optionDto);
  }

  @PutMapping()
  public List<OptionOutputDto> updateOptions(
      @PathVariable UUID productUuid, @RequestBody @Valid List<OptionInputDto> optionDtos) {
    return optionService.updateOptions(productUuid, optionDtos);
  }
}
