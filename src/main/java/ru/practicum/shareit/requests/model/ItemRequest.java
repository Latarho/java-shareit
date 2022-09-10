package ru.practicum.shareit.requests.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Модель - запрос вещи.
 */
@Data
public class ItemRequest {

    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
}