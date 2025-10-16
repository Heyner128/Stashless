package me.heyner.stashless.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ProductInputDto {
  @Nullable private String name;

  @Nullable private String description;

  @Nullable private String brand;
}
