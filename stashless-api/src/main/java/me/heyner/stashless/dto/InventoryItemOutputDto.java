package me.heyner.stashless.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class InventoryItemOutputDto {
  @Nullable private UUID uuid;
  @Nullable private UUID productUuid;
  @Nullable private BigDecimal costPrice;
  @Nullable private Long amountAvailable;
  private int marginPercentage;
  @Nullable private String name;
  @Nullable private Map<String, String> options;
  private int quantity;
}
