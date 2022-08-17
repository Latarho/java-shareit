package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Dto - пользователь.
 */
@Data
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotEmpty(message = "Поле name не может быть пустым.")
    private String name;

    @Email(message = "В поле email передан невалидный почтовый адрес.")
    @NotEmpty(message = "Поле email не может быть пустым.")
    private String email;
}