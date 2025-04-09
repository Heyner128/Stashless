package me.heyner.inventorypro.dto;

import java.util.List;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
public class OptionInputDto {
  private String name;
  private List<?> values;
}
