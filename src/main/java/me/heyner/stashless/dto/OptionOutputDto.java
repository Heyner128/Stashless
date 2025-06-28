package me.heyner.stashless.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;
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
public class OptionOutputDto {
    private UUID id;
    private String name;
    private Set<String> values;
    private Date createdAt;
    private Date updatedAt;
}
