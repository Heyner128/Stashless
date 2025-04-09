package me.heyner.inventorypro.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
public class SKUInputDto {

  String optionValue;
  private BigDecimal costPrice;
  private Long amountAvailable;
  private int marginPercentage;
  private String name;
  private UUID optionUUID;
}
