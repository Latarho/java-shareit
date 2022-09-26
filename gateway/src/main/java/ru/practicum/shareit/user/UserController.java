package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    /**
     * Создание нового пользователя.
     * @param userDto объект класса User.
     * @return объект класса User (новый пользователь).
     */
    @PostMapping
    public Object create(@RequestBody @Validated UserDto userDto) {
        log.info("Получен запрос - создание пользователя: " + userDto.toString());
        return userClient.create(userDto);
    }

    /**
     * Обновление существующего пользователя.
     * @param id идентификатор User.
     * @param userDto объект класса User.
     * @return объект класса User с обновленными данными.
     */
    @PatchMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос - обновление существующего пользователя id: " + id);
        return userClient.update(id, userDto);
    }

    /**
     * Получение пользователя по переданному id.
     * @param id идентификатор User.
     * @return объект класса User соответствующий переданному id.
     */
    @GetMapping("/{id}")
    public Object getById(@PathVariable Long id) {
        log.info("Получен запрос - получение пользователя по переданному id: " + id);
        return userClient.getById(id);
    }

    /**
     * Получение списка пользователей.
     * @return список пользователей.
     */
    @GetMapping
    public Object getAll() {
        log.info("Получен запрос - получение списка пользователей");
        return userClient.getAll();
    }

    /**
     * Удаление существующего пользователя.
     * @param id идентификатор User.
     */
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id) {
        log.info("Получен запрос - удаление существующего пользователя id: " + id);
        return userClient.delete(id);
    }
}