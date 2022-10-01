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

    /**
     * Создание нового пользователя
     * @param userDto объект класса User
     * @return объект класса User (новый пользователь)
     */
    @Override
    public UserDto create(UserDto userDto) throws ValidationException {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    /**
     * Получение пользователя по переданному id
     * @param id идентификатор пользователя
     * @return объект класса User соответствующий переданному id
     */
    @Override
    public UserDto getById(Long id) throws UserNotFoundException {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + id);
        } else {
            return UserMapper.toUserDto(userRepository.findById(id).get());
        }
    }

    /**
     * Получение списка пользователей
     * @return список пользователей
     */
    @Override
    public List<UserDto> getAll() {
        List<UserDto> listUserDto = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            listUserDto.add(UserMapper.toUserDto(user));
        }
        return listUserDto;
    }

    /**
     * Обновление существующего пользователя
     * @param id идентификатор пользователя
     * @param userDto объект класса User
     * @return объект класса User с обновленными данными
     */
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

    /**
     * Удаление существующего пользователя
     * @param id идентификатор пользователя
     */
    @Override
    public void delete(Long id) throws UserNotFoundException {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + id);
        } else {
            userRepository.deleteById(id);
        }
    }
}