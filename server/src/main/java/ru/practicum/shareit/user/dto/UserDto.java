package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Dto - пользователь.
 */
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}