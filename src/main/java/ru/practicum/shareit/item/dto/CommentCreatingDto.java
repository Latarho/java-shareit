package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Dto - отзыв, ответ на получение информации о вещи.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreatingDto {
    @NotBlank
    private String text;
}