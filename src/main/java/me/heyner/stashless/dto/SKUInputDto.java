package me.heyner.stashless.dto;

import java.math.BigDecimal;
import java.util.Map;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
public class SKUInputDto {
  private BigDecimal costPrice;
  private Long amountAvailable;
  private int marginPercentage;
  private String name;
  private Map<String, String> options;
}
