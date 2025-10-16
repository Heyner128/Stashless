package me.heyner.stashless.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class InventoryOutputDto {
  @Nullable private UUID id;
  @Nullable private String name;
  @Nullable private String description;
  @Nullable private List<InventoryItemOutputDto> items;
  @Nullable private UserDto user;
  @Nullable private Date createdAt;
  @Nullable private Date updatedAt;
}
