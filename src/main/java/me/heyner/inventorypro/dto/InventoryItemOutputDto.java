package me.heyner.inventorypro.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
public class InventoryItemOutputDto {
  private UUID skuId;
  private String skuName;
  private String brand;
  private BigDecimal costPrice;
  private Long amountAvailable;
  private int marginPercentage;
  private OptionOutputDto option;
  private int quantity;
}
