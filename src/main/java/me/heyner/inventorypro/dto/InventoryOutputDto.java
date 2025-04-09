package me.heyner.inventorypro.dto;

import java.util.Date;
import java.util.List;
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
public class InventoryOutputDto {
    private UUID id;
    private String name;
    private String description;
    private List<InventoryItemOutputDto> items;
    private UserDto user;
    private Date createdAt;
    private Date updatedAt;
}
