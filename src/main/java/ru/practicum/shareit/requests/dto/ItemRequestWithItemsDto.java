package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dto - отображение информации о созданном запросе на вещь с вещами
 */
@Data
@AllArgsConstructor
public class ItemRequestWithItemsDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}