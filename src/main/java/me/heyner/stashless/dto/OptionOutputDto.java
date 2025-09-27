package me.heyner.stashless.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class OptionOutputDto {
  @Nullable private UUID id;
  @Nullable private String name;
  @Nullable private Set<String> values;
  @Nullable private Date createdAt;
  @Nullable private Date updatedAt;
}
