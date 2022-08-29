package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получение списка пользователей.
     * @return список пользователей.
     */
    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получен запрос - получение списка пользователей");
        return userService.getAll();
    }

    /**
     * Получение пользователя по переданному id.
     * @param id идентификатор User.
     * @return объект класса User соответствующий переданному id.
     */
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Получен запрос - получение пользователя по переданному id: " + id);
        return userService.getById(id);
    }

    /**
     * Обновление существующего пользователя.
     * @param id идентификатор User.
     * @param userDto объект класса User.
     * @return объект класса User с обновленными данными.
     */
    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос - обновление существующего пользователя id: " + id);
        return userService.update(id, userDto);
    }

    /**
     * Создание нового пользователя.
     * @param userDto объект класса User.
     * @return объект класса User (новый пользователь).
     */
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос - создание пользователя: " + userDto.toString());
        return userService.create(userDto);
    }

    /**
     * Удаление существующего пользователя.
     * @param id идентификатор User.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос - удаление существующего пользователя id: " + id);
        userService.delete(id);
    }
}