package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        validateUser(userDto);
        User newUser = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.create(newUser));
    }

    @Override
    public UserDto getById(Long id) {
        if (userStorage.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсутствует пользователь id: " + id);
        }
        return UserMapper.toUserDto(userStorage.getById(id));
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> allUsers = new ArrayList<>();
        for (User userFromStorage : userStorage.getAll()) {
            allUsers.add(UserMapper.toUserDto(userFromStorage));
        }
        return allUsers;
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        if (userStorage.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсутствует пользователь id: " + id);
        }
        validateUser(userDto);
        User newUser = UserMapper.toUser(userDto);
        User updateUser = userStorage.getById(id);
        if (newUser.getName() != null) {
            updateUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            updateUser.setEmail(newUser.getEmail());
        }
        return UserMapper.toUserDto(userStorage.update(updateUser));
    }

    @Override
    public void delete(Long id) {
        if (userStorage.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсутствует пользователь id: " + id);
        }
        userStorage.delete(id);
    }

    private void validateUser(UserDto userDto) {
        List<UserDto> allUsers = getAll();
        for (UserDto userDtoFromStorage : allUsers) {
            if (!Objects.equals(userDtoFromStorage.getId(), userDto.getId())
                    && userDtoFromStorage.getEmail().equals(userDto.getEmail())) {
                throw new EmailAlreadyExistsException("Пользователь с таким email уже существует.");
            }
        }
    }
}