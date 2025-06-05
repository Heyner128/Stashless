package me.heyner.stashless.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
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
public class SKUOutputDto {
    private UUID id;
    private String name;
    private BigDecimal costPrice;
    private Long amountAvailable;
    private int marginPercentage;
    private Map<String, String> options;
    private Date createdAt;
    private Date updatedAt;
}
