package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) throws ValidationException {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto getById(Long id) throws UserNotFoundException {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + id);
        } else {
            return UserMapper.toUserDto(userRepository.findById(id).get());
        }
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> listUserDto = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            listUserDto.add(UserMapper.toUserDto(user));
        }
        return listUserDto;
    }

    @Override
    public UserDto update(Long id, UserDto userDto) throws EmailAlreadyExistsException {
        Optional<User> userFromList = userRepository.findById(id);
        userDto.setId(id);
        if (userDto.getName() == null) {
            userDto.setName(userFromList.orElseThrow().getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(userFromList.orElseThrow().getEmail());
        }
        for (User userToFind : userRepository.findAll()) {
            if ((!Objects.equals(userToFind.getId(), userDto.getId())) &&
                    userToFind.getEmail().equals(userDto.getEmail())) {
                throw new EmailAlreadyExistsException("Пользователь с email уже существует: " + userDto.getEmail());
            }
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + id);
        } else {
            userRepository.deleteById(id);
        }
    }
}