package me.heyner.stashless.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
public class InventoryInputDto {
  @NotBlank(message = "The name can't be empty")
  @Size(min = 4, message = "The minimum name length is 4 characters")
  private String name;

  private String description;
}
