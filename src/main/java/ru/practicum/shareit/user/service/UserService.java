package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto) throws ValidationException;
    UserDto getById(Long id) throws ValidationException, UserNotFoundException;
    List<UserDto> getAll();
    UserDto update(Long id, UserDto userDto) throws EmailAlreadyExistsException;
    void delete(Long id) throws UserNotFoundException;
}