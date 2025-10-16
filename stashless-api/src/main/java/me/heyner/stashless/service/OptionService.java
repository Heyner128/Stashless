package me.heyner.stashless.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import me.heyner.stashless.dto.OptionInputDto;
import me.heyner.stashless.dto.OptionOutputDto;
import me.heyner.stashless.exception.EntityNotFoundException;
import me.heyner.stashless.model.Option;
import me.heyner.stashless.model.OptionValue;
import me.heyner.stashless.repository.OptionRepository;
import me.heyner.stashless.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OptionService {

  private static final Logger logger = LoggerFactory.getLogger(OptionService.class);

  private final ProductRepository productRepository;

  private final OptionRepository optionRepository;

  private final ModelMapper modelMapper = new ModelMapper();

  public OptionService(ProductRepository productRepository, OptionRepository optionRepository) {
    this.productRepository = productRepository;
    this.optionRepository = optionRepository;
    modelMapper
        .typeMap(Option.class, OptionOutputDto.class)
        .addMappings(
            mapper ->
                mapper
                    .using(
                        ctx ->
                            ((Set<OptionValue>) ctx.getSource())
                                .stream().map(OptionValue::getValue).collect(Collectors.toSet()))
                    .map(Option::getValues, OptionOutputDto::setValues));

    modelMapper
        .typeMap(OptionInputDto.class, Option.class)
        .addMappings(
            mapper ->
                mapper
                    .using(
                        ctx ->
                            ((Set<String>) ctx.getSource())
                                .stream()
                                    .map(value -> new OptionValue().setValue(value))
                                    .collect(Collectors.toSet()))
                    .map(OptionInputDto::getValues, Option::setValues));
  }

  public OptionOutputDto addOption(UUID productUuid, OptionInputDto optionDto)
      throws EntityNotFoundException {

    Option option =
        modelMapper
            .map(optionDto, Option.class)
            .setProduct(
                productRepository
                    .findById(productUuid)
                    .orElseThrow(() -> new EntityNotFoundException("Not found")));

    option = optionRepository.save(option);

    logger.info("Adding option {} to product {}", option.getName(), productUuid);

    return modelMapper.map(option, OptionOutputDto.class);
  }

  public List<OptionOutputDto> getOptions(UUID productUuid) throws EntityNotFoundException {
    List<Option> options = optionRepository.findByProductId(productUuid);
    logger.info("Getting {} options for product {}", options.size(), productUuid);
    return options.stream().map(opt -> modelMapper.map(opt, OptionOutputDto.class)).toList();
  }

  public List<OptionOutputDto> updateOptions(UUID productUuid, List<OptionInputDto> optionsDto)
      throws EntityNotFoundException {

    List<Option> currentOptions = optionRepository.findByProductId(productUuid);

    for (Option option : currentOptions) {
      optionRepository.delete(option);
    }

    List<Option> savedOptions = new ArrayList<>();

    for (OptionInputDto optionDto : optionsDto) {
      Option option = modelMapper.map(optionDto, Option.class);
      option.setProduct(
          productRepository
              .findById(productUuid)
              .orElseThrow(() -> new EntityNotFoundException("Not found")));
      savedOptions.add(optionRepository.save(option));
    }

    return savedOptions.stream().map(opt -> modelMapper.map(opt, OptionOutputDto.class)).toList();
  }
}
