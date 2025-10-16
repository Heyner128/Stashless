package me.heyner.stashless.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class InventoryInputDto {
  @Nullable
  @NotBlank(message = "The name can't be empty")
  @Size(min = 4, message = "The minimum name length is 4 characters")
  private String name;

  @Nullable private String description;
}
