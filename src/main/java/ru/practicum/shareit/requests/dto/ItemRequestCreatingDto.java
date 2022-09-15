package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * Dto - создание запроса вещи.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreatingDto {
    @NotEmpty
    private String description;
}