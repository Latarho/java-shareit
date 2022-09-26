package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto - отзыв, ответ на получение информации о вещи.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreatingDto {
    private String text;
}