package me.heyner.stashless.dto;

import java.util.Set;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
public class OptionInputDto {
  private String name;
  private Set<String> values;
}
