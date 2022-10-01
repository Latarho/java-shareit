package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Dto - создание вещи.
 */
@Data
@AllArgsConstructor
public class ItemCreatingDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}