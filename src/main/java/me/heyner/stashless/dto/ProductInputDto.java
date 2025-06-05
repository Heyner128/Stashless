package me.heyner.stashless.dto;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
public class ProductInputDto {
  private String name;

  private String description;

  private String brand;
}
