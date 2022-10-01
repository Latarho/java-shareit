package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dto - запрос вещи.
 */
@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotEmpty
    private String description;
    @FutureOrPresent
    private LocalDateTime created;
    private List<ItemDto> items;
}