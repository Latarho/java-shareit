package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Dto - вещь.
 */
@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @JsonProperty(value = "available", required = true)
    @NotNull
    private Boolean available;

    private Long ownerId;

    private Long requestId;
}