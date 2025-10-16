package me.heyner.stashless.dto;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class OptionInputDto {
  @Nullable private String name;
  @Nullable private Set<String> values;
}
