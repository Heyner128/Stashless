package me.heyner.stashless.dto;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
public class InventoryItemOutputDto {
  private UUID uuid;
  private UUID productUuid;
  private BigDecimal costPrice;
  private Long amountAvailable;
  private int marginPercentage;
  private String name;
  private Map<String, String> options;
  private int quantity;
}
