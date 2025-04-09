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
public class ProductOutputDto {
    private UUID id;
    private String name;
    private String description;
    private String brand;
    private UserDto user;
    private List<OptionOutputDto> options;
    private List<SKUOutputDto> skus;
    private Date createdAt;
    private Date updatedAt;
}
