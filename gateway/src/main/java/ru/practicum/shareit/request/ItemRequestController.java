package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.helpers.HeaderKey;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                         @RequestBody @Valid  ItemRequestDto itemRequestDto) {
        log.info("Входящий запрос на создание запроса на вещь: " + itemRequestDto.toString());
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getByRequesterId(@RequestHeader(HeaderKey.USER_KEY) Long userId) {
        log.info("Получен запрос - получение информации по всем своим запросам на вещь для пользователя id: " + userId);
        return itemRequestClient.getByRequesterId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                          @PathVariable Long requestId) {
        log.info("Получен запрос - получение информации о запросе на вещь по переданному id: " + requestId);
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                         @RequestParam(required = false, defaultValue = "0")
                                         @Min(0) Integer from,
                                         @RequestParam(required = false, defaultValue = "10")
                                         @Min(1) Integer size) {
        log.info("Получен запрос - получение информации по всем запросам на вещь для пользователя id: " + userId);
        return itemRequestClient.getAll(userId, from, size);
    }
}