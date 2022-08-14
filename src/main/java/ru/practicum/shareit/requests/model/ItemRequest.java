package ru.practicum.shareit.requests.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Модель - запрос вещи.
 */
@Data
public class ItemRequest {

    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}