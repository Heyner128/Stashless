package me.heyner.stashless.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import me.heyner.stashless.dto.SKUInputDto;
import me.heyner.stashless.dto.SKUOutputDto;
import me.heyner.stashless.exception.EntityNotFoundException;
import me.heyner.stashless.model.*;
import me.heyner.stashless.repository.OptionRepository;
import me.heyner.stashless.repository.ProductRepository;
import me.heyner.stashless.repository.SKURepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SKUService {

  private final SKURepository skuRepository;
  private final ProductRepository productRepository;
  private final OptionRepository optionRepository;
  Logger logger = LoggerFactory.getLogger(SKUService.class);
  ModelMapper modelMapper = new ModelMapper();

  public SKUService(
      SKURepository skuRepository,
      OptionRepository optionRepository,
      ProductRepository productRepository) {
    this.skuRepository = skuRepository;
    this.optionRepository = optionRepository;
    this.productRepository = productRepository;
    modelMapper
      .typeMap(SKU.class, SKUOutputDto.class)
      .addMappings(
        mapper ->
          mapper
            .using(
              ctx ->
                ((Map<Option, OptionValue>) ctx.getSource())
                  .entrySet()
                  .stream()
                  .collect(
                    Collectors.toMap(
                      e -> e.getKey().getName(),
                      e -> e.getValue().getValue()
                    )
                  )
            )
            .map(SKU::getOptions, SKUOutputDto::setOptions));
  }

  private SKU joinProduct(SKU sku, UUID productUuid) {
    Product product =
        productRepository
          .findById(productUuid)
          .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    return sku.setProduct(product);
  }

  private SKU joinOptions(SKU sku, UUID productId, Map<String, String> optionValueNames) {
    Map<Option, OptionValue> options = new HashMap<>();

    for (Entry<String, String> entry : optionValueNames.entrySet()) {
      Option option =
          optionRepository
            .findByNameAndProductId(productId, entry.getKey())
            .orElseThrow(() -> new EntityNotFoundException("Option not found"));

      OptionValue optionValue =
          option.getValues().stream()
            .filter(ov -> ov.getValue().equals(entry.getValue()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Option value not found"));

      options.put(option, optionValue);
    }
    return sku.setOptions(options);
  }


  public SKUOutputDto addSKU(UUID productUuid, SKUInputDto skuDto) throws EntityNotFoundException {

    SKU sku = modelMapper.map(skuDto, SKU.class);
    sku = joinProduct(sku, productUuid);
    sku = joinOptions(sku, productUuid, skuDto.getOptions());

    logger.info("Creating SKU: {}", sku);

    sku = skuRepository.save(sku);

    logger.info("SKU created: {}", sku);
    
    return modelMapper.map(sku, SKUOutputDto.class);
  }


  public List<SKUOutputDto> getSkus(UUID productUuid) throws EntityNotFoundException {
    List<SKU> skus = skuRepository.findByProductId(productUuid);
    logger.info("SKUs {} found", skus.size());
    return skus.stream().map(sku -> modelMapper.map(sku, SKUOutputDto.class)).toList();
  }


  public List<SKUOutputDto> updateSkus(UUID productUuid, List<SKUInputDto> skuDtos)
      throws EntityNotFoundException {
    List<SKU> skus = skuRepository.findByProductId(productUuid);

    for(SKU sku: skus) {
      skuRepository.delete(sku);
    }

    List<SKU> savedSkus = new ArrayList<>();

    for(SKUInputDto skuDto: skuDtos) {

      SKU newSku = modelMapper.map(skuDto, SKU.class);
      newSku = joinProduct(newSku, productUuid);
      newSku = joinOptions(newSku, productUuid, skuDto.getOptions());
      skuRepository.save(newSku);
      logger.info("SKU updated: {}", newSku);
      savedSkus.add(newSku);
    }

    return savedSkus.stream()
        .map(sku -> modelMapper.map(sku, SKUOutputDto.class))
        .toList();
  }
}
